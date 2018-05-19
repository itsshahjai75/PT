package com.clickeat.restaurant.click_eatrestaurant.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions;


@SuppressLint("AppCompatCustomView")
public class UserTextView extends TextView{

	public UserTextView(Context context) {
		super(context);
		this.setTypeface(UtilsFunctions.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

	public UserTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(UtilsFunctions.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

	public UserTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(UtilsFunctions.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

}
