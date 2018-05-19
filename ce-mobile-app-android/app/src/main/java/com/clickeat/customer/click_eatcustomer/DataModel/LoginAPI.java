package com.clickeat.customer.click_eatcustomer.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pivotech on 28/9/17.
 */

public class LoginAPI {
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("requestFrom")
    @Expose
    public String requestFrom;

    public LoginAPI(String email, String password, String requestFrom) {
        this.email = email;
        this.password = password;
        this.requestFrom = requestFrom;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }
}
