package com.example.servingwebcontent.service.workwithwebsite;


import com.example.servingwebcontent.models.dto.IncomingRequestContract;
import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.models.operationwithemailService.Status;
import com.example.servingwebcontent.repositories.operationwithemailService.RequestForTrainingRepository;
import com.example.servingwebcontent.repositories.operationwithemailService.StatusRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IncomingRequestService {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private RequestForTrainingRepository requestForTrainingRepository;

    @Autowired
    private ItemMapperImpl itemMapperImpl;
    @Autowired
    StatusRepository statusRepository;

    public List<IncomingRequestContract> getRequsts(Map<String, String> param) {
        // Givctiteria(); //TODO динамический запрос

        return requestForTrainingRepository.findAll()
                // return requestForTrainingRepository.findRequest()
                .stream().filter(
                        (p) -> Filter(p, param)
                )
                .map(el -> itemMapperImpl.RequestForTrainingToContract(el))
                .collect(Collectors.toList());

    }

    private Boolean Filter(RequestForTraining p, Map<String, String> param) {
        try {


            return CompareDate(
                    p.getDateOperation(),
                    param.get("StartDateOperation"),
                    param.get("EndDateOperation"))
                    && CompareDate(
                    Optional.ofNullable(p.getDateRequest()).orElse(LocalDate.of(1,1,1)),
                    param.get("startDate"),
                    param.get("endDate"))
                    && CheckStatus(p.getStatus(), param.get("status"))
                    && CheckParam(p,param);
        } catch (Exception e) {
            logger.error("Фильтр при получении из базы");
            return false;
        }
    }

    private boolean CheckParam(RequestForTraining p, Map<String, String> param) {
        try{
        return CheckContains(p.getNumberRequest(),param.get("numberRequest")) &&
                CheckContains(p.getNameProgramTraining(),param.get("NameProgramTraining")) &&
                ChecClientParam(p,param);
        }catch (Exception e){
            return false;
        }
    }

    private boolean ChecClientParam(RequestForTraining p, Map<String, String> param) {
        if (p.getClient() == null){
            return CheckContains("",param.get("fullName")) &&
                    CheckContains("",param.get("regionJob")) &&
                    CheckContains("",param.get("specialization")) &&
                    CheckContains("",param.get("job")) &&
                    CheckContains("",param.get("region")) &&
                    CheckContains("",param.get("position"));
        }else {
            return CheckContains(p.getClient().getFullName(),param.get("fullName")) &&
                    CheckContains(p.getClient().getRegionJob(),param.get("regionJob")) &&
                    CheckContains(p.getClient().getSpecialization(),param.get("specialization")) &&
                    CheckContains(p.getClient().getJob(),param.get("job")) &&
                    CheckContains(p.getClient().getRegion(),param.get("region")) &&
                    CheckContains(p.getClient().getPosition(),param.get("position"));
        }
    }

    private boolean CheckContains(String s, String p) {
        if (p.equals(""))  {
            return true;
        }
        else {
            return s.contains(p);
        }
    }


    private Boolean CompareDate(LocalDate date, String data1, String data2) {
        // data1.equals("")?"0001-01-01":data1
        return -1 < date.compareTo(LocalDate.parse(data1.equals("") ? "0001-01-01" : data1)) & date.compareTo(LocalDate.parse(data2.equals("") ? "3000-01-01" : data2)) < 1;
    }

    private boolean CheckStatus(Status status, String s) {

        return s.equals("") || status.equals(statusRepository.findById(s).get());
    }


    void Givctiteria() {

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "equestForTraining" );
//        EntityManager entityManager = emf.createEntityManager();
//
//        emf.close();
//        entityManager.close();
//
//        Category cat = new Category();
//        entityManager.persist(cat);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Category> query = cb.createQuery(Category.class);
//        Root<Category> c = query.from(Category.class);
//        query.select(c);
//        List<Category> resultList = em.createQuery(query).getResultList();
//        assertEquals(1, resultList.size());

    }

}

