package com.example.servingwebcontent.models.operationwithemailService;


import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;


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

    @Column(name="Number_Program_Training")
    private String NumberProgramTraining;//+

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

    @Column(name="Request_Key")
    private String requestKey;

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

    public String ConvertToJsonUpor() {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(GetMapforJSON());
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private Map<String,ArrayList<Map<String,String>>> GetMapforJSON(){



        Map<String,String> mapObj = new HashMap();

        mapObj.put("cycle",ParserData.ValueToString(this.NameProgramTraining));
        mapObj.put("date_from",ParserData.ValueToString(this.startDate));
        mapObj.put("date_to",ParserData.ValueToString(this.endDate));
        mapObj.put("application_number",ParserData.ValueToString(this.numberRequest));
        mapObj.put("cycle_id",ParserData.ValueToString(this.NumberProgramTraining));
        mapObj.put("key",ParserData.ValueToString(this.requestKey));

        if (!status.getStatusName().equals("Canceled") ) {
            //   mapObj.put("status","");



            mapObj.put("full_name",ParserData.ValueToString(this.client.getFullName()));
            mapObj.put("snils",ParserData.ValueToString(this.client.getSnils()));
            mapObj.put("date_of_birth",ParserData.ValueToString(this.client.getBithDay()));
            mapObj.put("listener_region",ParserData.ValueToString(this.getClient().getRegion()));
            mapObj.put("direction",ParserData.ValueToString(this.getClient().getPosition()));
            mapObj.put("main_direction",ParserData.ValueToString(this.getClient().getSpecialization()));
            mapObj.put("place_of_work",ParserData.ValueToString(this.getClient().getRegionJob()));
            mapObj.put("job_title",ParserData.ValueToString(this.getClient().getJob()));
            mapObj.put("application_date",ParserData.ValueToString(this.getDateRequest()));
            mapObj.put("foundation_training",ParserData.ValueToString(this.getFoundationOfEducation()));
            mapObj.put("employer_approved",ParserData.ValueToString(this.isRequestConfirmed()));
            mapObj.put("email",ParserData.ValueToString(this.getClient().getEmail()));
            mapObj.put("phones",ParserData.ValueToString(this.getClient().getTelephoneNumber()));
            mapObj.put("payer",ParserData.ValueToString(this.getPayer()));

            //   mapObj.put("payment_link",);
            //     mapObj.put("amount_of_training",);
        }
        mapObj.put("comment",ParserData.ValueToString(this.getStatus().getTranscription()+ " " + this.comment));
        Map<String,ArrayList<Map<String,String>>> rez = new HashMap<>();
        ArrayList<Map<String,String>> requsts = new ArrayList<Map<String,String>>();
        requsts.add(mapObj);

        rez.put("items",requsts);


        return  rez;

    }

}
