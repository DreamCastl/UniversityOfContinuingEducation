package com.example.servingwebcontent.controllers.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTracking;
import com.example.servingwebcontent.models.operationwithemailService.LaunchStatusTrackingSMS;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.LaunchStatusTrackingSMSRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithDataBase.ServiceUPOR;
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
    private LaunchStatusTrackingSMSRepository TrackingRepositorySMS;

    @Autowired
    private IncomingRequestService incomingRequestService;

    @Autowired
    private ServiceUPOR serviceUPOR;

    @Autowired
    private StatusRepository statusRepository;

    @GetMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");

        model.addAttribute("requests", incomingRequestService.getRequsts(
                getRequestParamMap()));
        model.addAttribute("statuses", statusRepository.findAll());
        return "IncomingRequest";
    }

    @PostMapping("/IncomingRequest")
    @PreAuthorize("hasAuthority('read')")
    public String IncomingRequestPage(@RequestParam String StartDateOperation,
                                      @RequestParam String EndDateOperation,
                                      @RequestParam String status,
                                      @RequestParam String NameProgramTraining,
                                      @RequestParam String startDate,
                                      @RequestParam String endDate,
                                      @RequestParam String numberRequest,
                                      @RequestParam String fullName,
                                      @RequestParam String region,
                                      @RequestParam String specialization,
                                      @RequestParam String job,
                                      @RequestParam String regionJob,
                                      @RequestParam String position,
                                      Model model) {
        logger.info("Переход на IncomingRequest");
        model.addAttribute("linkActiveIncomingRequest", "nav-link active");
        model.addAttribute("linkActiveHome", "nav-link");

        model.addAttribute("requests", incomingRequestService.getRequsts(
                        getRequestParamMap(
                                StartDateOperation,
                                EndDateOperation,
                                status,
                                NameProgramTraining,
                                startDate,
                                endDate,
                                numberRequest,
                                fullName,
                                region,
                                specialization,
                                job,
                                regionJob,
                                position
                        )));

        model.addAttribute("statuses", statusRepository.findAll());
        return "IncomingRequest";
    }

    private Map<String, String> getRequestParamMap(String StartDateOperation,//+
                                                   String EndDateOperation,//+
                                                   String status,//+
                                                   String nameProgramTraining,
                                                   String startDate,//
                                                   String endDate,//
                                                   String numberRequest,
                                                   String fullName,
                                                   String region,
                                                   String specialization,
                                                   String job,
                                                   String regionJob,
                                                   String position) {
        Map<String, String> RequestParam = new HashMap<String, String>();
        RequestParam.put("StartDateOperation",
                StartDateOperation.equals("") ? LocalDate.of(1, 1, 1).toString() : StartDateOperation);
        RequestParam.put("EndDateOperation",
                EndDateOperation.equals("") ? LocalDate.of(3000, 1, 1).toString() : EndDateOperation);
        RequestParam.put("status", status);
        RequestParam.put("NameProgramTraining", nameProgramTraining);
        RequestParam.put("startDate", startDate);
        RequestParam.put("endDate", endDate);
        RequestParam.put("numberRequest", numberRequest);
        RequestParam.put("fullName", fullName);
        RequestParam.put("region", region);
        RequestParam.put("specialization", specialization);
        RequestParam.put("job", job);
        RequestParam.put("regionJob", regionJob);
        RequestParam.put("position", position);
        return RequestParam;

    }

    private Map<String, String> getRequestParamMap() {
        Map<String, String> RequestParam = new HashMap<String, String>();
        RequestParam.put("StartDateOperation", LocalDate.now().toString());
        RequestParam.put("EndDateOperation", LocalDate.now().toString());
        RequestParam.put("status", "");
        RequestParam.put("NameProgramTraining", "");
        RequestParam.put("startDate", "");
        RequestParam.put("endDate", "");
        RequestParam.put("numberRequest", "");
        RequestParam.put("fullName", "");
        RequestParam.put("region", "");
        RequestParam.put("specialization", "");
        RequestParam.put("job", "");
        RequestParam.put("regionJob", "");
        RequestParam.put("position", "");
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


    @PostMapping("/IncomingRequest/startSMS")
    @PreAuthorize("hasAuthority('write')")
    public String IncomingRequestPageStartSMS(Model model) {

        TrackingRepositorySMS.save(new LaunchStatusTrackingSMS(true));
        logger.info("SMS start");
        return "redirect:/IncomingRequest";
    }

    @PostMapping("/IncomingRequest/stopSMS")
    @PreAuthorize("hasAuthority('write')")
    public String IncomingRequestPageStopSMS(Model model) {
        TrackingRepositorySMS.save(new LaunchStatusTrackingSMS(false));
        logger.info("SMS stop");
        return "redirect:/IncomingRequest";
    }

}
