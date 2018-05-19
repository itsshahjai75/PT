package com.nexus.locum.locumnexus.fragments;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.nexus.locum.locumnexus.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupStepOne extends Fragment {

    public static ArrayList<String> arrayList_prefixName = new ArrayList<String>();
    public static ArrayAdapter<String> spinnerArrayAdapter;

    public  static  Spinner sp_prefixName;
    public  static  TextInputLayout TIL_firstName,TIL_lastName,TIL_mobileno,TIL_emailid,TIL_cnfemailid;
    public  static  EditText et_firstName,et_lastName,et_email,et_cnfemail,et_mobileno;
    public  static  RadioGroup rg_gender;
    public  static  RadioButton rb_male,rb_female ,rb_other;

    public SignupStepOne() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_signup_step_one, container, false);

        sp_prefixName= (Spinner)convertView.findViewById(R.id.sp_prefixName);

        et_firstName = (EditText)convertView.findViewById(R.id.et_firstName);
        et_lastName = (EditText)convertView.findViewById(R.id.et_lastName);
        et_email = (EditText)convertView.findViewById(R.id.et_email);
        et_cnfemail = (EditText)convertView.findViewById(R.id.et_cnfemail);
        et_mobileno = (EditText)convertView.findViewById(R.id.et_mobileno);

        rg_gender = (RadioGroup)convertView.findViewById(R.id.rg_gender);


        /*arrayList_prefixName.add("Dr.");
        arrayList_prefixName.add("Mrs.");
        arrayList_prefixName.add("Miss.");
        arrayList_prefixName.add("Sir.");*/


        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(),R.layout.onlytextview,arrayList_prefixName);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.onlytextview);
        sp_prefixName.setAdapter(spinnerArrayAdapter);


        et_email.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        return String.valueOf(source).toLowerCase();
                    }
                }
        });

        et_cnfemail.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        return String.valueOf(source).toLowerCase();
                    }
                }
        });

        return convertView;
    }

}
