package com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailReader;

import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

@Getter
public class EmailReader {

    private EmailProperties auth;
    private Folder inbox;
    private static final Logger logger = LogManager.getLogger();
    private final String ReaderFolderName;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    public EmailReader(EmailProperties auth,String FolderName) {
        AddImapProperties(auth);
        this.auth = auth;
        inbox = getFolder(FolderName);
        ReaderFolderName = FolderName;
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
        if (!inbox.isOpen()) {
            logger.info("Папка закрыта");
            inbox = getFolder(ReaderFolderName);
        }

        try {
            logger.info("Попытка получения почты");

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
            e.printStackTrace();
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
            e.printStackTrace();
        }
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
}