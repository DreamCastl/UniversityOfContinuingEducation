package com.example.servingwebcontent.Config.operationwithemailService;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SetSpreadSheetTable {

    @Value("${settable.id_Table}")
    private String id_Table;

    public String getID_Table() {
        return this.id_Table;
    }

}
