package com.clickeat.restaurant.click_eatrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_LOGINKEY;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener{
    private ImageView imgView;

    private Animation fadeIn;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imgView = (ImageView) findViewById(R.id.imgView);
       /* Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();*/

        CONST_SHAREDPREFERENCES  = getBaseContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);



        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        imgView.setAnimation(fadeIn);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep (2500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    if(CONST_SHAREDPREFERENCES.getString(PREF_LOGINKEY,"").equalsIgnoreCase("yes")){
                        startActivity(new Intent(SplashScreen.this,NavigationMainScreen.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashScreen.this, Login.class));
                        finish();
                    }

                   /* Intent intent = new Intent(SplashScreen.this,Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);*/
                }
            }
        };
        timerThread.start();

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
