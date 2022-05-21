package com.example.servingwebcontent.service.operationwithemailService;


import com.example.servingwebcontent.Config.operationwithemailService.EmailProperties;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOLocators;
import com.example.servingwebcontent.Config.operationwithemailService.NMFOProperties;
import com.example.servingwebcontent.Config.operationwithemailService.SetSpreadSheetTable;
import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailOperationService {

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
    private MailsOperations mailsOperations;

    private static final Logger logger = LogManager.getLogger();



    public void serviceRunner(boolean run) {
        boolean currentStatus = TrackingRepository.statusLanch();

        if (run && !currentStatus) {

            new MailsOperations(TrackingRepository,
                    settingsTable,
                    emailProperties,
                    nmfoProperties,
                    nmfoLocators).start();
            TrackingRepository.save(new LaunchStatusTracking(true));
        } else if (run && currentStatus ){
          logger.info("Поток уже запущен");
        }else if (!run) {
            TrackingRepository.save(new LaunchStatusTracking(false));
            logger.info("Поток останавливается");
        }

    }

}
