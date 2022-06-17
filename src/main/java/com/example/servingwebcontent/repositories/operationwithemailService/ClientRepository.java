package com.example.servingwebcontent.repositories.operationwithemailService;

import com.example.servingwebcontent.models.operationwithemailService.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    public default Client findClientbySNILS(String key) {
        return  this.findById(key).orElse(new Client(key));
    }
}
