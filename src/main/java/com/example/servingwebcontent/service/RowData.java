package com.example.servingwebcontent.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowData {
    private static final Logger logger = LogManager.getLogger();

    public static boolean AddClientData(List<String> clientInfo, Map<String, String> currentLine){

        currentLine.put("ApplicationCanceled", "false"); // TODO Отмененные заявки

        currentLine.put("FullName", clientInfo.get(1));
        currentLine.put("SNILS", clientInfo.get(2));
        currentLine.put("BirtDay", clientInfo.get(3));
        currentLine.put("Region", clientInfo.get(4));
        currentLine.put("Specialization", clientInfo.get(5));
        currentLine.put("PlaceOfWork", clientInfo.get(6));
        currentLine.put("ReginJob", clientInfo.get(7));
        currentLine.put("Position", clientInfo.get(8));
        currentLine.put("DateOfApplication", clientInfo.get(9));
        currentLine.put("ApplicationСonfirmed", clientInfo.get(10));
        currentLine.put("FoundationOfLearning", clientInfo.get(11));
        currentLine.put("ApprovedByEmployer", clientInfo.get(12));
        currentLine.put("Paid", clientInfo.get(13));
        currentLine.put("PaymentReceived", clientInfo.get(14));
        currentLine.put("PaymentDate", clientInfo.get(15));
        currentLine.put("Email", clientInfo.get(16));
        currentLine.put("TelephoneNumber", clientInfo.get(17));
        currentLine.put("Payer", clientInfo.get(18));
        currentLine.put("AdditionalInformation", clientInfo.get(19));
        currentLine.put("Comment", clientInfo.get(20));

        if (currentLine.get("Payer").equals("Физическое лицо")) {   //TODO Перенести во внешний файл или БД
            currentLine.put("NameFileHTML", "Шаблон_НМО_Физ_лица.html");
            currentLine.put("pathToAttachment", "Договор_НМО физ лица.docx");
        } else if (currentLine.get("Payer").equals("Юридическое лицо")) {
            currentLine.put("NameFileHTML", "Шаблон_НМО__Юр_Лица.html");
            currentLine.put("pathToAttachment", "Шаблон Договор повышение квалификации Юр.Л.docx");
        } else {
            logger.warn("no email info");
        }
        currentLine.put("subject", "Договор_НМО");
        currentLine.put("text", "");
        return false;
    }

    public static List<String> getInfoForGoogleSheet(Map<String, String> currentLine) {
        List<String> rez = new ArrayList<>();
        rez.add(currentLine.get("CourseName"));
        rez.add(currentLine.get("With"));
        rez.add(currentLine.get("On"));
        rez.add(currentLine.get("FullName"));
        rez.add(currentLine.get("SNILS"));
        rez.add(currentLine.get("BirtDay"));
        rez.add(currentLine.get("Region"));
        rez.add(currentLine.get("Specialization"));
        rez.add(currentLine.get("PlaceOfWork"));
        rez.add(currentLine.get("ReginJob"));
        rez.add(currentLine.get("Position"));
        rez.add(currentLine.get("DateOfApplication"));
        rez.add(currentLine.get("ApplicationСonfirmed"));
        rez.add(currentLine.get("FoundationOfLearning"));
        rez.add(currentLine.get("ApprovedByEmployer"));
        rez.add(currentLine.get("Paid"));
        rez.add(currentLine.get("PaymentReceived"));
        rez.add(currentLine.get("PaymentDate"));
        rez.add(currentLine.get("Email"));
        rez.add(currentLine.get("TelephoneNumber"));
        rez.add(currentLine.get("Payer"));
        rez.add(currentLine.get("AdditionalInformation"));
        rez.add(currentLine.get("Comment"));

        return rez;
    }

}
