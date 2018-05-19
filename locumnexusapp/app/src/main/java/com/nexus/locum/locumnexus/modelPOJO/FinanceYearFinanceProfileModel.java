package com.nexus.locum.locumnexus.modelPOJO;

import com.google.gson.JsonObject;

/**
 * Created by android on 27/4/18.
 */

public class FinanceYearFinanceProfileModel {

    String taxSubmisionBy;

    String isCurrenttaxReturn;
    String account_status;

    String financialYear;
    String isCurrentYear;

    String end_year;
    String start_year;

    String _id;

    JsonObject mainJson;


    public FinanceYearFinanceProfileModel(String taxSubmisionBy, String isCurrenttaxReturn, String account_status, String financialYear,
                                          String isCurrentYear, String end_year, String start_year, String _id, JsonObject mainJson) {
        this.taxSubmisionBy = taxSubmisionBy;
        this.isCurrenttaxReturn = isCurrenttaxReturn;
        this.account_status = account_status;
        this.financialYear = financialYear;
        this.isCurrentYear = isCurrentYear;
        this.end_year = end_year;
        this.start_year = start_year;
        this._id = _id;
        this.mainJson = mainJson;
    }

    public String getTaxSubmisionBy() {
        return taxSubmisionBy;
    }

    public void setTaxSubmisionBy(String taxSubmisionBy) {
        this.taxSubmisionBy = taxSubmisionBy;
    }

    public String getIsCurrenttaxReturn() {
        return isCurrenttaxReturn;
    }

    public void setIsCurrenttaxReturn(String isCurrenttaxReturn) {
        this.isCurrenttaxReturn = isCurrenttaxReturn;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getIsCurrentYear() {
        return isCurrentYear;
    }

    public void setIsCurrentYear(String isCurrentYear) {
        this.isCurrentYear = isCurrentYear;
    }

    public String getEnd_year() {
        return end_year;
    }

    public void setEnd_year(String end_year) {
        this.end_year = end_year;
    }

    public String getStart_year() {
        return start_year;
    }

    public void setStart_year(String start_year) {
        this.start_year = start_year;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public JsonObject getMainJson() {
        return mainJson;
    }

    public void setMainJson(JsonObject mainJson) {
        this.mainJson = mainJson;
    }
}
