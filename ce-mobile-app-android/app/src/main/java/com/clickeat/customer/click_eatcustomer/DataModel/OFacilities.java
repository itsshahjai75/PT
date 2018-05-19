package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 13/12/17.
 */

public class OFacilities {
    protected String name;
    protected String icon;
    protected Boolean status;

    public OFacilities(String name, String icon, Boolean status) {
        this.name = name;
        this.icon = icon;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
