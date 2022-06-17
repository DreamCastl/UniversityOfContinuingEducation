package com.example.servingwebcontent.models.operationwithemailService;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Data
@NoArgsConstructor
public class Status {

    @Id
    @Column(name="status_name")
    private String statusName;

    @Column(name="transcription")
    private String transcription;

}
