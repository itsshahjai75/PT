package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 13/12/17.
 */

public class OSpecialDiet {
    protected String name;
    protected String description;
    protected Boolean status;

    public OSpecialDiet(String name, String description, Boolean status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
