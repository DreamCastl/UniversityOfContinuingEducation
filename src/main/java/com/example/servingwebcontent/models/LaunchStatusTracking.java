package com.example.servingwebcontent.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LaunchStatusTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private boolean MailOperationServiceRunning = false;

    public LaunchStatusTracking() {
    }

    public LaunchStatusTracking(boolean mailOperationServiceRunning) {
        //Synchronized
                MailOperationServiceRunning = mailOperationServiceRunning;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMailOperationServiceRunning() {
        return MailOperationServiceRunning;
    }

    public void setMailOperationServiceRunning(boolean mailOperationServiceRunning) {
        MailOperationServiceRunning = mailOperationServiceRunning;
    }


}
