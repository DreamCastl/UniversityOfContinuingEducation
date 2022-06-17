package com.example.servingwebcontent.models.dto;

import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomingRequestContract {

    private String numberRequest;
    private String status;
    private String NameProgramTraining;
    private String startDate;
    private String endDate;
    private String dateRequest;
    private String dateOperation;
    private String employerApproved;
    private String requestConfirmed;
    private String foundationOfEducation;
    private String paid;
    private String paymentDate;
    private String  paymentReceived;
    private String payer;
    private String comment;
    private String additionalInformation;
    private String snils;
    private String fullName;
    private String bithDay;
    private String region;
    private String specialization;
    private String job;
    private String regionJob;
    private String position;
    private String email;
    private String telephoneNumber;
    private String clientAdditionalInformation;
    private String clientComment;
    private String commentUniversity;


}
