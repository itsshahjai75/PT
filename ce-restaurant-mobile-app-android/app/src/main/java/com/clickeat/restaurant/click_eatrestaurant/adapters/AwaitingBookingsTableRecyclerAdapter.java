package com.clickeat.restaurant.click_eatrestaurant.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.ApiInterface;
import com.clickeat.restaurant.click_eatrestaurant.BookATable;
import com.clickeat.restaurant.click_eatrestaurant.BookingDetailsForm;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.OTableGrid;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.TimeSlots;
import com.clickeat.restaurant.click_eatrestaurant.MyApplication;
import com.clickeat.restaurant.click_eatrestaurant.NavigationMainScreen;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_FULL_NAME;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions.convertDateStringToString;

public class AwaitingBookingsTableRecyclerAdapter extends
        RecyclerView.Adapter<AwaitingBookingsTableRecyclerAdapter.MyViewHolder> {

    private List<AwaitingUpcommingBookTable> AwaitingBookingList;
    private Context mContext;

    private List<String> tableList;
    SharedPreferences CONST_SHAREDPREFERENCES;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtListDate,txtListMonth,txttime,txtListName,txtListEmail,txtListPhone,txtTableNo,txtListPeople;

        LinearLayout layoutDescription;
        LinearLayout layoutDate;
        CardView cv_awaiting;
        Button btnListAccept,btnListReject,btnListSuggest;

        public MyViewHolder(View view) {
            super(view);
            txtListDate = (TextView) view.findViewById(R.id.txtListDate);
            txtListMonth = (TextView) view.findViewById(R.id.txtListMonth);
            txttime = (TextView) view.findViewById(R.id.txttime);
            txtListName = (TextView) view.findViewById(R.id.txtListName);
            txtListEmail = (TextView) view.findViewById(R.id.txtListEmail);
            txtListPhone = (TextView) view.findViewById(R.id.txtListPhone);
            txtTableNo = (TextView) view.findViewById(R.id.txtTableNo);
            txtListPeople = (TextView) view.findViewById(R.id.txtListPeople);

            cv_awaiting = (CardView) view.findViewById(R.id.cv_awaiting);

            layoutDate = (LinearLayout) view.findViewById(R.id.layoutDate);
            layoutDescription = (LinearLayout) view.findViewById(R.id.layoutDescription);

            btnListAccept = (Button) view.findViewById(R.id.btnListAccept);
            btnListReject = (Button) view.findViewById(R.id.btnListReject);
            btnListSuggest = (Button) view.findViewById(R.id.btnListSuggest);

            CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);



        }
    }

    public AwaitingBookingsTableRecyclerAdapter(List<AwaitingUpcommingBookTable> AwaitingBookingList,
                                                Context mContext) {
        this.AwaitingBookingList = AwaitingBookingList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final AwaitingUpcommingBookTable dataModel = AwaitingBookingList.get(position);
        tableList = new ArrayList<>();

        final String date = convertDateStringToString(dataModel.getDate(),
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "dd");
        holder.txtListDate.setText(date);

        String month =convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","MMM");
        holder.txtListMonth.setText(month);

        String time = convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","hh:mm aa");
        holder.txttime.setText(dataModel.getBookingTimeFrom()+"  to  "+ dataModel.getBookingTimeTo());

        holder.txtListName.setText(dataModel.getName());
        holder.txtListEmail.setText(dataModel.getEmail());
        holder.txtListPhone.setText(dataModel.getMobileNo());

        String tableNos = null;
        JsonArray table_data = dataModel.getTable_dataJson();
        for (int a=0;a<table_data.size();a++){

            String tableNo= table_data.get(a).getAsJsonObject().get("tableNum").getAsString();
            String capacity = table_data.get(a).getAsJsonObject().get("tableCapacity").getAsString();

            String tableId= table_data.get(a).getAsJsonObject().get("tableId").getAsString();
            String roomNo = table_data.get(a).getAsJsonObject().get("roomNo").getAsString();


            if(tableNos==null){
                tableNos = tableNo;
            }else{
                tableNos = tableNos+","+tableNo;
            }
        }
        holder.txtTableNo.setText(tableNos);

        holder.txtListPeople.setText(dataModel.getNumPeople());

        holder.btnListAccept.setVisibility(View.VISIBLE);
        holder.btnListAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm ?");
                alertDialog.setMessage("Are you sure want to confirm request ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete(holder.getAdapterPosition());

                                new UpdateBookingDetails().execute("Confirmed",dataModel.getBookingId());
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();



                }
        });
        holder.btnListReject.setVisibility(View.VISIBLE);
        holder.btnListReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Confirm ?");
                alertDialog.setMessage("Are you sure want to cancel request ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete(holder.getAdapterPosition());
                                new UpdateBookingDetails().execute("Cancelled",dataModel.getBookingId());
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        holder.btnListSuggest.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent filter = new Intent(mContext, BookATable.class);
                filter.putExtra("restaurantId", CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                filter.putExtra("people", dataModel.getNumPeople());
                filter.putExtra("date", dataModel.getDate());
                filter.putExtra("email", dataModel.getEmail());
                filter.putExtra("mobile", dataModel.getMobileNo());
                filter.putExtra("name", dataModel.getName());
                mContext.startActivity(filter);*/

                tableList.clear();
                JsonArray table_data = dataModel.getTable_dataJson();

                for (int a=0;a<table_data.size();a++){

                    String tableNo= table_data.get(a).getAsJsonObject().get("tableNum").getAsString();
                    String capacity = table_data.get(a).getAsJsonObject().get("tableCapacity").getAsString();

                    String tableId= table_data.get(a).getAsJsonObject().get("tableId").getAsString();
                    String roomNo = table_data.get(a).getAsJsonObject().get("roomNo").getAsString();


                    tableList.add("Table "+tableNo+" ("+capacity+")");

                }

                mContext.startActivity(new Intent(mContext,BookingDetailsForm.class)
                                .putExtra("restaurantId",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""))
                                .putExtra("bookingID",dataModel.getBookingId())
                                .putExtra("date",convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                "dd-MMM-yyyy"))
                                .putExtra("userName",dataModel.getName())
                                .putExtra("noPeople",dataModel.getNumPeople())
                                .putExtra("ReqTableNos",holder.txtTableNo.getText().toString())
                                .putExtra("timeFrom",dataModel.getBookingTimeFrom())
                                .putExtra("timeTo",dataModel.getBookingTimeTo())
                                .putExtra("tableDataArray",dataModel.getTable_dataJson().toString())
                                .putExtra("MainJSONObjData", dataModel.getMainObjData().toString())
                                .putStringArrayListExtra("tableList", (ArrayList<String>) tableList)
                        );

                /*getGridTimeSlots(CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,"")
                        ,convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                "dd-MMM-yyyy"),dataModel.getName(),dataModel.getNumPeople(),
                        dataModel.getBookingTimeFrom(),
                        dataModel.getBookingTimeTo());*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return AwaitingBookingList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_booking_table, parent, false);
        return new MyViewHolder(v);
    }


    public void delete(int position) { //removes the row
        AwaitingBookingList.remove(position);
        notifyItemRemoved(position);
    }





    static String resUpdateBookingsDetails;
    ProgressDialog progressDialog;
    public class UpdateBookingDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(mContext, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(final Object... parametros) {

            try {

                JsonObject updateBooking = new JsonObject();
                String status = parametros[0].toString();

                updateBooking.addProperty("status",status);

                JsonObject ReqObj = new JsonObject();
                ReqObj.add("updatedObj",updateBooking);

                String responseUpdatBookingConfirm = ResponseAPIWithHeader(mContext,
                        Const.SERVER_URL_API +"bookatables/updateBookingData/"+parametros[1].toString(),
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,"")
                        ,ReqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdateBookingsDetails=responseUpdatBookingConfirm;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUpdateBookingsDetails;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES UpdaBookings Body", ""+resUpdateBookingsDetails);
                JsonParser parser = new JsonParser();
                JsonObject rootObj = parser.parse(resUpdateBookingsDetails).getAsJsonObject();

                if(rootObj.has("status")){
                    if(rootObj.get("status").getAsString().equalsIgnoreCase("1")){
                        /*mContext.startActivity(new Intent(BookingDetailsForm.this, NavigationMainScreen.class));
                        mContext.overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                        mContext.finish();*/
                        notifyDataSetChanged();

                    }else{
                        Toast.makeText(mContext,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
                    }
                }

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }


}