package com.clickeat.customer.click_eatcustomer.DataModel;

import java.util.List;
import java.util.Map;

/**
 * Created by pivotech on 30/11/17.
 */

public class OCuisines {

    private String Name;
    private Boolean Status;

    public OCuisines(String name, Boolean status) {
        Name = name;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }
}
