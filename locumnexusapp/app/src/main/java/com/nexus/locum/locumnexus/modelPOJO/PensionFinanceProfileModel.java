package com.nexus.locum.locumnexus.modelPOJO;

import com.google.gson.JsonObject;

/**
 * Created by android on 26/4/18.
 */

public class PensionFinanceProfileModel {

    String amountFrom;
    String amountTo,rate;
    boolean ischecked;
    JsonObject mainObj;

    public PensionFinanceProfileModel(String amountFrom, String amountTo, String rate, boolean ischecked, JsonObject mainObj) {
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.rate = rate;
        this.ischecked = ischecked;
        this.mainObj = mainObj;
    }


    public String getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(String amountFrom) {
        this.amountFrom = amountFrom;
    }

    public String getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(String amountTo) {
        this.amountTo = amountTo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public JsonObject getMainObj() {
        return mainObj;
    }

    public void setMainObj(JsonObject mainObj) {
        this.mainObj = mainObj;
    }
}
