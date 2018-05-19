package com.clickeat.restaurant.click_eatrestaurant;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.FloorModel;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.RoomModel;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.TableModel;
import com.clickeat.restaurant.click_eatrestaurant.adapters.customspinner.FloorSpinnerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.customspinner.RoomSpinnerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.customspinner.TableSpinnerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPI;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_ID;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;

public class AddUnavailableTableActivity extends FragmentActivity {

    EditText et_reason;
    Button btnConfirm,btnReset;

    public static ArrayList<FloorModel> arrayListFloor = new ArrayList<FloorModel>();
    public static FloorSpinnerAdapter spinnerFloorAdapter;

    public static ArrayList<RoomModel> arrayListRoom = new ArrayList<RoomModel>();
    public static RoomSpinnerAdapter spinnerRoomAdapter;

    public static ArrayList<TableModel> arrayListTable = new ArrayList<TableModel>();
    public static TableSpinnerAdapter spinnerTableAdapter;

    Date currentTime,currentTimeNext;
    CardView cv_From,cv_To;
    TextView tvFromDate;
    TextView tvToDate;
    Spinner sp_floor,sp_room,sp_table;
    JsonObject tableDataRequestBodyJson = new JsonObject();

    ImageView imgClose;
    Date dateFrom,dateTo;
    SharedPreferences CONST_SHAREDPREFERENCES;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    Timepoint[] enableTimes = null;

    String selectedDateFrom,selectedTimeFrom;
    String selectedDateTo,selectedTimeTo;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_unavailable_table);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        sp_floor = (Spinner)this.findViewById(R.id.sp_floor);
        sp_room = (Spinner)this.findViewById(R.id.sp_room);
        sp_table = (Spinner)this.findViewById(R.id.sp_table);

        et_reason = (EditText)this.findViewById(R.id.et_reason);
        btnConfirm = (Button) this.findViewById(R.id.btnConfirm);
        btnReset = (Button) this.findViewById(R.id.btnReset);
        imgClose = (ImageView) this.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddUnavailableTableActivity.this,AddUnavailableTableActivity.class));
                finish();
            }
        });



        // Initializing an ArrayAdapter
        spinnerFloorAdapter = new FloorSpinnerAdapter(AddUnavailableTableActivity.this,
                R.layout.custom_spinner_items_with_left_image,arrayListFloor);
        spinnerFloorAdapter.setDropDownViewResource(R.layout.onlytextview);
        sp_floor.setAdapter(spinnerFloorAdapter);

        // Initializing an ArrayAdapter
        spinnerRoomAdapter = new RoomSpinnerAdapter(AddUnavailableTableActivity.this,
                R.layout.custom_spinner_items_with_left_image,arrayListRoom);
        spinnerRoomAdapter.setDropDownViewResource(R.layout.onlytextview);
        sp_room.setAdapter(spinnerRoomAdapter);

        // Initializing an ArrayAdapter
        spinnerTableAdapter = new TableSpinnerAdapter(AddUnavailableTableActivity.this,
                R.layout.custom_spinner_items_with_left_image,arrayListTable);
        spinnerTableAdapter.setDropDownViewResource(R.layout.onlytextview);
        sp_table.setAdapter(spinnerTableAdapter);

        new GetFloors().execute();


        arrayListFloor.clear();
        arrayListFloor.add(new FloorModel("1", "Select Floor",null));
        arrayListRoom.clear();
        arrayListRoom.add(new RoomModel("1", "Select Room",null));
        arrayListTable.clear();
        arrayListTable.add(new TableModel("1", "Select Table",null));



        cv_From = (CardView)this.findViewById(R.id.cv_From);
        cv_To = (CardView)this.findViewById(R.id.cv_To);

        currentTime = Calendar.getInstance().getTime();
        dateFrom = currentTime;

        tvFromDate = (TextView)this.findViewById(R.id.tvFromDate);
        //tvFromTime = (TextView)this.findViewById(R.id.tvFromTime);
        tvFromDate.setText(Html.fromHtml("<font size='10' color='#616161'>"+
                UtilsFunctions.convertDateToString(currentTime,"EEE , dd MMM yy")
                +"</font><font size='20' color='#000000'>"
                +"\n"+UtilsFunctions.convertDateToString(currentTime,"HH:mm")
                +"</font>"));

        tvToDate = (TextView)this.findViewById(R.id.tvToDate);
        Calendar cNext = Calendar.getInstance();
        cNext.setTime(currentTime);
        cNext.add(Calendar.HOUR_OF_DAY,1);
        currentTimeNext = cNext.getTime();
        dateTo = currentTimeNext;


        //tvToTime = (TextView)this.findViewById(R.id.tvToTime);
        tvToDate.setText(Html.fromHtml("<font size='10' color='#616161'>"+
                UtilsFunctions.convertDateToString(currentTimeNext,"EEE , dd MMM yy")
                +"</font><font size='20' color='#000000'>"
                +"\n"+UtilsFunctions.convertDateToString(currentTime,"HH:mm")
                +"</font>"));

        String TAG ="date Time piker";


        cv_From.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvFromDate.getError()!=null){
                    tvFromDate.setError(null);
                }


                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int date = c.get(Calendar.DATE);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int dayHour = c.get(Calendar.HOUR_OF_DAY);
                int dayMin = c.get(Calendar.MINUTE);
                int daySec = c.get(Calendar.SECOND);

                /*
                It is recommended to always create a new instance whenever you need to show a Dialog.
                The sample app is reusing them because it is useful when looking for regressions
                during testing
                 */


                if (dpd == null) {
                    dpd = DatePickerDialog.newInstance(
                            null,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                } else {
                    dpd.initialize(
                            null,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }
                dpd.setVersion(DatePickerDialog.Version.VERSION_2 );
                dpd.setTitle("Select Date");
                dpd.setMinDate(c);


                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
                        // dateTextView.setText(date);
                        Log.e("Selected date----",date);
                        String month = monthOfYear < 10 ? "0"+monthOfYear : ""+monthOfYear;
                        String daydate = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;

                        selectedDateFrom = Integer.toString(year)+"-"+month+"-"+daydate;

                        new GetResturantsTimeslots().execute(year+"-"+month+"-"+daydate+" 12:01:00.000Z");



                        if (tpd == null) {
                            tpd = TimePickerDialog.newInstance(
                                    null,
                                    dayHour,
                                    dayMin,
                                    true);
                        } else {
                            tpd.initialize(
                                    null,
                                    dayHour,
                                    dayMin,
                                    daySec,
                                    true);
                        }



                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                String secondString = second < 10 ? "0"+second : ""+second;
                                String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
                                //Log.e("Selected time----",time);
                                selectedTimeFrom = hourString+":"+minuteString;


                                Date date = UtilsFunctions.convertStringToDate(
                                        selectedDateFrom +" "+selectedTimeFrom
                                        ,"yyyy-MM-dd HH:mm");

                                //tvToTime = (TextView)this.findViewById(R.id.tvToTime);
                                tvFromDate.setText(Html.fromHtml("<font size='10' color='#616161'>"+
                                        UtilsFunctions.convertDateToString(date,"EEE , dd MMM yy")
                                        +"</font><font size='20' color='#000000'>"
                                        +"\n"+UtilsFunctions.convertDateToString(date,"HH:mm")
                                        +"</font>"));
                            }
                        });


                    }
                });
            }
        });

        cv_To.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvToDate.getError()!=null){
                    tvToDate.setError(null);
                }

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int date = c.get(Calendar.DATE);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int dayHour = c.get(Calendar.HOUR_OF_DAY);
                int dayMin = c.get(Calendar.MINUTE);
                int daySec = c.get(Calendar.SECOND);

                /*
                It is recommended to always create a new instance whenever you need to show a Dialog.
                The sample app is reusing them because it is useful when looking for regressions
                during testing
                 */


                if (dpd == null) {
                    dpd = DatePickerDialog.newInstance(
                            null,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                } else {
                    dpd.initialize(
                            null,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );
                }
                dpd.setVersion(DatePickerDialog.Version.VERSION_2 );
                dpd.setTitle("Select Date");
                dpd.setMinDate(c);


                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
                        // dateTextView.setText(date);
                        Log.e("Selected date----",date);
                        String month = monthOfYear < 10 ? "0"+monthOfYear : ""+monthOfYear;
                        String daydate = dayOfMonth < 10 ? "0"+dayOfMonth : ""+dayOfMonth;

                        selectedDateTo = Integer.toString(year)+"-"+month+"-"+daydate;

                        new GetResturantsTimeslots().execute(year+"-"+month+"-"+daydate+" 12:01:00.000Z");



                        if (tpd == null) {
                            tpd = TimePickerDialog.newInstance(
                                    null,
                                    dayHour,
                                    dayMin,
                                    true);
                        } else {
                            tpd.initialize(
                                    null,
                                    dayHour,
                                    dayMin,
                                    daySec,
                                    true);
                        }



                        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                String secondString = second < 10 ? "0"+second : ""+second;
                                String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
                                //Log.e("Selected time----",time);
                                selectedTimeTo = hourString+":"+minuteString;


                                Date date = UtilsFunctions.convertStringToDate(
                                        selectedDateTo +" "+selectedTimeTo
                                        ,"yyyy-MM-dd HH:mm");

                                //tvToTime = (TextView)this.findViewById(R.id.tvToTime);
                                tvToDate.setText(Html.fromHtml("<font size='10' color='#616161'>"+
                                        UtilsFunctions.convertDateToString(date,"EEE , dd MMM yy")
                                        +"</font><font size='20' color='#000000'>"
                                        +"\n"+UtilsFunctions.convertDateToString(date,"HH:mm")
                                        +"</font>"));
                            }
                        });


                    }
                });
            }
        });

        sp_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    String selectedFloor = arrayListFloor.get(position).getName();
                    Log.e("Selected Floor",selectedFloor);
                    new GetRooms().execute(selectedFloor);
                }else if(position==0){
                    arrayListFloor.clear();
                    arrayListFloor.add(new FloorModel("1", "Select Floor",null));
                    arrayListRoom.clear();
                    arrayListRoom.add(new RoomModel("1", "Select Room",null));
                    arrayListTable.clear();
                    arrayListTable.add(new TableModel("1", "Select Table",null));


                    spinnerFloorAdapter.notifyDataSetChanged();
                    spinnerRoomAdapter.notifyDataSetChanged();
                    spinnerTableAdapter.notifyDataSetChanged();

                    new GetFloors().execute();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sp_room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    String selectedRoom = arrayListRoom.get(position).getName();
                    Log.e("selectedRoom",selectedRoom);
                    new GetTable().execute(arrayListFloor.get(sp_floor.getSelectedItemPosition()).getName(),selectedRoom);

                }else if(position==0){
                    arrayListRoom.clear();
                    arrayListRoom.add(new RoomModel("1", "Select Room",null));
                    arrayListTable.clear();
                    arrayListTable.add(new TableModel("1", "Select Table",null));

                    spinnerRoomAdapter.notifyDataSetChanged();
                    spinnerTableAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position>0){
                    tableDataRequestBodyJson = arrayListTable.get(position).getJson();
                }else if(position==0){
                    tableDataRequestBodyJson = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e("fromtime",tvFromDate.getText().toString().trim());

                dateFrom = UtilsFunctions.convertStringToDate(tvFromDate.getText().toString().trim(),"EEE , dd MMM yy HH:mm");
                dateTo = UtilsFunctions.convertStringToDate(tvToDate.getText().toString().trim(),"EEE , dd MMM yy HH:mm");

                if(dateTo.before(dateFrom)){
                    tvToDate.setError("Invalid Date Time.");
                }else if(sp_floor.getSelectedItemPosition()==0){
                    ((TextView)sp_floor.getSelectedView()).setError("Select Floor !");
                }else if(sp_room.getSelectedItemPosition()==0){
                    ((TextView)sp_floor.getSelectedView()).setError("Select Room !");
                }else if(sp_table.getSelectedItemPosition()==0){
                    ((TextView)sp_floor.getSelectedView()).setError("Select Table !");
                }else if(et_reason.getText().length()<=0){
                    et_reason.setError("Required !");
                }else {

                    JsonObject bodyJson= new JsonObject();
                    bodyJson.addProperty("floor",tableDataRequestBodyJson.get("floorNo").getAsString());
                    bodyJson.addProperty("room",tableDataRequestBodyJson.get("roomNo").getAsString());
                    bodyJson.add("table_data",tableDataRequestBodyJson);



                    String startBookingDate = UtilsFunctions.convertDateStringToString(tvFromDate.getText().toString().trim()
                            ,"EEE , dd MMM yy HH:mm","yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");
                    String startTimeSlot =UtilsFunctions.convertDateStringToString(tvFromDate.getText().toString().trim()
                            ,"EEE , dd MMM yy HH:mm","HH:mm");;

                    String endBookingDate = UtilsFunctions.convertDateStringToString(tvToDate.getText().toString().trim()
                            ,"EEE , dd MMM yy HH:mm","yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'");
                    String endTimeSlot = UtilsFunctions.convertDateStringToString(tvToDate.getText().toString().trim()
                            ,"EEE , dd MMM yy HH:mm","HH:mm");
                    String unavailableTableReason = et_reason.getText().toString();


                   bodyJson.addProperty("startDate",startBookingDate);
                   bodyJson.addProperty("startTimeSlot",startTimeSlot);
                   bodyJson.addProperty("endDate",endBookingDate);
                   bodyJson.addProperty("endTimeSlot",endTimeSlot);
                   bodyJson.addProperty("unavailableTableReason",unavailableTableReason);

                    new UnavialableTable().execute(bodyJson);



                }
            }
        });



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    //ProgressDialog progressDialog;
    String resTimSlots;
    private class GetResturantsTimeslots extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            // progressDialog = ProgressDialog.show(AddUnavailableTableActivity.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                JsonObject reqObj = new JsonObject();
                reqObj.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
            reqObj.addProperty("date",parametros[0].toString());
            reqObj.addProperty("todayPassedTimeFilter",0);

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API
                        +"restaurant-details/getRestaurantTimeslots", reqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resTimSlots=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resTimSlots;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES TimeSlots---", resTimSlots);

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int date = c.get(Calendar.DATE);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int dayHour = c.get(Calendar.HOUR_OF_DAY);
                int dayMin = c.get(Calendar.MINUTE);
                int daySec = c.get(Calendar.SECOND);


                //timePickerDialog.setTimeInterval(1, 30, 60);

                JsonArray TimeSlotArray = new JsonParser().parse(resTimSlots).getAsJsonArray();

                enableTimes = new Timepoint[TimeSlotArray.size()];

                for (int a=0;a<TimeSlotArray.size();a++){
                    String timeslot = TimeSlotArray.get(a).getAsJsonObject().get("timeslot").getAsString();

                    String[] separatedTimes = timeslot.split(":");
                    String HH = separatedTimes[0];
                    String mm = separatedTimes[1];

                    Log.e("Times slots---",HH+":"+mm);
                    enableTimes[a] = new Timepoint(Integer.parseInt(HH),Integer.parseInt(mm));
                }

                tpd.setSelectableTimes( enableTimes );
                tpd.show(getFragmentManager(), "TimePickerDialog");




            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

    String resGEtfloors;
    private class GetFloors extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
           // progressDialog = ProgressDialog.show(AddUnavailableTableActivity.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject reqObj = new JsonObject();
                reqObj.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"restaurant-details/getRestaurantFloors", reqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGEtfloors=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGEtfloors;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resGEtfloors---", resGEtfloors);

                String strArray = resGEtfloors.substring(1,2);
                int totalFloor = Integer.parseInt(strArray)+1;


                arrayListFloor.clear();
                arrayListFloor.add(new FloorModel("1", "Select Floor",null));

                for(int a=0;a<totalFloor;a++){
                    arrayListFloor.add(new FloorModel(String.valueOf(a), String.valueOf(a),null));

                }
                spinnerFloorAdapter.notifyDataSetChanged();


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }

    String resGEtrooms;
    private class GetRooms extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            /*progressDialog = ProgressDialog.show(
                    AddUnavailableTableActivity.this, "Loading", "Please Wait...", true, false);*/

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject reqObj = new JsonObject();
                reqObj.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                reqObj.addProperty("floor",parametros[0].toString());

                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"restaurant-details/getRestaurantRooms", reqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGEtrooms=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGEtrooms;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resGEtrooms---", resGEtrooms);

                String strArray = resGEtrooms.substring(1,2);
                int totalRoom = Integer.parseInt(strArray);


                arrayListRoom.clear();
                arrayListRoom.add(new RoomModel("1", "Select Room",null));


                for(int a=1;a<=totalRoom;a++){
                    arrayListRoom.add(new RoomModel(String.valueOf(a), String.valueOf(a),null));

                }
                spinnerRoomAdapter.notifyDataSetChanged();



            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //progressDialog.dismiss();
        }
    }


    String resGetTable;
    private class GetTable extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            //progressDialog = ProgressDialog.show(AddUnavailableTableActivity.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject reqObj = new JsonObject();
                reqObj.addProperty("restaurant_id",CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""));
                reqObj.addProperty("floor",parametros[0].toString());
                reqObj.addProperty("room",parametros[1].toString());
                reqObj.addProperty("numberOfPeople",2);
                String responseUSerTitles = ResponseAPI(getBaseContext(),Const.SERVER_URL_API +"restaurant-details/getTables", reqObj.toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGetTable=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGetTable;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resGetTable---", resGetTable);

                JsonParser parse = new JsonParser();
                JsonArray responseArray = parse.parse(resGetTable).getAsJsonArray();
                arrayListTable.clear();
                arrayListTable.add(new TableModel(Integer.toString(0), "Select Table",null));

                for(int a=0;a<responseArray.size();a++){

                    Integer tableNum = responseArray.get(a).getAsJsonObject().get("tableNum").getAsInt();
                    Integer tableCapacity = responseArray.get(a).getAsJsonObject().get("tableCapacity").getAsInt();
                    String tableId = responseArray.get(a).getAsJsonObject().get("tableId").getAsString();
                    Integer roomNo = responseArray.get(a).getAsJsonObject().get("roomNo").getAsInt();
                    Integer floorNo = responseArray.get(a).getAsJsonObject().get("floorNo").getAsInt();

                    arrayListTable.add(new TableModel(Integer.toString(a), Integer.toString(tableNum),responseArray.get(a).getAsJsonObject()));
                }


                spinnerTableAdapter.notifyDataSetChanged();


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

           // progressDialog.dismiss();
        }
    }

    String resUnavilabletable;
    ProgressDialog progressDialogtableUnavailable;
    private class UnavialableTable extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialogtableUnavailable = ProgressDialog.show(
                    AddUnavailableTableActivity.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {


                String responseUSerTitles = ResponseAPIWithHeader(getBaseContext(),
                        Const.SERVER_URL_API +"restaurant-details/saveUnavilableTable/" +CONST_SHAREDPREFERENCES.getString(PREF_USER_ID,""),
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,""),
                        parametros[0].toString(),
                        "post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUnavilabletable=responseUSerTitles;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resUnavilabletable;
        }


        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);


            try{
                Log.i("RES resUnavilabletable---", resUnavilabletable);
                //if(res)

                startActivity(new Intent(AddUnavailableTableActivity.this,UnavailableTableActivity.class));
                finish();

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressDialogtableUnavailable.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddUnavailableTableActivity.this,UnavailableTableActivity.class));
        finish();
    }
}
