package com.example.servingwebcontent.controllers;

import com.example.servingwebcontent.service.MailOperationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IncomingRequestController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private MailOperationService mailOperationService;

    @GetMapping("/IncomingRequest")
    public String IncomingRequestPage(Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");
        return "IncomingRequest";
    }

    @PostMapping("/IncomingRequest/start")
    public String IncomingRequestPageStart(Model model) {

        mailOperationService.serviceRunner(true);
        return "redirect:/IncomingRequest";
    }

    @PostMapping("/IncomingRequest/stop")
    public String IncomingRequestPageStop(Model model) {
        logger.info("Остановка алгоритма");
        mailOperationService.serviceRunner(false);
        return "redirect:/IncomingRequest";
    }

}
