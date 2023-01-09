package com.example.servingwebcontent.service.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.Client;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.repositories.operationwithemailService.ClientRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.ParserData;
import com.example.servingwebcontent.service.operationwithemailService.WorkWithEmail.WorkWithEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class RequestForTrainingService {
    @Autowired
    private RequestForTrainingRepository requestForTrainingRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private StatusRepository statusRepository;
    private static final Logger logger = LogManager.getLogger();

    public RequestForTraining addDateForMessage(Message message, WorkWithEmail workWithEmail) {
        RequestForTraining requestForTraining = new RequestForTraining();
        requestForTraining.setDateOperation(LocalDate.now());
        Random rand = new Random();
        //String.valueOf(rand.nextInt(9999)

        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random r = new Random();
        String rez = "";
        for (int i=0; i < 4; i++)
            rez += alphabet.charAt(r.nextInt(alphabet.length()));

        requestForTraining.setRequestKey(rez);

        String Content = null;
        try {
            Content = workWithEmail.GetContentMail(message);
            ParserData ParserData = new ParserData();//TODO Передалать в компоненту ?
            requestForTraining.setNumberRequest(ParserData.NumberApplicationFromContext(Content));
            requestForTraining.setNumberProgramTraining(ParserData.NumberProgrammTrainingFromContext(Content));
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy");

            requestForTraining.setStartDate(LocalDate.parse(ParserData.getWithDate(Content), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            requestForTraining.setEndDate(LocalDate.parse(ParserData.getOnDate(Content), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            requestForTraining.setNameProgramTraining(ParserData.getNameProgramm(Content));

        } catch (MessagingException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.warn("Не удалось получить контент из письма");
        } finally {
            workWithEmail.SetFlagSeen(message, false);
        }
        return requestForTraining;
    }

    public void AddDateFromNMFO(RequestForTraining requestForTraining, List<String> dataNMFO) {



        if (dataNMFO.size() == 0) {
            requestForTraining.setStatus(statusRepository.FindByKey("Canceled"));
            return;
        }

        try {
            requestForTraining.setDateRequest(ParserData.getDate(dataNMFO.get(10)));
            requestForTraining.setRequestConfirmed(false);
            requestForTraining.setFoundationOfEducation(dataNMFO.get(12));
            requestForTraining.setEmployerApproved(ParserData.EmployerApproved(dataNMFO.get(13)));
            //requestForTraining.setEmployerApproved(ParserData.NotOrYes(dataNMFO.get(13)));
            requestForTraining.setPaymentReceived(ParserData.NotOrYes(dataNMFO.get(16)));

            requestForTraining.setPaymentDate(ParserData.getDate(dataNMFO.get(15)));//
            requestForTraining.setPayer(ParserData.getPlayer(dataNMFO.get(19)));
            requestForTraining.setAdditionalInformation(dataNMFO.get(20));
            requestForTraining.setComment(dataNMFO.get(21));

            Client client = FindOrCreateClient(dataNMFO);;
            requestForTraining.setClient(client);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Client FindOrCreateClient(List<String> dataNMFO) {


        try {
            Client client = clientRepository.findClientbySNILS( dataNMFO.get(3));;
            client.setEmail(dataNMFO.get(17));
            client.setTelephoneNumber(dataNMFO.get(18));
            client.setFullName(dataNMFO.get(1));
//        client.setSNILS,dataNMFO.get(2));
            client.setBithDay(ParserData.getDate(dataNMFO.get(2)));
            client.setRegion(dataNMFO.get(4));
            client.setSpecialization(dataNMFO.get(5));
            client.setRegionJob( dataNMFO.get(8));
            client.setPosition(dataNMFO.get(9));
            client.setJob(dataNMFO.get(7));
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Ошибка создания клиента");
            return null;
        }



    }
}
