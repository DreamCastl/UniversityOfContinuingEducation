package com.example.servingwebcontent.service.workwithwebsite;

import com.example.servingwebcontent.models.dto.IncomingRequestContract;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public IncomingRequestContract RequestForTrainingToContract(RequestForTraining requestForTraining) {


        IncomingRequestContract contract = IncomingRequestContract.builder()
                .numberRequest(Optional.ofNullable(requestForTraining.getNumberRequest()).orElse(""))
                .status(Optional.ofNullable(requestForTraining.getStatus().getTranscription()).orElse(""))
                .NameProgramTraining(Optional.ofNullable(requestForTraining.getNameProgramTraining()).orElse(""))
                .startDate(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getStartDate()).orElse(LocalDate.of(1,1,1))))
                .endDate(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getEndDate()).orElse(LocalDate.of(1, 1, 1))))
                .dateRequest(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getDateRequest()).orElse(LocalDate.of(1, 1, 1))))
                .dateOperation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getDateOperation().toString()).orElse("")))
                .employerApproved(ParserData.ValueToString(Optional.ofNullable(requestForTraining.isEmployerApproved()).orElse(false)))
                .requestConfirmed(ParserData.ValueToString(Optional.ofNullable(requestForTraining.isRequestConfirmed()).orElse(false)))
                .foundationOfEducation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getFoundationOfEducation()).orElse("")))
                .paid(ParserData.ValueToString(Optional.ofNullable(requestForTraining.isPaid()).orElse(false)))
                .paymentDate(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getPaymentDate()).orElse(LocalDate.of(1, 1, 1))))
                .paymentReceived(ParserData.ValueToString(Optional.ofNullable(requestForTraining.isPaymentReceived()).orElse(false)))
                .payer(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getPayer()).orElse("")))
                .comment(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getComment()).orElse("")))
                .additionalInformation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getAdditionalInformation()).orElse("")))
                .snils("")
                .fullName("")
                .bithDay(LocalDate.of(1, 1, 1).toString())
                .region("")
                .specialization("")
                .job("")
                .regionJob("")
                .position("")
                .email("")
                .telephoneNumber("")
                .clientAdditionalInformation("")
                .clientComment("")
                .commentUniversity("")
                .build();

        if (requestForTraining.getClient() != null) {
            contract.setSnils(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getSnils()).orElse("")));
            contract.setFullName(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getFullName()).orElse("")));
            contract.setBithDay(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getBithDay()).orElse(LocalDate.of(1, 1, 1))));
            contract.setRegion(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getRegion()).orElse("")));
            contract.setSpecialization(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getSpecialization()).orElse("")));
            contract.setJob(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getJob()).orElse("")));
            contract.setRegionJob(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getRegionJob()).orElse("")));
            contract.setPosition(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getPosition()).orElse("")));
            contract.setEmail(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getEmail()).orElse("")));
            contract.setTelephoneNumber(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getTelephoneNumber()).orElse("")));
            contract.setAdditionalInformation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getAdditionalInformation()).orElse("")));
            contract.setClientComment(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getComment()).orElse("")));
            contract.setClientAdditionalInformation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getCommentUniversity()).orElse("")));
            contract.setClientAdditionalInformation(ParserData.ValueToString(Optional.ofNullable(requestForTraining.getClient().getCommentUniversity()).orElse("")));
        }
    return contract;
    }
}
