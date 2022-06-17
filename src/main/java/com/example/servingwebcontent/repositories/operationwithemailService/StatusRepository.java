package com.example.servingwebcontent.repositories.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status,String> {

    public default Status FindByKey(String key){

        return this.findById(key).get();


    }
}
