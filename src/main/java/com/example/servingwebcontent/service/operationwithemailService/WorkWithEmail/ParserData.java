package com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

public class ParserData {
    private static final Logger logger = LogManager.getLogger();

    public String NumberApplicationFromContext(String cont){
        return cont.substring(cont.indexOf("подал-(а) заявку")+17,cont.indexOf(" на цикл по"));
    }
    public String NumberProgrammFromContext(String cont){
        int indx = cont.indexOf("овышения квалификации ");
        return cont.substring(indx+22,cont.indexOf(" ",indx+23));
    }

    public String getNameProgramm(String cont){
        return cont.substring(cont.indexOf("«")+1,cont.indexOf("»"));
    }

    public String getWithDate(String cont){
        return cont.substring(cont.indexOf("ВНИМАНИЕ")-26,cont.indexOf("ВНИМАНИЕ")-2).split(" по ")[0]; //todo параша
    }

    public String getOnDate(String cont){
        return cont.substring(cont.indexOf("ВНИМАНИЕ")-26,cont.indexOf("ВНИМАНИЕ")-2).split(" по ")[1];//todo параша
    }

    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }
    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
