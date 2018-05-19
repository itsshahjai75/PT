package com.clickeat.restaurant.click_eatrestaurant.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions.convertDateStringToString;

public class UpcomingBookingsTableRecyclerAdapter extends
        RecyclerView.Adapter<UpcomingBookingsTableRecyclerAdapter.MyViewHolder> {

    private List<AwaitingUpcommingBookTable> UpcomingBookingList;
    private Context mContext;
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

            btnListAccept.setVisibility(View.GONE);
            btnListReject.setVisibility(View.GONE);
            btnListSuggest.setVisibility(View.GONE);

            CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        }
    }

    public UpcomingBookingsTableRecyclerAdapter(List<AwaitingUpcommingBookTable> UpcomingBookingList, Context mContext) {
        this.UpcomingBookingList = UpcomingBookingList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final AwaitingUpcommingBookTable dataModel = UpcomingBookingList.get(position);

        String date = convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","dd");
        holder.txtListDate.setText(date);

        String month =convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","MMM");
        holder.txtListMonth.setText(month);

        String time = convertDateStringToString(dataModel.getDate(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","hh:mm aa");

        holder.txttime.setText(dataModel.getBookingTimeFrom()+"  to  "+ dataModel.getBookingTimeTo());

        holder.txtListName.setText(dataModel.getName());
        holder.txtListEmail.setText(dataModel.getEmail());
        holder.txtListPhone.setText(dataModel.getMobileNo());
        holder.txtTableNo.setText(dataModel.getTableNo());
        holder.txtListPeople.setText(dataModel.getNumPeople());


        holder.btnListAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new UpdateBookingDetails().execute("Confirmed",dataModel.getBookingId(),holder.getAdapterPosition());

            }
        });

        holder.btnListReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return UpcomingBookingList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_booking_table, parent, false);
        return new MyViewHolder(v);
    }

  /*  static String resUpdateBookingsDetails;
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

                String responseUpcomming = ResponseAPIWithHeader(mContext,Const.SERVER_URL_API +"bookatables/search/BookingData"
                        ,Const.CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , ReqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdateBookingsDetails=responseUpcomming;

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
               // JsonArray rootObjArray = parser.parse(resUpdateBookingsDetails).getAsJsonArray();

               *//* for(int a=0;a<rootObjArray.size();a++){

                    String _id = rootObjArray.get(a).getAsJsonObject().get("_id").getAsString();
                    String bookingDate = rootObjArray.get(a).getAsJsonObject().get("bookingDate").getAsString();
                    String numOfPeople = rootObjArray.get(a).getAsJsonObject().get("numOfPeople").getAsString();

                    JsonObject table_data = rootObjArray.get(a).getAsJsonObject().get("table_data").getAsJsonObject();
                    String tableNum = Integer.toString(table_data.get("tableNum").getAsInt());
                    String tableCapacity = Integer.toString(table_data.get("tableCapacity").getAsInt());
                    String tableId =table_data.get("tableId").getAsString();
                    String roomNo = Integer.toString(table_data.get("roomNo").getAsInt());


                    String customerName = rootObjArray.get(a).getAsJsonObject().get("customerName").getAsString();
                    String customerEmail = rootObjArray.get(a).getAsJsonObject().get("customerEmail").getAsString();
                    String customerPhone = rootObjArray.get(a).getAsJsonObject().get("customerPhone").getAsString();
                    String bookingStartTime = rootObjArray.get(a).getAsJsonObject().get("bookingStartTime").getAsString();
                    String bookingEndTime = rootObjArray.get(a).getAsJsonObject().get("bookingEndTime").getAsString();
                    String restaurant_id = rootObjArray.get(a).getAsJsonObject().get("restaurant_id").getAsString();
                    String restaurantName = rootObjArray.get(a).getAsJsonObject().get("restaurantName").getAsString();
                    String userid = rootObjArray.get(a).getAsJsonObject().get("userid").getAsString();
                    String flag = rootObjArray.get(a).getAsJsonObject().get("flag").getAsString();
                    String status = rootObjArray.get(a).getAsJsonObject().get("status").getAsString();
                    String __v = rootObjArray.get(a).getAsJsonObject().get("__v").getAsString();
                    dataModels.add(new AwaitingUpcommingBookTable(_id,bookingDate,  customerName, customerEmail,customerPhone,tableNum, numOfPeople));
                }*//*


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }*/
}