package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by pivotech on 5/1/18.
 */


public interface FoldShading {
    void onPreDraw(Canvas canvas, Rect bounds, float rotation, int gravity);

    void onPostDraw(Canvas canvas, Rect bounds, float rotation, int gravity);
}

