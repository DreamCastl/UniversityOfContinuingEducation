package com.example.servingwebcontent.models.operationwithemailService;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Client {

    @Id
    private String snils;

    private String fullName = "";

//    @OneToMany // Одному(!) клиенту может соответствовать Много(!) заявок
//    @JoinColumn(name = "RequestForTraining")
//    private List<RequestForTraining> requestsForTraining;

    private LocalDate bithDay;
    private String region = "";
    private String specialization = "";

    private String job = "";
    private String regionJob = "";

    private String position = "";

    private String email = "";
    private String telephoneNumber = "";

    private String additionalInformation = "";
    private String comment = "";
    private String commentUniversity = "";

    public Client(String snils) {
        this.snils = snils;
    }
}
