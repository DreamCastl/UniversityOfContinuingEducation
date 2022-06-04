package com.example.servingwebcontent.controllers.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.service.operationwithemailService.MailOperationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IncomingRequestController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private LaunchStatusTrackingRepository TrackingRepository;

    @GetMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");
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

}
