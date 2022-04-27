package com.example.servingwebcontent.repositories;

import com.example.servingwebcontent.models.LaunchStatusTracking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaunchStatusTrackingRepository extends CrudRepository<LaunchStatusTracking, Long> {

    @Query(value = "SELECT mail_operation_service_running, MAX(id) \n" +
            "FROM launch_status_tracking\n" +
            "GROUP BY mail_operation_service_running,id", nativeQuery = true)
    public default boolean statusLanch(){

            Iterable<LaunchStatusTracking> status = this.findAll();

            boolean rez = false;
            for (LaunchStatusTracking tracking : status) {
                rez = tracking.isMailOperationServiceRunning();
            }
            return rez;

        }
}
