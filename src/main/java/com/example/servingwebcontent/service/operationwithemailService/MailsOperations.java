package com.example.servingwebcontent.service.operationwithemailService;

import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOLocators;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOProperties;
import com.example.servingwebcontent.Config.operationwithemailService.SetSpreadSheetTable;
import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.models.operationwithemailService.Status;
import com.example.servingwebcontent.repositories.operationwithemailService.ClientRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithDataBase.SheetsAndJava;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailReader.EmailReader;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.JavaMailSending.JavaMailSender;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.WorkWithEmail;
import com.example.servingwebcontent.service.operationwithemailService.NMFO.DriverNMFO;
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

    private SetSpreadSheetTable SettingsTable;
    private EmailProperties emailProperties;
    private WorkWithEmail workWithEmail;

    private SheetsAndJava sheetsService;
    private DriverNMFO driverConnect;
    private NMFOProperties propertiesNMFO;
    private NMFOLocators locators;

    protected RequestForTrainingRepository requestForTrainingRepository;
    protected ClientRepository clientRepository;
    private int counter = 0;

    public MailsOperations(LaunchStatusTrackingRepository trackingRepository,
                           SetSpreadSheetTable settingsTable ,
                           EmailProperties emailProperties,
                           NMFOProperties NMFOProperties,
                           NMFOLocators NMFOLocators,
                           ClientRepository clientRepository ,
                           RequestForTrainingRepository requestForTrainingRepository) {
        TrackingRepository = trackingRepository;
        this.SettingsTable = settingsTable;
        this.emailProperties = emailProperties;
        this.propertiesNMFO = NMFOProperties;
        this.locators = NMFOLocators;
        this.requestForTrainingRepository= requestForTrainingRepository;
        this.clientRepository = clientRepository;

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
                logger.info("Ожидание");
                MailsOperations.sleep(300000);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            logger.info("Второй поток прерван");
        } finally {

            TrackingRepository.save(new LaunchStatusTracking(false));
            logger.info("Поток закончен");
        }
    }

    private void Initialization() {
        logger.info("Инициализация");
        sheetsService = new SheetsAndJava(SettingsTable);
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

            logger.info("Запуск " +counter + " " +counter%20);
            if (counter%10 == 0) {
                driverConnect.getDriver().quit();
                driverConnect = new DriverNMFO(propertiesNMFO,locators);
                logger.info("Перезагрузка драйвере");
            }
            if (!TrackingRepository.statusLanch()) {
                driverConnect.getDriver().quit();
                break;}
            logger.info("Начинаем разбирать письмо");
            try {
                OperationWithMessage(Message);
                counter += 1;
            } catch (Exception e) {
                driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc();
                workWithEmail.getEmailReader().SetFlagSeen(Message, false);
                logger.warn("Fucking ERROR " + e.getMessage());
            }
        }
    }

    private void OperationWithMessage(Message message) throws Exception {

        RequestForTraining currentRequest = new RequestForTraining(message,workWithEmail);

        // 5. Получаем данные с НМФО
        currentRequest.AddDateFromNMFO(driverConnect.getDataNMFO(currentRequest.getNumberRequest())) ;
        if (currentRequest.getClient() == null) {
            logger.warn("Не нашел контент по заявке " + currentRequest.getNumberRequest());
          //  currentRequest.setStatus(Status ОТМЕНЕНА); TODO Тащим из репозитория
        } else {
            //7. Подтверждаем заявку на НМФО
            driverConnect.getSpoAndVoPage().setConfirmationCheckBox();//todo напомнить Насте на чекед знанчения контейнера чтобы дважды не подтверждать
            driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc(); // возврат на страницу гугла
            //8. Отправляем письмо на почту.
            workWithEmail.sendMessage(currentRequest);
        }
        // 9. Записываем строку в google Sheet
        requestForTrainingRepository.save(currentRequest); // TODO переделать сейв т.к. есть вложенный, не факт что работает...
      //TODO Временно будем писать в 2 БД  sheetsService.AppendRow("Заявки", RowData.getInfoForGoogleSheet(currentLine));
        workWithEmail.getEmailReader().
                SetFlagSeen(message, true);
        logger.info("------------> END <------------");
    }

}
