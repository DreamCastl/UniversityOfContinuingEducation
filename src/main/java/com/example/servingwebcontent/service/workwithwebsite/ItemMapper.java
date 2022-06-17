package com.example.servingwebcontent.service.workwithwebsite;

import com.example.servingwebcontent.models.dto.IncomingRequestContract;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;

public interface ItemMapper {

    default IncomingRequestContract RequestForTrainingToContract(RequestForTraining requestForTraining){

        return new IncomingRequestContract();
    };
}
