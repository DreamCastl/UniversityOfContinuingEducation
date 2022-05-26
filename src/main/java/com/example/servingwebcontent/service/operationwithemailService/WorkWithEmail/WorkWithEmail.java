package com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail;

import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailReader.EmailReader;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailSending.JavaMailSender;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Folder;
import java.util.Map;

@Getter
public class WorkWithEmail { //TODO сделать единым
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

    public void sendMessage(RequestForTraining currentRequest) {

        if (! sendInbox.isOpen()) {
            logger.info("Папка исходящих закрыта");
            sendInbox = this.emailReader.getFolder(sendInboxName);
        }
        javaMailSender.sendMessageWithAttachment(currentRequest,sendInbox);
    }
}
