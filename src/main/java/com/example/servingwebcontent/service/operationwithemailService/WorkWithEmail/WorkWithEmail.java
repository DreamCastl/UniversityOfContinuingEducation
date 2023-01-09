package com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail;

import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.models.operationwithemailService.Status;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Getter
public class WorkWithEmail {
    private final EmailProperties auth;
    private final String sendInboxName;
    private Folder sendInbox;
    private final String readerFolderName;
    private Folder readInbox;

    private JavaMailSenderImpl emailSender;

    private StatusRepository statusRepository;

    private static final Logger logger = LogManager.getLogger();

    public WorkWithEmail(EmailProperties emailProperties,StatusRepository statRepo) {
        AddImapProperties(emailProperties);
        auth = emailProperties;
        sendInboxName = "Отправленные";//emailProperties.getGetSendingInbox();
        sendInbox = getFolder(sendInboxName);
        readerFolderName = emailProperties.getGetReadInbox();
        readInbox = getFolder(readerFolderName);
        statusRepository = statRepo;
        emailSender = new JavaMailSenderImpl();
        emailSender.setHost(emailProperties.getSMTP_Server());
        emailSender.setPort(Integer.parseInt(emailProperties.getSMTP_Port()));
        emailSender.setUsername(emailProperties.getLogin());
        emailSender.setPassword(emailProperties.getPassword());
        AddProperties(emailSender.getJavaMailProperties());

    }

    public void sendMessage(RequestForTraining currentRequest) {

        if (! sendInbox.isOpen()) {
            logger.info("Папка исходящих закрыта");
        }
        sendMessageWithAttachment(currentRequest);
    }

    private void AddImapProperties(EmailProperties auth){
        Properties properties = new Properties();
        properties.put("mail.debug", "false");
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.port", auth.getIMAP_Port());
        auth.setIMAP_properties(properties);
    }

    public Message[] ReadMessage() {

        Message[] messages = new Message[0];
        if (!readInbox.isOpen()) {
            logger.info("Папка закрыта");
            readInbox = getFolder(readerFolderName);
        }

        try {
        //    logger.info("Попытка получения почты");

            messages = readInbox.search(new SearchTerm() {
                @Override
                public boolean match(Message msg) {
                    try {
                        return !msg.isSet(Flags.Flag.SEEN) && ((msg.getSubject().equals("Создание заявки на цикл")));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        //logger.warn("фильтр не сработал");
                        return false;
                    }
                }
            });
            return messages;
        } catch (MessagingException e) {
            e.printStackTrace();
            //logger.error(e.getMessage());
            //logger.warn("Получение писем не удалось");
        }
        return messages;
    }

    public String GetContentMail(Message Message) throws MessagingException, IOException {
        logger.info("Получение контента из письма");
        return ParserData.getTextFromMessage(Message);
    }

    public Folder getFolder(String FolderName) {

        Session session = Session.getDefaultInstance(auth.getIMAP_properties(), auth);

        try {
            logger.info("Открытие сессии и папки");
            Store store = session.getStore();
            store.connect(auth.getIMAP_Server(), auth.getLogin(), auth.getPassword()); // Подключение к почтовому серверу
            Folder inbox = store.getFolder(FolderName); // Папка входящих сообщений
            inbox.open(Folder.READ_WRITE); // Открываем папку в режиме только для чтения
            return inbox;
        } catch (MessagingException e) {
            logger.info("Ошибка открытия папки");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void SetFlagSeen(Message message,boolean flag) {
        try {
            message.setFlag(Flags.Flag.SEEN,flag);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void AddProperties(Properties props){
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
    }

    public boolean sendMessageWithAttachment(RequestForTraining currentRequest) {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            helper.setFrom(emailSender.getUsername());
            helper.setTo(currentRequest.getClient().getEmail());
            helper.setBcc("shestopalova-nv@mail.ru");
            helper.setSubject("Заявка на обучение "+currentRequest.getNumberRequest());
            String htmlLetter = GetTextLetterFromHTML(getHTMLLetter(currentRequest.getPayer()));
            htmlLetter = htmlLetter.replace("$$login$$",currentRequest.getNumberRequest());
            htmlLetter = htmlLetter.replace("$$key$$",currentRequest.getRequestKey());
            helper.setText(htmlLetter,true);

       //     String pathToAttachment = getPathContract(currentRequest.getPayer());
       //     FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
       //     helper.addAttachment(pathToAttachment, file);

            Status status =  statusRepository.FindByKey("FailedToSendEmail");

            emailSender.send(message);
            logger.info("Письмо отправлено");

            status = statusRepository.FindByKey("LetterSent");
            currentRequest.setStatus(status);

            try {
                Message[] massMessage = new Message[1];
                message.setFlag(Flags.Flag.SEEN, true);
                massMessage[0] = message;
                sendInbox.appendMessages(massMessage);
                logger.info("Письмо сохранено");
                status = statusRepository.FindByKey("LetterSentAndSave");
                currentRequest.setStatus(status);
            } catch (Exception e ) {
                status = statusRepository.FindByKey("LetterSentButNotSave");
                currentRequest.setStatus(status);
            }
            return true;
        } catch (Exception e ) {

            try {
                Message[] massMessage = new Message[1];
                message.setFlag(Flags.Flag.SEEN, false);
                massMessage[0] = message;
                sendInbox.appendMessages(massMessage);
                logger.info("Письмо сохранено");
            }catch (Exception e2 ) {
                logger.error("Не сохранил в папку отправленные, переменные тут уже мертвы.");
            }
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("Письмо не отправлено");

            currentRequest.setStatus(statusRepository.FindByKey("FailedToSendEmail"));
            return false;
        }
    }

    private String getPathContract(String payer) {
        if (payer.equals("физическое лицо")) {
            return "Договор_НМО физ лица.docx";
        } else if (payer.equals("юридическое лицо")) {
            return "Шаблон Договор повышение квалификации Юр.Л.docx";
        } else {
            logger.warn("no email info");
            return "Договор_НМО физ лица.docx";
        }
    }

    private String getHTMLLetter(String payer) {
        if (payer.equals("физическое лицо")) {
            return "Шаблон_НМО_Физ_лица.html";
       } else if (payer.equals("юридическое лицо")) {
            return "Шаблон_НМО__Юр_Лица.html";
       } else {
                logger.warn("no email info");
                return "Шаблон_НМО_Физ_лица.html";
        }
    }

    private String GetTextLetterFromHTML(String NameFile){
        //  FileInputStream fis = null;
        try {
            //fis = new FileInputStream(  NameFile );
            FileSystemResource file = new FileSystemResource(new File(NameFile));
            return IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            return "";
        }

    }

}

