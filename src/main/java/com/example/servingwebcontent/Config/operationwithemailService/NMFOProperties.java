package com.example.servingwebcontent.Config.operationwithemailService;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "security.nmfo")
@Component
@Data
public class NMFOProperties {
    private String loginPage;
    private String spoPage;
    private String voPage;
    private String loginAdmin;
    private String PassAdmin;

    public String getLoginPage() {
        return loginPage;
    }

    public String getSpoPage() {
        return spoPage;
    }

    public String getVoPage() {
        return voPage;
    }

    public String getLoginAdmin() {
        return loginAdmin;
    }

    public String getPassAdmin() {
        return PassAdmin;
    }
}
