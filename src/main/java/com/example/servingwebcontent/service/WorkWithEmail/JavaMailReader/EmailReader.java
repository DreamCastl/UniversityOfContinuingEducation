package com.example.servingwebcontent.service.WorkWithEmail.JavaMailReader;

import com.example.servingwebcontent.Config.EmailProperties;
import com.example.servingwebcontent.service.WorkWithEmail.ParserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

public class EmailReader {

    private EmailProperties auth;

    private static final Logger logger = LogManager.getLogger();
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public EmailReader(EmailProperties auth) {
        AddImapProperties(auth);
        this.auth = auth;
    }

    private void AddImapProperties(EmailProperties auth){
        Properties properties = new Properties();
        properties.put("mail.debug", "false");
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.port", auth.getIMAP_Port());
        auth.setIMAP_properties(properties);
    }

    public Message[] ReadMessage(String FolderName) {

        Session session = Session.getDefaultInstance(auth.getIMAP_properties(), auth);
        Message[] messages = new Message[0];
        try {
            logger.info("Попытка получения почты");
            Store store = session.getStore();
            store.connect(auth.getIMAP_Server(), auth.getLogin(), auth.getPassword()); // Подключение к почтовому серверу
            Folder inbox = store.getFolder(FolderName); // Папка входящих сообщений
            inbox.open(Folder.READ_WRITE); // Открываем папку в режиме только для чтения

            messages = inbox.search(new SearchTerm() {
                @Override
                public boolean match(Message msg) {
                    try {
                        return !msg.isSet(Flags.Flag.SEEN) && ((msg.getSubject().equals("Создание заявки на цикл")));
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        logger.warn("фильтр не сработал");
                        return false;
                    }
                }
            });
            return messages;
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
            logger.warn("Получение писем не удалось");
        }
        return messages;
    }


    public String GetContentMail(Message Message) throws MessagingException, IOException {
        logger.info("Получение контента из письма");
        return ParserData.getTextFromMessage(Message);
    }

    public void SetFlagSeen(Message message,boolean flag) {
        try {
            message.setFlag(Flags.Flag.SEEN,flag);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}