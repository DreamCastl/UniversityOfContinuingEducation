package com.example.servingwebcontent.service.WorkWithEmail;

import com.example.servingwebcontent.service.WorkWithEmail.JavaMailReader.EmailReader;
import com.example.servingwebcontent.service.WorkWithEmail.JavaMailSending.JavaMailSender;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Folder;
import java.util.Map;

@Getter
public class WorkWithEmail {
    EmailReader emailReader;
    JavaMailSender javaMailSender;
    Folder sendInbox;
    private final String sendInboxName;
    private static final Logger logger = LogManager.getLogger();

    public WorkWithEmail(EmailReader emailReader, JavaMailSender javaMailSender, String sendInboxName) {
        this.emailReader = emailReader;
        this.javaMailSender = javaMailSender;
        this.sendInboxName = sendInboxName;
        this.sendInbox = this.emailReader.getFolder(sendInboxName);
    }

    public void sendMessage(Map<String, String> currentLine) {

        if (! sendInbox.isOpen()) {
            logger.info("Папка исходящих закрыта");
            sendInbox = this.emailReader.getFolder(sendInboxName);
        }
        javaMailSender.sendMessageWithAttachment(currentLine,sendInbox);
    }
}
