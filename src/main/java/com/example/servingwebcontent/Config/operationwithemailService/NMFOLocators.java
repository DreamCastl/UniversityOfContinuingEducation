package com.example.servingwebcontent.Config.operationwithemailService;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "locators")
@Component
@Data
public class NMFOLocators {
    private String registryBtn;
    private String cyclePcTasksBtn;
    private String numberRequestField;
    private String searchBtn;
    private String focusToClientField;
    private String requestsAndResultsEducationBtn;
    private String numberRequestOnOpenRequestsPageField;
    private String confirmationCheckBox;
}
