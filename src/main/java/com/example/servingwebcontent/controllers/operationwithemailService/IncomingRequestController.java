package com.example.servingwebcontent.controllers.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.Client;
import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class IncomingRequestController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private LaunchStatusTrackingRepository TrackingRepository;

    @Autowired
    private RequestForTrainingRepository requestForTrainingRepository;

    @GetMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");

        Iterable<RequestForTraining> requests = requestForTrainingRepository.findAll();
        model.addAttribute("requests", requests);

        FindRequsts();
        return "IncomingRequest";
    }

    @PostMapping("/IncomingRequest/start")
    @PreAuthorize("hasAuthority('write')")
    public String IncomingRequestPageStart(Model model) {

        TrackingRepository.save(new LaunchStatusTracking(true));
        logger.info("Поток запущен");
        return "redirect:/IncomingRequest";
    }

    @PostMapping("/IncomingRequest/stop")
    @PreAuthorize("hasAuthority('write')")
    public String IncomingRequestPageStop(Model model) {
        logger.info("Остановка алгоритма");
        TrackingRepository.save(new LaunchStatusTracking(false));
        logger.info("Поток останавливается");
        return "redirect:/IncomingRequest";
    }

    public void FindRequsts(){

        
    }

}
