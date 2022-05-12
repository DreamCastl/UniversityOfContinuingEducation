package com.example.servingwebcontent.service.WorkWithEmail.JavaMailSending;

import com.example.servingwebcontent.Config.EmailProperties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
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

    public boolean sendMessageWithAttachment(Map<String, String> PropertiesSend, Folder sendInbox) {

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
            helper.setTo(PropertiesSend.get("Email"));
            helper.setSubject(PropertiesSend.get("subject"));
            helper.setText(GetTextLetterFromHTML(PropertiesSend.get("NameFileHTML")),true);

         //  pathToAttachment = "/src/main/resources/ContractTemplates/"+ PropertiesSend.get("pathToAttachment");
            String pathToAttachment = PropertiesSend.get("pathToAttachment");
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));

            helper.addAttachment(PropertiesSend.get("pathToAttachment"), file);

            emailSender.send(message);
            logger.info("Письмо отправлено");
            Message[] massMessage = new Message[1];
            message.setFlag(Flags.Flag.SEEN,true);

            massMessage[0] = message;

            sendInbox.appendMessages(massMessage);
            logger.info("Письмо сохранено");
            PropertiesSend.put("info","Обработана");
            return true;
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("Письмо не отправленно");
            PropertiesSend.put("info","Письмо не отправленно");
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


    public void SaveMessage(){
//        Session session = Session.getDefaultInstance(props, null);
//        Store store = session.getStore("imap");
//        store.connect(host, "user", "userpwd");
//
//        Folder folder = (Folder) store.getFolder("Sent");
//        if (!folder.exists()) {
//            folder.create(Folder.HOLDS_MESSAGES);
//        }
//        folder.open(Folder.READ_WRITE);
//        System.out.println("appending...");
//        try {
//            folder.appendMessages(new Message[]{message});
//            // Message[] msgs = folder.getMessages();
//            message.setFlag(FLAGS.Flag.RECENT, true);
//        } catch (Exception ignore) {
//            System.out.println("error processing message " + ignore.getMessage());
//        } finally {
//            store.close();
//            folder.close(false);
//        }
    }
}


