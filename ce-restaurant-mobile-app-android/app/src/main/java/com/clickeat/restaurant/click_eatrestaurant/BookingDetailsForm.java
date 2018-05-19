package com.clickeat.restaurant.click_eatrestaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.AwaitingUpcommingBookTable;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.OTableGrid;
import com.clickeat.restaurant.click_eatrestaurant.DataModel.TimeSlots;
import com.clickeat.restaurant.click_eatrestaurant.adapters.AwaitingBookingsTableRecyclerAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.MatrixTableAdapter;
import com.clickeat.restaurant.click_eatrestaurant.adapters.MatrixTableAdapterEdited;
import com.clickeat.restaurant.click_eatrestaurant.fragments.NavTabHomeAwaiting;
import com.clickeat.restaurant.click_eatrestaurant.utilities.Const;
import com.clickeat.restaurant.click_eatrestaurant.utilities.UtilsFunctions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inqbarna.tablefixheaders.TableFixHeaders;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.clickeat.restaurant.click_eatrestaurant.adapters.MatrixTableAdapterEdited.tableAndSlotsJSONAdapter;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPI;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.APICall.ResponseAPIWithHeader;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.MyPREFERENCES;
import static com.clickeat.restaurant.click_eatrestaurant.utilities.Const.PREF_USER_TOKEN;

public class BookingDetailsForm extends Activity implements MatrixTableAdapterEdited.TableInterface {

    private ProgressDialog loadingSpinner;
    private static String restaurantId,bookingID;
    private static String restaurantName;
    private static String bookingDate;
    public static String people;
    private static String ReqTableNos;
    private static String timeFrom;
    private static String timeTo;
    TableFixHeaders tableFixHeaders;
    private Intent extras;
    private EditText input_person, input_date;
    private TextView textHeader;
    public static  TextView tv_noteforOpsManager;
    private ImageButton btnBookClose;
    private List<String> times = new ArrayList<>();
    private List<String> tables = new ArrayList<>();
    private Button btnBookDone;
    public static Button btnConfirmBooking ,btnAmendBooking,btnCancelBooking;
    MatrixTableAdapterEdited.TableInterface tableInterface;
    private HashMap<String, String> tableDataMap;


    MatrixTableAdapterEdited<String> matrixTableAdapter ;
    private Boolean isDataFound = false;

    private static Integer BOOK_GRID = 001;
    JsonArray BookedUsertableDataArray;
    JsonArray tableAndSlots = null;
    SharedPreferences CONST_SHAREDPREFERENCES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_booking_details_form);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        CONST_SHAREDPREFERENCES  = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        tv_noteforOpsManager = findViewById(R.id.tv_noteforOpsManager);
        input_person = findViewById(R.id.input_person);
        input_date = findViewById(R.id.input_date);
        textHeader = findViewById(R.id.textHeader);
        btnBookClose = findViewById(R.id.btnBookClose);
        btnBookDone = findViewById(R.id.btnBookDone);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnConfirmBooking.setVisibility(View.VISIBLE);
        btnAmendBooking = findViewById(R.id.btnAmendBooking);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);




        try {
            tableInterface = (MatrixTableAdapterEdited.TableInterface) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this
                    + " must implement OnListItemSelectedListener");
        }

        btnBookClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        extras = getIntent();
        if (extras != null) {

            /*restaurantId = extras.getStringExtra("restaurantId");
            bookingDate = extras.getStringExtra("bookDate");
            restaurantName = extras.getStringExtra("restaurantName");
            people = extras.getStringExtra("people");
            tables = extras.getStringArrayListExtra("tableList");
            String timeExtra = extras.getStringExtra("timeSlot");
           *//* times = new ArrayList<>();
            times = (ArrayList<String>) getIntent().getSerializableExtra("timeSlot");*//*
            if (timeExtra != null) {
                times = new ArrayList<>();
                times.add(timeExtra);
            }*/


            tables = extras.getStringArrayListExtra("tableList");

            restaurantId = extras.getStringExtra("restaurantId");
            bookingID = extras.getStringExtra("bookingID");
            bookingDate = extras.getStringExtra("date");
            restaurantName = extras.getStringExtra("userName");
            people = extras.getStringExtra("noPeople");
            ReqTableNos = extras.getStringExtra("ReqTableNos");
            timeFrom = extras.getStringExtra("timeFrom");
            timeTo = extras.getStringExtra("timeTo");


            textHeader.setText(restaurantName);
            input_person.setText("People: " + people);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                input_date.setText("Booking Date: " + format.format(format.parse(bookingDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date date = null;
            try {
                date = format.parse(bookingDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateTime = dateFormat.format(date);
            tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);



            String jsonArray = extras.getStringExtra("tableDataArray");

            try {
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(jsonArray);
                BookedUsertableDataArray = tradeElement.getAsJsonArray();
                //System.out.println(array.toString(2));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //JsonObject TableDataJSON = new JsonParser().parse(getIntent().getStringExtra("TableDataJSON")).getAsJsonObject();
            JsonObject MainJSONObjData = new JsonParser().parse(getIntent().getStringExtra("MainJSONObjData")).getAsJsonObject();
            getGridTimeSlots(restaurantId
                    ,bookingDate,restaurantName,people,
                    timeFrom,
                    timeTo
                    ,MainJSONObjData);

        }


        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(BookingDetailsForm.this).create();
                alertDialog.setTitle("Amend ?");
                alertDialog.setMessage("Are you sure want to confirm request ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new UpdateBookingDetails().execute("Confirmed",bookingID);
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

        btnAmendBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog alertDialog = new AlertDialog.Builder(BookingDetailsForm.this).create();
                alertDialog.setTitle("Amend ?");
                alertDialog.setMessage("Are you sure want to amend request ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String[] GetbookingDate = input_date.getText().toString().split(":");
                                String bookignDate = GetbookingDate[1].toString();
                                JsonObject mainReqJSON = new JsonObject();
                                mainReqJSON.addProperty("bookingDate",UtilsFunctions.convertDateStringToString(bookignDate,"dd-MMM-yyyy","yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

                                String bookingStartTime = "";
                                for(int a=0;a<tableAndSlotsJSONAdapter.size();a++) {

                                    JsonArray timeSlotA = tableAndSlotsJSONAdapter.get(a).getAsJsonObject().get("timeslot").getAsJsonArray();

                                    for (int ts = 0; ts < timeSlotA.size(); ts++) {
                                        String timeslot = timeSlotA.get(ts).getAsJsonObject().get("timeslot").getAsString();
                                        String status = timeSlotA.get(ts).getAsJsonObject().get("status").getAsString();
                                        if(status.equalsIgnoreCase("amend")){
                                            bookingStartTime=timeslot;
                                            break;
                                        }
                                    }
                                }
                                mainReqJSON.addProperty("bookingStartTime",bookingStartTime);

                                JsonArray tableData = new JsonArray();
                                for(int a=0;a<tableAndSlotsJSONAdapter.size();a++) {

                                    JsonObject tableA = tableAndSlotsJSONAdapter.get(a).getAsJsonObject().get("table").getAsJsonObject();

                                    String tableNumA = tableA.get("tableNum").getAsString();
                                    String tableCapacityA = tableA.get("tableCapacity").getAsString();
                                    String tableIdA = tableA.get("tableId").getAsString();
                                    String roomNoA = tableA.get("roomNo").getAsString();
                                    String floorNoA = tableA.get("floorNo").getAsString();

                                    JsonArray timeSlotA = tableAndSlotsJSONAdapter.get(a).getAsJsonObject().get("timeslot").getAsJsonArray();

                                    for (int ts = 0; ts < timeSlotA.size(); ts++) {

                                        String timeslot = timeSlotA.get(ts).getAsJsonObject().get("timeslot").getAsString();
                                        String status = timeSlotA.get(ts).getAsJsonObject().get("status").getAsString();
                                        if(status.equalsIgnoreCase("amend")){
                                            tableData.add(tableA);
                                            break;
                                        }
                                    }
                                }
                                mainReqJSON.add("table_data",tableData);


                                new AmendBookingDetails().execute(mainReqJSON,bookingID);
                                Log.e("booking ID----",bookingID);

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

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(BookingDetailsForm.this).create();
                alertDialog.setTitle("Confirm ?");
                alertDialog.setMessage("Are you sure want to cancel request ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new UpdateBookingDetails().execute("Cancelled",bookingID);
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


        /*btnBookDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<>done-", " tables ==> " + tables.toString());
                Log.d("<>done-", " timeSlots  ==> " + times.toString());
                if (tables.size() > 0 && times.size() > 0) {
                    Integer totalCapacity = 0;
                    StringBuilder tabBulider = new StringBuilder();
                    List<String> selectedList = new ArrayList<>();
                    if (tables.size() > 0) {
                        for (int i = 0; i < tables.size(); i++) {
                            for (Map.Entry<String, String> entry : tableDataMap.entrySet()) {
                                if (entry.getKey().equals(tables.get(i).toString())) {
                                    selectedList.add(entry.getValue());
                                }
                            }
                        }

                        List<OTableGrid> gridData = new ArrayList<>();
                        gridData = MyApplication.getGlobalData().getAllTableGridData();
                        for (int t = 0; t < selectedList.size(); t++) {
                            for (int tab = 0; tab < gridData.size(); tab++) {
                                if (selectedList.get(t).toString().equals(gridData.get(tab).getTableId())) {
                                    totalCapacity += gridData.get(tab).getTableCapacity();

                                   *//* if (t + 1 == selectedList.size())
                                        tabBulider.append(gridData.get(tab).getTableNum());
                                    else
                                        tabBulider.append(gridData.get(tab).getTableNum() + " , ");*//*
                                }
                            }
                        }
                        if (totalCapacity < Integer.parseInt(people)) {
                            Toast.makeText(BookingDetailsForm.this, "Table capacity does not match !", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("capacity", totalCapacity.toString());
                            if (times.size() == 1)
                                returnIntent.putExtra("timeSlot", times.get(0));
                            if (times.size() == 2)
                                returnIntent.putExtra("timeSlot", times.get(1));
                            returnIntent.putStringArrayListExtra("tableIds",(ArrayList<String>) selectedList);
                            returnIntent.putStringArrayListExtra("tableList", (ArrayList<String>) tables);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(BookingDetailsForm.this, "without selection cannot proceed further...", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void init() {

        tableDataMap = new HashMap<>();
        List<OTableGrid> gridData = new ArrayList<>();
        gridData = MyApplication.getGlobalData().getAllTableGridData();
        Log.d("<>log-", " grid data --> " + gridData.size());
        List<TimeSlots> timeSlots = new ArrayList<>();
        timeSlots = gridData.get(0).getListTimeslots();
        Log.d("<>log-", " time slots data --> " + timeSlots.size());

        String[][] shades = new String[gridData.size() + 1][timeSlots.size() + 1];
        shades[0][0] = "";
        for (int y = 1; y <= gridData.get(0).getListTimeslots().size(); y++) {
            Log.d("<>yy-", gridData.get(0).getListTimeslots().get(y - 1).getTimeslot());
            shades[0][y] = gridData.get(0).getListTimeslots().get(y - 1).getTimeslot();
        }
        for (int i = 1; i <= gridData.size(); i++) {
            List<TimeSlots> timeSlots1 = new ArrayList<>();
            shades[i][0] = "Table " + String.valueOf(gridData.get(i - 1).getTableNum()) + " ("
                    + String.valueOf(gridData.get(i - 1).getTableCapacity()) + ")";
            tableDataMap.put("Table " + String.valueOf(gridData.get(i - 1).getTableNum()) + " ("
                            + String.valueOf(gridData.get(i - 1).getTableCapacity()) + ")",
                    String.valueOf(gridData.get(i - 1).getTableId()));
            timeSlots1 = gridData.get(i - 1).getListTimeslots();
//            for (int y = 1; y < timeSlots1.size(); y++) {
            for (int y = 1; y < timeSlots1.size(); y++) {
                shades[i][y] = timeSlots1.get(y - 1).getAvailable();
            }
        }
        Log.d("<>log-", " shades data ==> " + shades.toString());
        Log.d("<>log-", " table list data ==> " + tables.toString());
      matrixTableAdapter = new MatrixTableAdapterEdited<String>(BookingDetailsForm.this,
                shades, tableInterface,
                times, tables,ReqTableNos,timeFrom,timeTo,tableAndSlots);
        tableFixHeaders.setAdapter(matrixTableAdapter);
    }

    @Override
    public void getListBookedTable(List<String> tables) {
        this.tables = tables;
    }

    @Override
    public void getListBookedTimeSlots(List<String> times) {
        this.times = times;
    }


    private void getGridTimeSlots(final String restaurantId, final String date, final String name,
                                  final String people, final String startTime , final String endTime,
                                  final JsonObject JSONobj) {
     //   showSpinner();

        Log.e("got JSON from card----",""+JSONobj);

        JsonObject subinnerData = new JsonObject();
        subinnerData.add("bookingObj", JSONobj);
        new GetGridforTableAmend().execute(subinnerData);

     }


     String resGetTableGridData;
    ProgressDialog progressDialog;
     class GetGridforTableAmend extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(BookingDetailsForm.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(Object... parametros) {

            try {

                JsonObject getBookingsJson = new JsonObject();
                getBookingsJson.addProperty("status","Awaiting Confirmation");
;

                String responseTableGrid = ResponseAPIWithHeader(BookingDetailsForm.this,
                        Const.SERVER_URL_API +"restaurant-details/getGridDataForAmend"
                        ,CONST_SHAREDPREFERENCES.getString(Const.PREF_USER_TOKEN,"")
                        , parametros[0].toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resGetTableGridData=responseTableGrid;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resGetTableGridData;
        }


        @Override
        protected void onPostExecute(String result) {

            String response_string = "";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            progressDialog.dismiss();

            try{
                Log.e("RES tableGrid Body---", ""+resGetTableGridData);


                JsonArray responseData = new JsonParser().parse(resGetTableGridData).getAsJsonArray();
                List<OTableGrid> tableGrid = new ArrayList<>();
                if (responseData.size() > 0) {
                    for (int i = 0; i < responseData.size(); i++) {
                        Integer tableNum = 0, tableCapacity = 0, roomNo = 0, floorNo = 0;
                        String tableId = "";
                        List<TimeSlots> timeSlots = new ArrayList<>();
                        if (responseData.get(i).getAsJsonObject().has("table") &&
                                !responseData.get(i).getAsJsonObject().get("table").isJsonNull() &&
                                responseData.get(i).getAsJsonObject().get("table").isJsonObject()) {
                            if (responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().has("tableId") &&
                                    !responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableId").isJsonNull()) {
                                tableId = responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableId").getAsString();
                            }
                            if (responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().has("tableNum") &&
                                    !responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableNum").isJsonNull()) {
                                tableNum = responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableNum").getAsInt();
                            }
                            if (responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().has("tableCapacity") &&
                                    !responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableCapacity").isJsonNull()) {
                                tableCapacity = responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("tableCapacity").getAsInt();
                            }
                            if (responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().has("roomNo") &&
                                    !responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("roomNo").isJsonNull()) {
                                roomNo = responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("roomNo").getAsInt();
                            }
                            if (responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().has("floorNo") &&
                                    !responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("floorNo").isJsonNull()) {
                                floorNo = responseData.get(i).getAsJsonObject().get("table").getAsJsonObject().get("floorNo").getAsInt();
                            }
                        }

                        if (responseData.get(i).getAsJsonObject().has("timeslot") &&
                                !responseData.get(i).getAsJsonObject().get("timeslot").isJsonNull() &&
                                responseData.get(i).getAsJsonObject().get("timeslot").isJsonArray()) {
                            JsonArray times = new JsonArray();
                            times = responseData.get(i).getAsJsonObject().get("timeslot").getAsJsonArray();
                            if (times.size() > 0) {
                                for (int j = 0; j < times.size(); j++) {
                                    String timeslot = "", available = "";
                                    if (times.get(j).getAsJsonObject().has("timeslot") &&
                                            !times.get(j).getAsJsonObject().get("timeslot").isJsonNull()) {
                                        timeslot = times.get(j).getAsJsonObject().get("timeslot").getAsString();
                                    }

                                    if (times.get(j).getAsJsonObject().has("available") &&
                                            !times.get(j).getAsJsonObject().get("available").isJsonNull()) {
                                        available = times.get(j).getAsJsonObject().get("available").getAsString();
                                    }

                                    timeSlots.add(new TimeSlots(timeslot, available));
                                }
                            }
                        }
                        tableGrid.add(new OTableGrid(
                                tableNum, tableCapacity, tableId, roomNo, floorNo, timeSlots
                        ));
                    }

                    MyApplication.getGlobalData().addTableGridData(tableGrid);
                    isDataFound = true;
                    if (isDataFound) {

                        tableAndSlots = responseData;

                        init();

                        matrixTableAdapter.notifyDataSetChanged();

                        //mContext.startActivityForResult(i, BOOK_GRID);
                    }else{
                        Toast.makeText(BookingDetailsForm.this, " No tables found !!!", Toast.LENGTH_SHORT).show();
                    }
//                            init();
                }


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                progressDialog.dismiss();
            }

        }
    }


    /*show spinner */
    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(BookingDetailsForm.this, R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    /*dismiss spinner*/
    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }



    static String resUpdateBookingsDetails;
    public class UpdateBookingDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(BookingDetailsForm.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(final Object... parametros) {

            try {

                JsonObject updateBooking = new JsonObject();
                String status = parametros[0].toString();

                updateBooking.addProperty("status",status);

                JsonObject ReqObj = new JsonObject();
                ReqObj.add("updatedObj",updateBooking);

                String responseUpdatBookingConfirm = ResponseAPIWithHeader(getBaseContext(),
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
                         startActivity(new Intent(BookingDetailsForm.this, NavigationMainScreen.class));
                         overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                         finish();
                     }else{
                         Toast.makeText(BookingDetailsForm.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
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

    public class AmendBookingDetails extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");
            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(BookingDetailsForm.this, "Loading", "Please Wait...", true, false);

        }

        @Override
        protected String doInBackground(final Object... parametros) {

            try {

                String responseUpdatBookingAmend = ResponseAPIWithHeader(getBaseContext(),
                        Const.SERVER_URL_API +"bookatables/amendBooking/"+parametros[1].toString(),
                        CONST_SHAREDPREFERENCES.getString(PREF_USER_TOKEN,"")
                        ,parametros[0].toString(),"post");
                // Log.d("URL ====",Const.SERVER_URL_API+"filter_venues?"+upend);
                resUpdateBookingsDetails=responseUpdatBookingAmend;


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
                        startActivity(new Intent(BookingDetailsForm.this, NavigationMainScreen.class));
                        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                        finish();
                    }else{
                        Toast.makeText(BookingDetailsForm.this,rootObj.get("message").getAsString(),Toast.LENGTH_LONG).show();
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
