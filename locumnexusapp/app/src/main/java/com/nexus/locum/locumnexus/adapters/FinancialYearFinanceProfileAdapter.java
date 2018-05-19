package com.nexus.locum.locumnexus.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.FinancialProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.modelPOJO.FinanceYearFinanceProfileModel;
import com.nexus.locum.locumnexus.modelPOJO.PensionFinanceProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;
import com.nexus.locum.locumnexus.utilities.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

public class FinancialYearFinanceProfileAdapter extends
        RecyclerView.Adapter<FinancialYearFinanceProfileAdapter.MyViewHolder>  {

    private List<FinanceYearFinanceProfileModel> pensionRateList;

    private Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;

    /**
     * View holder class
     */

    public FinancialYearFinanceProfileAdapter(ArrayList<FinanceYearFinanceProfileModel> pensinList, Context mContext) {
        this.pensionRateList = pensinList;
        this.mContext = mContext;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final FinanceYearFinanceProfileModel dataModel = pensionRateList.get(position);

        String cureentformateDate = "yyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";
        String parseDateformate = "dd/MMM/yyyy";

        holder.tvfinancialcYear.setText(dataModel.getFinancialYear());
        holder.tvstartdate.setText(Utils.convertDateStringToString(dataModel.getStart_year(),cureentformateDate,parseDateformate));
        holder.tvenddate.setText(Utils.convertDateStringToString(dataModel.getEnd_year(),cureentformateDate,parseDateformate));

        holder.tvStatus.setText(dataModel.getAccount_status());

        holder.tvtaxreturnofCurrentYTear.setText(Utils.convertDateStringToString(dataModel.getIsCurrenttaxReturn(),cureentformateDate,parseDateformate)
        );

        holder.tvtaxsubmitteddate.setText("N/A");


        holder.tvtaxsubmissionBy.setText(Utils.convertDateStringToString(dataModel.getTaxSubmisionBy(),cureentformateDate,parseDateformate));


        holder.tv_preferedMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.tv_preferedMore);
                //inflating menu from xml resource
                popup.inflate(R.menu.optionmenu_practices_profile_adapter);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                //handle menu1 click
                                // get prompts.xml view



                                LayoutInflater li = LayoutInflater.from(mContext);
                                View promptsView = li.inflate(R.layout.dialog_financialyear_financeprofile, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                final TextView tv_title = (TextView) promptsView.findViewById(R.id.tv_title);

                                final EditText et_financeyear = (EditText)promptsView.findViewById(R.id.et_financeyear);
                                final EditText et_taxreturncurentYear = (EditText)promptsView.findViewById(R.id.et_taxreturncurentYear);
                                final EditText taxsubmitBy = (EditText)promptsView.findViewById(R.id.taxsubmitBy);


                                tv_title.setText("FY : "+dataModel.getFinancialYear());

                                et_financeyear.setText(dataModel.getFinancialYear());
                                et_taxreturncurentYear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        // Get Current Date
                                        final Calendar c = Calendar.getInstance();
                                        int mYear = c.get(Calendar.YEAR);
                                        int mMonth = c.get(Calendar.MONTH);
                                        int mDay = c.get(Calendar.DAY_OF_MONTH);


                                        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                                                new DatePickerDialog.OnDateSetListener() {

                                                    @Override
                                                    public void onDateSet(DatePicker view, int year,
                                                                          int monthOfYear, int dayOfMonth) {
                                                        String selectedDate  = Utils.convertDateStringToString(
                                                                dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                                                                ,"dd-MM-yyyy","dd/MMM/yyyy");
                                                        et_taxreturncurentYear.setText(selectedDate);
                                                    }
                                                }, mYear, mMonth, mDay);
                                       // datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                                        datePickerDialog.show();

                                    }
                                });

                                taxsubmitBy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        // Get Current Date
                                        final Calendar c = Calendar.getInstance();
                                        int mYear = c.get(Calendar.YEAR);
                                        int mMonth = c.get(Calendar.MONTH);
                                        int mDay = c.get(Calendar.DAY_OF_MONTH);


                                        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                                                new DatePickerDialog.OnDateSetListener() {

                                                    @Override
                                                    public void onDateSet(DatePicker view, int year,
                                                                          int monthOfYear, int dayOfMonth) {

                                                        String selectedDate  = Utils.convertDateStringToString(
                                                                dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                                                                ,"dd-MM-yyyy","dd/MMM/yyyy");
                                                        taxsubmitBy.setText(selectedDate);

                                                    }
                                                }, mYear, mMonth, mDay);
                                        datePickerDialog.show();




                                    }
                                });



                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Save",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        // get user input and set it to result
                                                        // edit text
                                                        String  parseDateformate = "yyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";
                                                        String cureentformateDate  = "dd/MMM/yyyy";
                                                        JsonObject req = new JsonObject();

                                                        req.addProperty("account_status",dataModel.getAccount_status());
                                                        req.addProperty("financialYear",et_financeyear.getText().toString());
                                                        req.addProperty("isCurrentYear",dataModel.getIsCurrentYear());
                                                        req.addProperty("end_year",dataModel.getStart_year());
                                                        req.addProperty("start_year",dataModel.getEnd_year());
                                                        req.addProperty("isCurrenttaxReturn",
                                                                Utils.convertDateStringToString(et_taxreturncurentYear.getText().toString(),cureentformateDate,parseDateformate));
                                                        req.addProperty("taxReturnSubmisionDate", "");
                                                        req.addProperty("taxSubmisionBy",
                                                                Utils.convertDateStringToString(taxsubmitBy.getText().toString(),cureentformateDate,parseDateformate));


                                                        new UpdateFinancialYear().execute(req);
                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();

                                break;
                            case R.id.menu_delete:
                                //handle menu2 click

                                break;
                            case R.id.menu_share:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup

                popup.show();




            }
        });
    }

    @Override
    public int getItemCount() {
        return pensionRateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvfinancialcYear
        ,tv_preferedMore ,tvstartdate
        ,tvenddate ,tvtaxreturnofCurrentYTear
        ,tvtaxsubmitteddate
        ,tvtaxsubmissionBy,tvStatus;

        public MyViewHolder(View view) {
            super(view);

            tvfinancialcYear = (TextView)view.findViewById(R.id.tvfinancialcYear);
            tv_preferedMore = (TextView)view.findViewById(R.id.tv_preferedMore);
            tvstartdate = (TextView)view.findViewById(R.id.tvstartdate);
            tvenddate = (TextView)view.findViewById(R.id.tvenddate);
            tvtaxreturnofCurrentYTear = (TextView)view.findViewById(R.id.tvtaxreturnofCurrentYTear);
            tvtaxsubmitteddate = (TextView)view.findViewById(R.id.tvtaxsubmitteddate);
            tvtaxsubmissionBy = (TextView)view.findViewById(R.id.tvtaxsubmissionBy);
            tvStatus = (TextView)view.findViewById(R.id.tvStatus);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_financialyear_financprofile, parent, false);

        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);



        return new MyViewHolder(view);
    }


    String resUpdatePensionRate;
    // ProgressDialog progressDialog;
    private class UpdateFinancialYear extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                String responseUSerTitles = ResponseWithAuthAPI(mContext,
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/editFinancialYears/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                               , parametros[0].toString(),"post");

                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdatePensionRate=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUpdatePensionRate;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);
            new GetUserProfileDetails().execute();

            //progressDialog.dismiss();
        }
    }

    static String resUSerProfileDetails;
    public class GetUserProfileDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject getProfileDetails = new JsonObject();
                getProfileDetails.addProperty("_id", CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));



                String USerProfileDetails = ResponseWithAuthAPI(mContext, CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/getProfile", getProfileDetails.toString(),"post");
                resUSerProfileDetails =USerProfileDetails;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUSerProfileDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            try{
                Log.i("RES profDetails---", ""+resUSerProfileDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUSerProfileDetails).getAsJsonObject();

                //getUserDetailsJson = rootObj;

                CONST_PROFILE_JSON = rootObj;

                CONST_SHAREDPREFERENCES.edit().putBoolean(PREF_IS_PROFILECOMPLETED,rootObj.get("isProfileCompleted").getAsBoolean()).apply();




            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            FinancialProfileActivity.viewPager.getAdapter().notifyDataSetChanged();
            //notifyDataSetChanged();

        }
    }
}