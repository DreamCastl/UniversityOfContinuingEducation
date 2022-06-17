package com.example.servingwebcontent.controllers.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.service.workwithwebsite.IncomingRequestService;
import javafx.util.converter.LocalDateStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IncomingRequestController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private LaunchStatusTrackingRepository TrackingRepository;

    @Autowired
    private IncomingRequestService incomingRequestService;

    @GetMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");

        model.addAttribute("requests", incomingRequestService.getRequsts(
                getRequestParamMap()));

        return "IncomingRequest";
    }

    @PostMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(@RequestParam String StartDateOperation, @RequestParam String EndDateOperation, Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");

        model.addAttribute("requests", incomingRequestService.getRequsts(
                getRequestParamMap(StartDateOperation,EndDateOperation)));

        return "IncomingRequest";
    }

    private Map<String, String> getRequestParamMap(String StartDateOperation,String EndDateOperation)
    {
        Map<String, String> RequestParam = new HashMap<String,String>();
        RequestParam.put("StartDateOperation",
                StartDateOperation.equals("") ? LocalDate.of(1,1,1).toString(): StartDateOperation );
        RequestParam.put("EndDateOperation",
                EndDateOperation.equals("") ? LocalDate.of(3000,1,1).toString(): EndDateOperation );
        return RequestParam;

    }

    private Map<String, String> getRequestParamMap()
    {
        Map<String, String> RequestParam = new HashMap<String,String>();
        RequestParam.put("StartDateOperation",LocalDate.now().toString());
        RequestParam.put("EndDateOperation",LocalDate.now().toString());
        return RequestParam;
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
