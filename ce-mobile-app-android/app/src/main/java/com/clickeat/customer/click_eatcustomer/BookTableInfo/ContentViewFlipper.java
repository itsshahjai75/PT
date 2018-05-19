package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by pivotech on 3/11/17.
 */

public class ContentViewFlipper extends ViewFlipper {
    public ContentViewFlipper( Context context ) {
        super( context );
    }

    public ContentViewFlipper( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        }
        catch( Exception e ) {}
    }
}
