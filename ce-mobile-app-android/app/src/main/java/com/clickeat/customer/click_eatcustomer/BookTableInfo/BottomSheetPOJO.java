package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Intent;

/**
 * Created by pivotech on 30/10/17.
 */

public class BottomSheetPOJO {

    private String mTitle;
    private Integer mState;

    public BottomSheetPOJO(String mTitle, Integer mState) {
        this.mTitle = mTitle;
        this.mState = mState;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Integer getmState() {
        return mState;
    }

    public void setmState(Integer mState) {
        this.mState = mState;
    }
}
