package com.example.servingwebcontent.service.WorkWithEmail;

import com.example.servingwebcontent.service.WorkWithEmail.JavaMailReader.EmailReader;
import com.example.servingwebcontent.service.WorkWithEmail.JavaMailSending.JavaMailSender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.mail.Folder;
import java.util.Map;

@Getter
public class WorkWithEmail {
    EmailReader emailReader;
    JavaMailSender javaMailSender;
    Folder sendInbox;

    public WorkWithEmail(EmailReader emailReader, JavaMailSender javaMailSender, String sendInboxName) {
        this.emailReader = emailReader;
        this.javaMailSender = javaMailSender;
        this.sendInbox = this.emailReader.getFolder(sendInboxName);
    }

    public void sendMessage(Map<String, String> currentLine) {
        javaMailSender.sendMessageWithAttachment(currentLine,sendInbox);
    }
}
