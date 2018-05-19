package com.clickeat.customer.click_eatcustomer.DataModel;

public class OAddressList {
    protected String addressId;
    protected String addressType;
    protected String addressText;

    public OAddressList(String addressId, String addressType, String addressText) {
        this.addressId = addressId;
        this.addressType = addressType;
        this.addressText = addressText;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }
}
