package com.example.servingwebcontent.service;

import com.example.servingwebcontent.Config.EmailProperties;
import com.example.servingwebcontent.Config.GoogleSheetsProperties;
import com.example.servingwebcontent.Config.NMFOLocators;
import com.example.servingwebcontent.Config.NMFOProperties;
import com.example.servingwebcontent.models.LaunchStatusTracking;
import com.example.servingwebcontent.repositories.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.service.WorkWithDataBase.SheetsAndJava;
import com.example.servingwebcontent.service.WorkWithEmail.JavaMailReader.EmailReader;
import com.example.servingwebcontent.service.WorkWithEmail.JavaMailSending.JavaMailSender;
import com.example.servingwebcontent.service.WorkWithEmail.ParserData;
import com.example.servingwebcontent.service.WorkWithEmail.WorkWithEmail;
import com.example.servingwebcontent.service.WorkWithWebSite.NMFO.DriverNMFO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailsOperations extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private LaunchStatusTrackingRepository TrackingRepository;
    @Autowired
    private GoogleSheetsProperties googleSheetsProperties;
    private EmailProperties emailProperties;
    private WorkWithEmail workWithEmail;
    @Autowired
    private SheetsAndJava sheetsService;
    private DriverNMFO driverConnect;
    private NMFOProperties propertiesNMFO;
    private NMFOLocators locators;

    public MailsOperations(LaunchStatusTrackingRepository trackingRepository,
                           GoogleSheetsProperties googleSheetsProperties,
                           EmailProperties emailProperties,
                           NMFOProperties NMFOProperties,
                           NMFOLocators NMFOLocators) {
        TrackingRepository = trackingRepository;
        this.googleSheetsProperties = googleSheetsProperties;
        this.emailProperties = emailProperties;
        this.propertiesNMFO = NMFOProperties;
        this.locators = NMFOLocators;
        logger.info("Поток запущен");
        // this.start();
    }

    public MailsOperations() {
    }

    public void run() {
        try {
            Initialization();
            while (TrackingRepository.statusLanch()) {
                logger.info("Запуск потока");
                Operation();
                MailsOperations.sleep(3);
            }
        } catch (InterruptedException e) {
            logger.info("Второй поток прерван");
        } finally {
            TrackingRepository.save(new LaunchStatusTracking(false));
            logger.info("Поток закончен");
        }
    }

    private void Initialization() {
        logger.info("Инициализация");
        sheetsService = new SheetsAndJava(googleSheetsProperties);
        workWithEmail = new WorkWithEmail(
                new EmailReader(emailProperties,emailProperties.getGetReadInbox())
                ,new JavaMailSender(emailProperties)
                ,"Отправленные");
        driverConnect = new DriverNMFO(propertiesNMFO,locators);
    }

    private void Operation() {

        Message[] messages = workWithEmail.getEmailReader()
                .ReadMessage();//"письма")

        for (Message Message : messages) {
            logger.info("Начинаем разбирать письмо");
            try {
                OperationWithMessage(Message);
            } catch (Exception e) {
                driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc();
                workWithEmail.getEmailReader().SetFlagSeen(Message, false);
                logger.warn("Fucking ERROR " + e.getMessage());
            }
        }
    }

    private void OperationWithMessage(Message message) throws Exception {

        Map<String, String> currentLine = new HashMap<>();
        getLineBaseInfo(currentLine, message);

        if (currentLine.get("Error").equals("true")) {
            throw new Exception();
        }
        // 5. Получаем данные с НМФО
        driverConnect.getDataNMFO(currentLine);
        if (currentLine.get("ApplicationCanceled").equals("true")) {
            logger.warn("Не нашел контент по заявке " + currentLine.get("Number"));
        } else {
            //7. Подтверждаем заявку на НМФО
            driverConnect.getSpoAndVoPage().setConfirmationCheckBox();//todo напомнить Насте на чекед знанчения контейнера чтобы дважды не подтверждать
            driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc(); // возврат на страницу гугла
            //8. Отправляем письмо на почту.
            workWithEmail.sendMessage(currentLine);
        }
        // 9. Записываем строку в google Sheet
        sheetsService.AppendRow("Заявки", RowData.getInfoForGoogleSheet(currentLine));
        workWithEmail.getEmailReader().
                SetFlagSeen(message, true);
        logger.info("------------> END <------------");
    }

    private void getLineBaseInfo(Map<String, String> сurrentLine, Message message) {
        сurrentLine.put("Error", "false");
        сurrentLine.put("Data", new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date()));
        String Content = null;
        try {
            Content = workWithEmail.getEmailReader()
                    .GetContentMail(message);
            ParserData ParserData = new ParserData();//TODO Передалать в компоненту ?
            сurrentLine.put("Number", ParserData.NumberApplicationFromContext(Content));
            сurrentLine.put("With", ParserData.getWithDate(Content));
            сurrentLine.put("On", ParserData.getOnDate(Content));
            сurrentLine.put("CourseName", ParserData.getNameProgramm(Content));

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            сurrentLine.put("Number", "");
            сurrentLine.put("Error", "true");
            logger.warn("Не удалось получить контент из письма");
        } finally {
            workWithEmail.getEmailReader()
                    .SetFlagSeen(message, false);
        }
        сurrentLine.put("Payer", "");
        сurrentLine.put("Email", "");
    }

}
