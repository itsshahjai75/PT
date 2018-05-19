package com.clickeat.restaurant.click_eatrestaurant.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions;

/**
 * Created by android on 16/2/18.
 */

@SuppressLint("AppCompatCustomView")
public class UserRadioButtonAdv extends RadioButton {

    @SuppressLint("ResourceType")
    public UserRadioButtonAdv(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, 0, 0);
        buttonDrawable = a.getDrawable(1);
    // We can now set a tint
        DrawableCompat.setTint(buttonDrawable, Color.RED);
    // ...or a tint list
       // DrawableCompat.setTintList(buttonDrawable, myColorStateList);
    // ...and a different tint mode
        DrawableCompat.setTintMode(buttonDrawable, PorterDuff.Mode.SRC_OVER);
        setButtonDrawable(android.R.color.transparent);
        this.setTypeface(UtilsFunctions.getFont(context, Integer.parseInt(this.getTag().toString())));

    }
    Drawable buttonDrawable;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buttonDrawable != null) {
            buttonDrawable.setState(getDrawableState());
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();

            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }

            int buttonWidth = buttonDrawable.getIntrinsicWidth();
            int buttonLeft = (getWidth() - buttonWidth) / 2;
            buttonDrawable.setBounds(buttonLeft, y, buttonLeft+buttonWidth, y + height);
            buttonDrawable.draw(canvas);
        }
    }
}