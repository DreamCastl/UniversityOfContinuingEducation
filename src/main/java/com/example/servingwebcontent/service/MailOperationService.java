package com.example.servingwebcontent.service;


import com.example.servingwebcontent.Config.*;
import com.example.servingwebcontent.models.LaunchStatusTracking;
import com.example.servingwebcontent.repositories.LaunchStatusTrackingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailOperationService {
    @Autowired
    protected GoogleSheetsProperties GoogleSheetsProperties;
    @Autowired
    private EmailProperties emailProperties;
    @Autowired
    private NMFOLocators NMFOLocators;
    @Autowired
    private NMFOProperties NMFOProperties;
    @Autowired
    protected LaunchStatusTrackingRepository TrackingRepository;
    @Autowired
    private MailsOperations mailsOperations;

    private static final Logger logger = LogManager.getLogger();



    public void serviceRunner(boolean run) {
        boolean currentStatus = TrackingRepository.statusLanch();

        if (run && !currentStatus) {

            new MailsOperations(TrackingRepository,
                    GoogleSheetsProperties,
                    emailProperties,
                    NMFOProperties,
                    NMFOLocators).start();
            TrackingRepository.save(new LaunchStatusTracking(true));
        } else if (run && currentStatus ){
          logger.info("Поток уже запущен");
        }else if (!run) {
            TrackingRepository.save(new LaunchStatusTracking(false));
            logger.info("Поток останавливается");
        }

    }

}
