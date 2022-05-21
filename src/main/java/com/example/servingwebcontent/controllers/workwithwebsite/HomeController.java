package com.example.servingwebcontent.controllers.workwithwebsite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LogManager.getLogger();

    @GetMapping("/Home")
    public String HomePage(Model model) {
        logger.info("Переход на HomePage");
        model.addAttribute("linkActiveHome", "nav-link active");
        model.addAttribute("linkActiveIncomingRequest", "nav-link");
        return "Home";
    }

    @GetMapping("/")
    public String BasePage(Model model) {
        model.addAttribute("linkActiveHome", "nav-link active");
        model.addAttribute("linkActiveIncomingRequest", "nav-link");
        return "Home";
    }

    @GetMapping("/success")
    public String getSuccessPage() {
        return "success";
    }

}