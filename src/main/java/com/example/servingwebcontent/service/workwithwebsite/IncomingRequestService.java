package com.example.servingwebcontent.service.workwithwebsite;


import com.example.servingwebcontent.models.dto.IncomingRequestContract;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IncomingRequestService {

    @Autowired
    private RequestForTrainingRepository requestForTrainingRepository;

    @Autowired
    private ItemMapperImpl itemMapperImpl;
    @Autowired
    StatusRepository statusRepository;

    public List<IncomingRequestContract> getRequsts(Map<String, String> Param) {

        return requestForTrainingRepository.findAll().stream().filter(
                        (p) ->
                                CompareDate(
                                        p.getDateOperation(),
                                        Param.get("StartDateOperation"),
                                        Param.get("EndDateOperation")
                                )
                )
                .map(el -> itemMapperImpl.RequestForTrainingToContract(el))
                .collect(Collectors.toList());
    }

    private Boolean CompareDate(LocalDate date, String data1, String data2) {
        return -1 < date.compareTo(LocalDate.parse(data1)) & date.compareTo(LocalDate.parse(data2)) < 1;
    }
}

