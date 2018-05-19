package com.clickeat.restaurant.click_eatrestaurant.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.UnavailableTablesModel;
import com.clickeat.restaurant.click_eatrestaurant.R;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions;
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
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions.convertDateStringToString;

public class UnavailableTableRecyclerAdapter extends
        RecyclerView.Adapter<UnavailableTableRecyclerAdapter.MyViewHolder> {

    private List<UnavailableTablesModel> unavailableTablesModel;
    private Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;
    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cv_unavailableTable;
        TextView tv_fromDateTime,tvtoDateTime ,tvFloorrNo,tvRoomNo ,tvTableNo,tvreason ;
        SwitchCompat swcAvialable;

        public MyViewHolder(View view) {
            super(view);

            tv_fromDateTime = (TextView) view.findViewById(R.id.tv_fromDateTime);
            tvtoDateTime = (TextView) view.findViewById(R.id.tvtoDateTime);
            tvFloorrNo = (TextView) view.findViewById(R.id.tvFloorrNo);
            tvRoomNo = (TextView) view.findViewById(R.id.tvRoomNo);
            tvTableNo = (TextView) view.findViewById(R.id.tvTableNo);
            tvreason = (TextView) view.findViewById(R.id.tvreason);
            swcAvialable = (SwitchCompat) view.findViewById(R.id.swcAvialable);

            cv_unavailableTable = (CardView) view.findViewById(R.id.cv_unavailableTable);

            CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        }
    }

    public UnavailableTableRecyclerAdapter(List<UnavailableTablesModel> unavailableTablesModel, Context mContext) {
        this.unavailableTablesModel = unavailableTablesModel;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final UnavailableTablesModel dataModel = unavailableTablesModel.get(position);
        holder.tv_fromDateTime.setText(UtilsFunctions.convertDateStringToString(dataModel.getStartTime()
                ,"yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'","dd MMM")
                +" "+dataModel.getStartTimeSlot());
        holder.tvtoDateTime.setText(UtilsFunctions.convertDateStringToString(dataModel.getEndTime()
                ,"yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'","dd MMM")
                +" "+dataModel.getEndTimeSlot());

        holder.tvFloorrNo.setText(dataModel.getTable_data().get("floorNo").getAsString().length()<=1
                ?"0"+dataModel.getTable_data().get("floorNo").getAsString() : dataModel.getTable_data().get("floorNo").getAsString() );
        holder.tvRoomNo.setText(dataModel.getTable_data().get("roomNo").getAsString().length()<=1
                ?"0"+dataModel.getTable_data().get("roomNo").getAsString() : dataModel.getTable_data().get("roomNo").getAsString());
        holder.tvTableNo.setText(dataModel.getTable_data().get("tableNum").getAsString().length()<=1
                ?"0"+dataModel.getTable_data().get("tableNum").getAsString() : dataModel.getTable_data().get("tableNum").getAsString());
        holder.tvreason.setText(dataModel.getUnavailableTableReason());
        holder.swcAvialable.setChecked(false);

        holder.swcAvialable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(mContext);
                    }
                    builder.setTitle("Available Table ?")
                            .setMessage("Are you sure you want to available this table?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete


                                    JsonObject reqJson = new JsonObject();
                                    reqJson.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                                    reqJson.addProperty("unavailableObjectId",dataModel.getMainJson().get("_id").getAsString());

                                    unavailableTablesModel.remove(position);
                                    notifyItemRemoved(position);
                                    //notifyDataSetChanged();


                                    new UpdateUnavailableTable().execute(reqJson);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    holder.swcAvialable.setChecked(false);
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return unavailableTablesModel.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_unavailable_tables, parent, false);
        return new MyViewHolder(v);
    }

    static String resUpdateBookingsDetails;
    ProgressDialog progressDialog;
    public class UpdateUnavailableTable extends AsyncTask<Object, Void, String> {

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


                String status = parametros[0].toString();


                String responseTableUnavialbe = ResponseAPIWithHeader(mContext,Const.SERVER_URL_API
                        +"restaurant-details/makeAvailableTable"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , parametros[0].toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdateBookingsDetails=responseTableUnavialbe;




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

               /* for(int a=0;a<rootObjArray.size();a++){

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
                }*/


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }
}