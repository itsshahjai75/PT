package com.nexus.locum.locumnexus.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.utilities.Const;

import static com.nexus.locum.locumnexus.SignupLocum.spinnerCategoryAdapter;
import static com.nexus.locum.locumnexus.SignupLocum.str_category;
import static com.nexus.locum.locumnexus.SignupLocum.str_categoryNumber;
import static com.nexus.locum.locumnexus.SignupLocum.str_password;
import static com.nexus.locum.locumnexus.SignupLocum.str_plan;
import static com.nexus.locum.locumnexus.SignupLocum.str_plan_type;

/**
 * A simple {@link Fragment} subclass.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class SignupStepTwo extends Fragment {

    public static LinearLayout ll_rbMonthly,ll_rbYearly;
    public static ImageView iv_planDetails;
    public static TextInputLayout TIL_password,TIL_confirmPassword,TIL_catTypeNumber;
    public static EditText et_password,et_confirmPassword,et_catTypeNumber;
    public static Spinner sp_category;
    public static RadioGroup rg_plan,rg_plantype;
    public static RadioButton rb_enterprise,rb_basic ,rb_monthly,rb_yearly;
    public static TextView tv_rb_monthly, tv_rb_yearly,tv_termscondition;
    public static boolean isTermsAccepted= false;

    TextView str_previousSelectedCategory;

    public SignupStepTwo() {
        // Required empty public constructor
    }


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View convertView = inflater.inflate(R.layout.fragment_signup_step_two, container, false);

        str_previousSelectedCategory = new TextView(getContext());
        str_previousSelectedCategory.setText("");

        ll_rbMonthly = (LinearLayout)convertView.findViewById(R.id.ll_rbMonthly);
        ll_rbYearly = (LinearLayout)convertView.findViewById(R.id.ll_rbYearly);

        iv_planDetails = (ImageView)convertView.findViewById(R.id.iv_planDetails);
        sp_category= (Spinner)convertView.findViewById(R.id.sp_category);
        TIL_catTypeNumber = (TextInputLayout)convertView.findViewById(R.id.TIL_catTypeNumber);

        et_password = (EditText)convertView.findViewById(R.id.et_password);
        et_confirmPassword = (EditText)convertView.findViewById(R.id.et_confirmPassword);
        et_catTypeNumber = (EditText)convertView.findViewById(R.id.et_catTypeNumber);


        rg_plan = (RadioGroup)convertView.findViewById(R.id.rg_plan);
        rg_plantype = (RadioGroup)convertView.findViewById(R.id.rg_plantype);

        rb_enterprise = (RadioButton) convertView.findViewById(R.id.rb_enterprise);
        rb_basic = (RadioButton)convertView.findViewById(R.id.rb_basic);
        rb_monthly = (RadioButton)convertView.findViewById(R.id.rb_monthly);
        rb_yearly = (RadioButton)convertView.findViewById(R.id.rb_yearly);

        tv_rb_monthly =  (TextView)convertView.findViewById(R.id.tv_rb_monthly);
        tv_rb_yearly =  (TextView)convertView.findViewById(R.id.tv_rb_yearly);
        tv_termscondition =  (TextView)convertView.findViewById(R.id.tv_termscondition);

        rb_enterprise.setChecked(true);
        rb_basic.setChecked(false);
        rb_enterprise.setTextColor(getResources().getColor(R.color.colorPrimary));
        rb_basic.setTextColor(getResources().getColor(R.color.md_grey_500));

        rb_monthly.setChecked(false);
        ll_rbMonthly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
        rb_monthly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.md_grey_500));

        rb_yearly.setChecked(true);
        ll_rbYearly.setBackgroundResource(R.drawable.round_small_corner_priamary_filled);
        rb_yearly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));

        tv_rb_monthly.setText(getString(R.string.pound_currency)+" 50  ");
        tv_rb_yearly.setText(" "+getString(R.string.pound_currency)+" 500 ");

        rb_monthly.setChecked(false);
        ll_rbMonthly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
        rb_monthly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.md_grey_500));

        rb_yearly.setChecked(true);
        ll_rbYearly.setBackgroundResource(R.drawable.round_small_corner_priamary_filled);
        rb_yearly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.whiteApp));


        rb_monthly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
        tv_rb_monthly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
        rb_yearly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));
        tv_rb_yearly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));



        rg_plan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int selectedId=rg_plan.getCheckedRadioButtonId();
                RadioButton radioplanButton=(RadioButton)convertView.findViewById(selectedId);

                if(radioplanButton.getText().toString().equalsIgnoreCase(rb_enterprise.getText().toString())){
                    tv_rb_monthly.setText(getString(R.string.pound_currency)+" 50  ");
                    tv_rb_yearly.setText(" "+getString(R.string.pound_currency)+" 500 ");

                }else if(radioplanButton.getText().toString().equalsIgnoreCase(rb_basic.getText().toString())){
                    tv_rb_monthly.setText(getString(R.string.pound_currency)+" 15  ");
                    tv_rb_yearly.setText(" "+getString(R.string.pound_currency)+" 150 ");
                }

                if(rb_enterprise.isChecked()==true){

                    rb_enterprise.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_basic.setTextColor(getResources().getColor(R.color.md_grey_500));

                }else if(rb_basic.isChecked()==true){

                    rb_basic.setTextColor(getResources().getColor(R.color.colorPrimary));
                    rb_enterprise.setTextColor(getResources().getColor(R.color.md_grey_500));

                }

            }
        });

        rg_plantype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int selectedId=rg_plantype.getCheckedRadioButtonId();
                RadioButton radioplanTypeButton=(RadioButton)convertView.findViewById(selectedId);

                if(radioplanTypeButton.getText().toString().equalsIgnoreCase(rb_monthly.getText().toString())){
                    rb_monthly.setChecked(true);
                    rb_monthly.setBackgroundResource(R.drawable.rounded_corner_primary_borde);
                    rb_yearly.setChecked(false);
                    rb_yearly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
                }else if(radioplanTypeButton.getText().toString().equalsIgnoreCase(rb_yearly.getText().toString())){
                    rb_monthly.setChecked(false);
                    rb_monthly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
                    rb_yearly.setChecked(true);
                    rb_yearly.setBackgroundResource(R.drawable.rounded_corner_primary_borde);
                }
            }
        });


        ll_rbMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_monthly.setChecked(true);
            }
        });

        ll_rbYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_yearly.setChecked(true);
            }
        });

        rb_monthly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rb_monthly.isChecked()==true){
                    rb_monthly.setChecked(true);
                    ll_rbMonthly.setBackgroundResource(R.drawable.round_small_corner_priamary_filled);
                    rb_monthly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.whiteApp));

                    rb_yearly.setChecked(false);
                    ll_rbYearly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
                    rb_yearly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.md_grey_500));

                    rb_monthly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));
                    tv_rb_monthly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));
                    rb_yearly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
                    tv_rb_yearly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));

                }
            }
        });

        rb_yearly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rb_yearly.isChecked()==true) {
                    rb_monthly.setChecked(false);
                    ll_rbMonthly.setBackgroundResource(R.drawable.rounded_corner_bordergrey);
                    rb_monthly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.md_grey_500));

                    rb_yearly.setChecked(true);
                    ll_rbYearly.setBackgroundResource(R.drawable.round_small_corner_priamary_filled);
                    rb_yearly.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.whiteApp));


                    rb_monthly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
                    tv_rb_monthly.setTextColor(getResources().getColor(R.color.md_grey_500,getActivity().getTheme()));
                    rb_yearly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));
                    tv_rb_yearly.setTextColor(getResources().getColor(R.color.whiteApp,getActivity().getTheme()));
                }
            }
        });



        // Initializing an ArrayAdapter
       sp_category.setAdapter(spinnerCategoryAdapter);


        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0) {
                    TIL_catTypeNumber.setVisibility(View.VISIBLE);
                    et_catTypeNumber.setVisibility(View.VISIBLE);

                    if (parent.getSelectedItem().toString().equalsIgnoreCase("Nurse(Dental)")) {
                        TIL_catTypeNumber.setHint("NMC Number");
                        et_catTypeNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                        et_catTypeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                        if(et_catTypeNumber.getError()!=null){
                            et_catTypeNumber.setError("Enter NMC Number");
                        }
                    } else if (parent.getSelectedItem().toString().equalsIgnoreCase("Nurse(Non-Dental)")) {
                        TIL_catTypeNumber.setHint("NMC Number");
                        et_catTypeNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                        et_catTypeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                        if(et_catTypeNumber.getError()!=null){
                            et_catTypeNumber.setError("Enter NMC Number");
                        }
                    } else if (parent.getSelectedItem().toString().equalsIgnoreCase("Dentist")) {
                        TIL_catTypeNumber.setHint("GDC Number");
                        et_catTypeNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                        et_catTypeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                        if(et_catTypeNumber.getError()!=null){
                            et_catTypeNumber.setError("Enter GDC Number");
                        }
                    } else if (parent.getSelectedItem().toString().equalsIgnoreCase("General Practitioner")) {
                        TIL_catTypeNumber.setHint("GMC Number");
                        et_catTypeNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_catTypeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                        if(et_catTypeNumber.getError()!=null){
                            et_catTypeNumber.setError("Enter GMC Number");
                        }
                    }

                    if(str_previousSelectedCategory.getText().toString().equalsIgnoreCase("")){
                        str_previousSelectedCategory.setText(parent.getSelectedItem().toString());
                    }

                    if(!str_previousSelectedCategory.getText().toString()
                            .equals(parent.getSelectedItem().toString())){
                        et_catTypeNumber.setText("");
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iv_planDetails.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {

               /* AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Plans");

                WebView wv = new WebView(getContext());
                wv.loadUrl(Const.SERVER_URL_ONLY+"locum-signup-plandetails");
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();*/

                WebView webView = new WebView(getContext());
                //webView.loadUrl("www.google.com");
                webView.loadUrl(Const.SERVER_URL_ONLY+"locum-signup-plandetails");
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

                webView.setWebViewClient(new MyWebViewClient());

                 Log.d("url_load Plandetails",Const.SERVER_URL_ONLY+"locum-signup-plandetails");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("PlanDetails")
                        .setView(webView)
                        .setNeutralButton("OK", null)
                        .show();
            }
        });

        tv_termscondition.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {



                WebView webView2 = new WebView(getContext());
                webView2.loadUrl(Const.SERVER_URL_ONLY+"signup-terms");
                webView2.getSettings().setJavaScriptEnabled(true);
                webView2.getSettings().setDomStorageEnabled(true);
                webView2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

                webView2.setWebViewClient(new MyWebViewClient());


                // Log.d("url_load",Const.SERVER_URL_ONLY+"locum-signup-plandetails");
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setView(webView2)
                        .setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isTermsAccepted =true;
                                tv_termscondition.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0);
                                tv_termscondition.setError(null);
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isTermsAccepted=false;
                                tv_termscondition.setError("Please Read and Accept !");
                                tv_termscondition.setCompoundDrawablesWithIntrinsicBounds( android.R.drawable.checkbox_off_background, 0,0, 0);

                            }
                        })
                        .show();

                if(isTermsAccepted==true){
                    tv_termscondition.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0);
                    tv_termscondition.setError(null);
                }else if(isTermsAccepted==false){
                    tv_termscondition.setError("Please Read and Accept !");
                    tv_termscondition.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_off_background, 0, 0, 0);

                }

            }
        });


        if(str_password.length()>0 || str_category.length()>0 || str_categoryNumber.length()>0
                || str_plan_type.length()>0 || str_plan.length()>0){

            if(str_password.length()>0){
                et_password.setText(str_password);
                et_confirmPassword.setText(str_password);
            }
            if(str_category.length()>0){
                sp_category.setSelection(spinnerCategoryAdapter.getPosition(str_category));
            }

            if(str_categoryNumber.length()>0){
                et_catTypeNumber.setText(str_categoryNumber);
            }

            if(str_plan_type.length()>0){
                if(str_plan.equalsIgnoreCase(rb_basic.getText().toString())){
                    rb_basic.setChecked(true);
                }else if(str_plan.equalsIgnoreCase(rb_enterprise.getText().toString())){
                    rb_enterprise.setChecked(true);
                }
            }

            if(str_plan_type.length()>0){
                if(str_plan_type.equalsIgnoreCase(rb_monthly.getText().toString())){
                    rb_monthly.setChecked(true);
                }else if(str_plan_type.equalsIgnoreCase(rb_yearly.getText().toString())){
                    rb_yearly.setChecked(true);
                }
            }




        }


        return convertView;
    }

    ProgressDialog prDialog;
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            view.loadUrl(url);
            /*new Thread(new Runnable()
            {
                public void run()
                {
                    view.loadUrl(url);
                }
            }).start();*/
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            prDialog = new ProgressDialog(getActivity());
            prDialog.setMessage("Please wait ...");
            prDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(prDialog!=null){
                prDialog.dismiss();
            }
        }
    }

}
