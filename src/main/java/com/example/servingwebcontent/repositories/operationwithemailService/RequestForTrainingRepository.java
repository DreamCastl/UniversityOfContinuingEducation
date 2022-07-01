package com.example.servingwebcontent.repositories.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.RequestForTraining;
import com.example.servingwebcontent.models.operationwithemailService.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Repository
public interface RequestForTrainingRepository extends JpaRepository<RequestForTraining,String> {

    @Query("FROM RequestForTraining")
    Collection<RequestForTraining> findRequest();


}
