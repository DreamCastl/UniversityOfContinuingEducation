package com.example.servingwebcontent.Config;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsProperties {
    @Value("${google.sheets.properties.id_table}")
    private String id_Table;

    @Bean
    public String getID_Table() {
        return this.id_Table;
    }

}
