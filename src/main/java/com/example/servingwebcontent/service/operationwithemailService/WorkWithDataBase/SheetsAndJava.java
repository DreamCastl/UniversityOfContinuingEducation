package com.example.servingwebcontent.service.operationwithemailService.WorkWithDataBase;

import com.example.servingwebcontent.Config.operationwithemailService.SetSpreadSheetTable;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Component
public class SheetsAndJava {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google Sheets Example";
    private SetSpreadSheetTable settingsTable;
    private static final Logger logger = LogManager.getLogger();

    public SheetsAndJava(SetSpreadSheetTable Properties) {
        this.settingsTable = Properties;
        logger.info("Подключение к Google Sheets.");
        try {
            sheetsService = getSheetsService();
        } catch (IOException | GeneralSecurityException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsAndJava.class.getResourceAsStream("/credential.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),JacksonFactory.getDefaultInstance(),
                clientSecrets,scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws  IOException,GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

    }

    public void AppendRow(String NameTable,List clientInfo)  {
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(
                        clientInfo
                ));
        try {
            logger.info("Adding в Google Sheets");
            AppendValuesResponse appendResult = sheetsService.spreadsheets().values()
                    .append(settingsTable.getID_Table(),NameTable,appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (IOException e) {
            logger.error("Err");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


}
