package com.example.servingwebcontent.Config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.mail.PasswordAuthentication;
import java.util.Properties;

@ConfigurationProperties(prefix = "email.properties")
@Component
@Data
@Getter
public class EmailProperties extends javax.mail.Authenticator
{
    private String login   ;
    private String password;
    private String IMAP_Server;
    private String IMAP_Port;
    private String SMTP_Server;
    private String SMTP_Port;
    private Properties IMAP_properties = new Properties() ;
    private String getSendingInbox;
    private String getReadInbox;

    private Properties AddImapProperties(Properties properties) {
        properties.put("mail.debug", "false");
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.port", IMAP_Port);
        return properties;
    }





    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(login, password);
    }

//    public String getLogin() {
//        return login;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public Properties getIMAP_properties() {
//        return IMAP_properties;
//    }
//
//    public String getSMTP_Server() {
//        return SMTP_Server;
//    }
//
//    public String getSMTP_Port() {
//        return SMTP_Port;
//    }
}
