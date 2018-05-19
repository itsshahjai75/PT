package com.clickeat.customer.click_eatcustomer.Search;

/**
 * Created by pivotech on 05/02/2018.
 */

public interface IOnSearchViewChangeListener {
    public static final String TAG = IOnSearchViewChangeListener.class.getSimpleName();

    public boolean onSearchViewTextChange(String newFilter);

    public void onSearchViewClose();
}
