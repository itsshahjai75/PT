package com.nexus.locum.locumnexus.adapters;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.FinancialProfileActivity;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.fragments.RateFinancialProfileFragment;
import com.nexus.locum.locumnexus.modelPOJO.RatesFinancialProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseAPI;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

public class RatesFinancialProfileAdapter extends RecyclerView.Adapter<RatesFinancialProfileAdapter.ViewHolder> {

    public static ArrayList<String> arrayList_prefixName = new ArrayList<String>();
    public static ArrayAdapter<String> spinnerArrayAdapter;

    private ArrayList<RatesFinancialProfileModel> mArrayList;
    Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;

    public RatesFinancialProfileAdapter(ArrayList<RatesFinancialProfileModel> arrayList, Context mContext) {
        mArrayList = arrayList;
        this.mContext = mContext;
    }

    @Override
    public RatesFinancialProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_rate_financial_type, viewGroup, false);
        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        new GetRateTypes().execute();

        spinnerArrayAdapter = new ArrayAdapter<String>(mContext,R.layout.onlytextview,arrayList_prefixName);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.onlytextview);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RatesFinancialProfileAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.tvRateType.setText(mArrayList.get(i).getRateType());
        viewHolder.tvRate.setText(mContext.getString(R.string.pound_currency)+" "+mArrayList.get(i).getAmount());

        viewHolder.tvedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectQualificationDialog(viewHolder.tvRateType.getText().toString()
                        , viewHolder.tvRate.getText().toString().substring(2,viewHolder.tvRate.getText().length()));
            }
        });

        viewHolder.tvdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject requestRate = new JsonObject();
                requestRate.addProperty("rateType", viewHolder.tvRateType.getText().toString().toLowerCase());
                requestRate.addProperty("amount", viewHolder.tvRate.getText().toString());
                requestRate.addProperty("isDelete", true);

                JsonObject ReqMain = new JsonObject();

                ReqMain.add("sessionRates",requestRate);

                new AddRateType().execute(ReqMain);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvRateType,tvRate;
        ImageView tvedit,tvdelete;

        public ViewHolder(View view) {
            super(view);

             tvRateType = (TextView) view.findViewById(R.id.tvRateType);
             tvRate = (TextView) view.findViewById(R.id.tvRate);

             tvedit = (ImageView) view.findViewById(R.id.tvedit);
             tvdelete = (ImageView) view.findViewById(R.id.tvdelete);

        }
    }



    protected void showSelectQualificationDialog(final String rateType,final String rate) {


        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        //alert.setTitle("Add Rate");
        // this is set the view from XML inside AlertDialog
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.custom_dialog_rate_add_profile, null);
        alert.setView(dialogView);

        final EditText et_amount = (EditText) dialogView.findViewById(R.id.et_amount);
        final Spinner sp_rateTypes = (Spinner) dialogView.findViewById(R.id.sp_rateTypes);

        et_amount.setText(rate);

        sp_rateTypes.setAdapter(spinnerArrayAdapter);
        sp_rateTypes.setSelection(arrayList_prefixName.indexOf(rateType));

        Button btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        Button btn_clear = (Button) dialogView.findViewById(R.id.btn_clear);

        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sp_rateTypes.getSelectedItemPosition()>0) {

                    JsonObject requestRate = new JsonObject();
                    requestRate.addProperty("rateType", sp_rateTypes.getSelectedItem().toString());
                    requestRate.addProperty("amount", et_amount.getText().toString());
                    requestRate.addProperty("isDelete", false);

                    JsonObject ReqMain = new JsonObject();

                    ReqMain.add("sessionRates",requestRate);

                    new AddRateType().execute(ReqMain);

                    dialog.dismiss();

                }else{
                    Toast.makeText(mContext,"Please select rate type.",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSelectQualificationDialog(rateType,rate);
            }
        });

        TextView tvClose = (TextView)dialogView.findViewById(R.id.tvClose);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }



    String resUserTitles,resCategory;
    JsonArray rootArrayRateType = new JsonArray();

    private class GetRateTypes extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(SignupLocum.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                String responseUSerTitles = ResponseAPI(mContext,Const.SERVER_URL_API +"rate_types", "","get");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUserTitles=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUserTitles;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES rate_types---", resUserTitles);
                JsonParser parser = new JsonParser();


                arrayList_prefixName.clear();
                rootArrayRateType = parser.parse(resUserTitles).getAsJsonArray();


                arrayList_prefixName.clear();
                arrayList_prefixName.add("Select Rate Type");

                for(int a=0; a<rootArrayRateType.size();a++){
                    String _id = rootArrayRateType.get(a).getAsJsonObject().get("_id").getAsString();
                    String name = rootArrayRateType.get(a).getAsJsonObject().get("name").getAsString();
                    String value = rootArrayRateType.get(a).getAsJsonObject().get("value").getAsString();
                    String createdBy = rootArrayRateType.get(a).getAsJsonObject().get("createdBy").getAsString();
                    String createdDate = rootArrayRateType.get(a).getAsJsonObject().get("createdDate").getAsString();
                    String __v = rootArrayRateType.get(a).getAsJsonObject().get("__v").getAsString();

                    arrayList_prefixName.add(name);
                    spinnerArrayAdapter.notifyDataSetChanged();

                }

                Collections.sort(arrayList_prefixName.subList(1,arrayList_prefixName.size()), new Comparator<String>() {
                    @Override
                    public int compare(String item, String t1) {
                        return item.compareToIgnoreCase(t1);
                    }

                });

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    String resUPdateAddress;
    ProgressDialog progressDialog;
    private class AddRateType extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait Updating...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                // Log.d("data send--",""+SignupJsonRequest.toString());

                String responseUSerTitles = ResponseWithAuthAPI(mContext,CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN, ""),
                        Const.SERVER_URL_API +"users/"+CONST_SHAREDPREFERENCES.getString(PREF_USER_ID, "")
                                +"/sessionRates/saltvalue", parametros[0].toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUPdateAddress=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUPdateAddress;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.e("RES resUPdateAddress---", resUPdateAddress);
                JsonParser parser = new JsonParser();
                JsonObject rootObjsignup = parser.parse(resUPdateAddress).getAsJsonObject();

                if(rootObjsignup.has("errors")){

                    //loadFragment(new SignupStepOne());
                }else{

                    //  startActivity(new Intent(AddNewAddress.this,));
                    new GetUserProfileDetails().execute();

                    Toast.makeText(mContext,"Rate Updated.",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
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

        }
    }
}