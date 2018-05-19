package com.nexus.locum.locumnexus.fragments.hometabs;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.FinancialProfileActivity;
import com.nexus.locum.locumnexus.PersonalProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilelDetailsLocums extends Fragment {

    CardView cv_personal,cv_professional ,cv_finance,cv_preference;
    TextView tvPersonalProfile,tv_professional ,tv_finance,tv_preference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_profile_details_locums, container, false);
        cv_personal = (CardView)convertView.findViewById(R.id.cv_personal);
        cv_professional = (CardView)convertView.findViewById(R.id.cv_professional);
        cv_finance = (CardView)convertView.findViewById(R.id.cv_finance);
        cv_preference = (CardView)convertView.findViewById(R.id.cv_preference);

        tvPersonalProfile = (TextView)convertView.findViewById(R.id.tvPersonalProfile);
        tv_professional = (TextView)convertView.findViewById(R.id.tv_professional);
        tv_finance = (TextView)convertView.findViewById(R.id.tv_finance);
        tv_preference = (TextView)convertView.findViewById(R.id.tv_preference);


        cv_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PersonalProfileActivity.class));
            }
        });

        cv_professional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfessionalProfileActivity.class));
            }
        });

        cv_finance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FinancialProfileActivity.class));
            }
        });


        if(!CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("isMainCompleted").getAsBoolean()
                || !CONST_PROFILE_JSON.get("Personal").getAsJsonObject().get("isAddressCompleted").getAsBoolean()){
            tvPersonalProfile.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_warning,0);
        }else{
            tvPersonalProfile.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked_green,0);
        }

        if(!CONST_PROFILE_JSON.get("professional").getAsJsonObject().get("isProfessionalCompleted").getAsBoolean()){
            tv_professional.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_warning,0);
        }else{
            tv_professional.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked_green,0);
        }

        if(!CONST_PROFILE_JSON.get("financial").getAsJsonObject().get("isFinancialCompleted").getAsBoolean()){
            tv_finance.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_warning,0);
        }else{
            tv_finance.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked_green,0);
        }

        return convertView;
    }

}
