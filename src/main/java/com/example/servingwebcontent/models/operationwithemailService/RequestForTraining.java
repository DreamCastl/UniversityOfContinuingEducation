package com.example.servingwebcontent.models.operationwithemailService;


import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class RequestForTraining {

    @Id
    private String numberRequest;//+

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "Client")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "Status")
    private Status status;

    @Column(name="Name_Program_Training")
    private String NameProgramTraining;//+

    @Column(name="start_Date")
    private LocalDate startDate;
    @Column(name="end_Date")
    private LocalDate endDate;
    @Column(name="date_Request")
    private LocalDate dateRequest;
    @Column(name="date_Operation")
    private LocalDate dateOperation;

    @Column(name="employer_Approved")
    private boolean employerApproved; // ?
    @Column(name="request_Confirmed")
    private boolean requestConfirmed;
    @Column(name="foundation_Of_Education")
    private String foundationOfEducation;
    private boolean paid;
    @Column(name="payment_Date")
    private LocalDate paymentDate;
    @Column(name="Payment_Received")
    private boolean  PaymentReceived;

    private String Payer; // Плательщик строкой, но вариантов не много... Можем записать в таблицу статусов и добавить к ней связь.
    // Типо статус 100 - Юр. лицо, Статус 200 - Физ.
    private String comment = "";
    @Column(name="additional_Information")
    private String additionalInformation = "";

    private static final Logger logger = LogManager.getLogger();

    public List<String> ConvertToList() {
        List<String> list = new ArrayList();

        list.add(status.getTranscription());
        list.add(NameProgramTraining);
        list.add(ParserData.ValueToString(startDate));
        list.add(ParserData.ValueToString(endDate));
        list.add(ParserData.ValueToString(numberRequest)) ;
        if (!status.getStatusName().equals("Canceled") ) {

            list.add(ParserData.ValueToString(client.getFullName()));
            list.add(ParserData.ValueToString(client.getSnils()));
            list.add(ParserData.ValueToString(client.getBithDay()));
            list.add(ParserData.ValueToString(client.getRegion()));
            list.add(ParserData.ValueToString(client.getSpecialization()));
            list.add(ParserData.ValueToString(client.getJob()));
            list.add(ParserData.ValueToString(client.getRegionJob()));
            list.add(ParserData.ValueToString(client.getPosition()));
            list.add(ParserData.ValueToString(dateRequest));
            list.add(ParserData.ValueToString(requestConfirmed));
            list.add(ParserData.ValueToString(foundationOfEducation));
            list.add(ParserData.ValueToString(employerApproved));
            list.add(ParserData.ValueToString(paid));
            list.add(ParserData.ValueToString(paymentDate));
            list.add(ParserData.ValueToString(PaymentReceived));
            list.add(ParserData.ValueToString(client.getEmail()));
            list.add(ParserData.ValueToString(client.getTelephoneNumber()));
            list.add(ParserData.ValueToString(Payer));
            list.add(ParserData.ValueToString(additionalInformation));
            list.add(ParserData.ValueToString(comment));
        }
        return list;
    }
}
