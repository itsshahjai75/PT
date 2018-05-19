package com.nexus.locum.locumnexus.modelPOJO;

import com.google.gson.JsonObject;

/**
 * Created by android on 20/4/18.
 */

public class RatesFinancialProfileModel {


    String rateType;
    String amount;
    JsonObject jsonObject;

    public RatesFinancialProfileModel(String rateType, String amount, JsonObject jsonObject) {
        this.rateType = rateType;
        this.amount = amount;
        this.jsonObject = jsonObject;
    }


    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
