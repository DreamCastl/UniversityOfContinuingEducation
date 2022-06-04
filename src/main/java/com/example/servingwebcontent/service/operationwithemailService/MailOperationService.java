package com.example.servingwebcontent.service.operationwithemailService;

import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOLocators;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOProperties;
import com.example.servingwebcontent.Config.operationwithemailService.SetSpreadSheetTable;
import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.repositories.operationwithemailService.ClientRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import com.example.servingwebcontent.service.operationwithemailService.NMFO.DriverNMFO;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithDataBase.SheetsAndJava;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.WorkWithEmail;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import java.util.Date;

@Service
public class MailOperationService {
    // инжектим настройки.
    @Autowired
    private SetSpreadSheetTable settingsTable;
    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private NMFOLocators nmfoLocators;
    @Autowired
    private NMFOProperties nmfoProperties;
    @Autowired
    protected LaunchStatusTrackingRepository TrackingRepository;
    @Autowired
    protected RequestForTrainingRepository requestForTrainingRepository;
    @Autowired
    protected ClientRepository clientRepository;
    @Autowired
    protected StatusRepository statusRepository;

    // используемые компоненты (не компонненты т.к. нужно пересоздать)
    private SheetsAndJava sheetsService;
    private DriverNMFO driverConnect;
    private WorkWithEmail workWithEmail;
    private int counter = 0;
    private Date DateRunning = new Date(1, 1, 1);

    private static final Logger logger = LogManager.getLogger();
    boolean currentStatus = false;

    @Autowired
    private RequestForTrainingService requestForTrainingService;

    @EventListener(ApplicationReadyEvent.class)
    public void serviceRunner() {

        TrackingRepository.save(new LaunchStatusTracking(false)); // Устанавливаем статус по умолчанию при запуске.

        while (true) { //variats ?
            currentStatus = TrackingRepository.statusLanch(); // Обновляем статус
            if (currentStatus) {
                try {
                    Initialization();
                    Operation();
                    logger.info("Сделалъ");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    logger.info("Второй поток прерван");
                }
                try {
                    logger.info("Ожидание");
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void Operation() {

        Message[] messages = workWithEmail.ReadMessage();//"письма")

        for (Message Message : messages) {

            logger.info("Разбор письма №" + counter + " кратность " + counter % 15);
            if (counter % 15 == 0) {
                driverConnect.getDriver().quit();
                driverConnect = new DriverNMFO(nmfoProperties, nmfoLocators);
                logger.info("Перезагрузка драйвера");
            }
            if (!TrackingRepository.statusLanch()) {
                driverConnect.getDriver().quit();
                break;
            }

            try {
                counter += 1;
                OperationWithMessage(Message);
            } catch (Exception e) {
                driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc();
                workWithEmail.SetFlagSeen(Message, false);
                logger.warn("Fucking ERROR " + e.getMessage());
            }
        }
    }

    private void OperationWithMessage(Message message) throws Exception {

        RequestForTraining currentRequest = requestForTrainingService.addDateForMessage(message,workWithEmail);

        // 5. Получаем данные с НМФО
        requestForTrainingService.AddDateFromNMFO(currentRequest,
                driverConnect.getDataNMFO(currentRequest.getNumberRequest()));

        if (currentRequest.getClient() == null) {
            logger.warn("Не нашел контент по заявке " + currentRequest.getNumberRequest());
        } else {
            //7. Подтверждаем заявку на НМФО
            //todo   driverConnect.getSpoAndVoPage().setConfirmationCheckBox();//todo напомнить Насте на чекед знанчения контейнера чтобы дважды не подтверждать
            currentRequest.setRequestConfirmed(true);
            driverConnect.getSpoAndVoPage().closeWindowsAndReturnCyclePc(); // возврат на страницу гугла
            //8. Отправляем письмо на почту.
            workWithEmail.sendMessage(currentRequest);
        }
        // 9. Записываем строку в google Sheet
        requestForTrainingRepository.save(currentRequest);
        sheetsService.AppendRow("Заявки",currentRequest.ConvertToList() );
        workWithEmail.SetFlagSeen(message, true);
        logger.info("------------> END <------------");
    }

    private void Initialization() {

        /*
        Т.к. юзеры сами влезают в письма, есть какая-то дурная ошибка по срабатыванию фильтра на почте
        так же накапливаются заявки в браузере, поэтому делаем
         перезапуск каждые 2 часа
        */

        if ( new Date().compareTo(DateUtils.addHours(DateRunning, 2)) > 0) {
            logger.info("Инициализация");
            DateRunning = new Date();
            counter = 0;
            sheetsService = new SheetsAndJava(settingsTable);
            workWithEmail = new WorkWithEmail(emailProperties,statusRepository);

            if (driverConnect != null) {
                driverConnect.getDriver().quit();
            }
            driverConnect = new DriverNMFO(nmfoProperties, nmfoLocators);
        }
    }
}
