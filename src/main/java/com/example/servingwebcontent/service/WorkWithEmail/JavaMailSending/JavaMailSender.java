package com.example.servingwebcontent.service.WorkWithEmail.JavaMailSending;

import com.example.servingwebcontent.Config.EmailProperties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

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

    public boolean sendMessageWithAttachment(Map<String, String> PropertiesSend) {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        } catch (MessagingException e) {
            e.printStackTrace();
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

        //    emailSender.send(message);

            logger.info("Письмо отправленно");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.warn("Письмо не отправленно");
            return false;
        }
    }

    private String GetTextLetterFromHTML(String NameFile){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("src/main//resources/"+NameFile);
            return IOUtils.toString(fis, "UTF-8");
        } catch (IOException e) {
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


