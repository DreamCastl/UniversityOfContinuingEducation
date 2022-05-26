package com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailSending;

import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class JavaMailSender {
    static JavaMailSenderImpl emailSender;

     private static final Logger logger = LogManager.getLogger();

    public JavaMailSender(EmailProperties emailProperties) {

        emailSender = new JavaMailSenderImpl();
        emailSender.setHost(emailProperties.getSMTP_Server());
        emailSender.setPort(Integer.parseInt(emailProperties.getSMTP_Port()));
        emailSender.setUsername(emailProperties.getLogin());
        emailSender.setPassword(emailProperties.getPassword());
        AddProperties(emailSender.getJavaMailProperties());

    }

    public boolean sendMessageWithAttachment(RequestForTraining currentRequest, Folder sendInbox) {

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
            helper.setSubject("Заявка на обучение "+currentRequest.getNumberRequest());
            //     helper.setText(GetTextLetterFromHTML(PropertiesSend.get("NameFileHTML")),true);TODO придумать где хранить

      //      String pathToAttachment = PropertiesSend.get("pathToAttachment"); TODO придумать где хранить, договор зависит от Payera// Хранить в объекте?
            // TODO    FileSystemResource file = new FileSystemResource(new File(pathToAttachment));

        // TODO    helper.addAttachment(PropertiesSend.get("pathToAttachment"), file);


            logger.info("Письмо сохранено");

            emailSender.send(message);
            logger.info("Письмо отправлено");


            Message[] massMessage = new Message[1];
            message.setFlag(Flags.Flag.SEEN,true);
            massMessage[0] = message;
            sendInbox.appendMessages(massMessage);

            //  currentRequest.setStatus(Status );TODO "Обработана"
            return true;
        } catch (Exception e ) {

            try {
                Message[] massMessage = new Message[1];
                message.setFlag(Flags.Flag.SEEN, false);
                massMessage[0] = message;
                sendInbox.appendMessages(massMessage);
            }catch (Exception e2 ) {
                logger.error("Не сохранил в папку отправленные, переменные тут уже мертвы.");
            }
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("Письмо не отправлено");
            //  currentRequest.setStatus(Status );TODO "Письмо не отправлено"
            return false;
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


    private static void AddProperties(Properties props){
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
    }

}


