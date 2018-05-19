package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.OBookTimeSlot;
import com.clickeat.customer.click_eatcustomer.DataModel.OTable;
import com.clickeat.customer.click_eatcustomer.DataModel.OTableGrid;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.DataModel.TimeSlots;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BookATable extends Activity implements RecyclerBookTimeSlotAdapter.UpdateDataClickListener,
        NumberPicker.OnValueChangeListener {
    private static Integer BOOK_GRID = 001;
    private Intent intent;
    private EditText edtName, edtEmail, edtMobileNo, edtPeople, edtDate;
    private String restaurantId, name;
    private ProgressDialog loadingSpinner;
    private Spinner spinnerTableNumber;
    private Button btnCancel, btnBook;
    private TextView textHeader, txtLink, txtBookTime, txtBookTable;
    private ImageButton txtSelectionTable;
    private RecyclerView recyclerBookTimeSlot;
    private RecyclerBookTimeSlotAdapter timeSlotAdapter;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar myCalendar = Calendar.getInstance();
    private HashMap<Integer, String> spinnerMap;
    private String timeSlot = "00";
    private EditText edtAdditionalNote;
    private Boolean isDataFound = false;
    private LinearLayout infoLayout;
    private List<String> tableList;
    private String selectedTime;
    List<String> tableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_book_atable);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        findIds();
        spinnerMap = new HashMap<Integer, String>();
        intent = getIntent();
        init();
        getTableNumbers();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        edtPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetails();
            }
        });

    }

    private void findIds() {
        edtName = findViewById(R.id.input_name);
        edtEmail = findViewById(R.id.input_email);
        edtDate = findViewById(R.id.input_date);
        edtMobileNo = findViewById(R.id.input_mobile);
        edtPeople = findViewById(R.id.input_person);
        spinnerTableNumber = findViewById(R.id.spinnerTableNumber);
        btnCancel = findViewById(R.id.btnBookCancel);
        textHeader = findViewById(R.id.textHeader);
        recyclerBookTimeSlot = findViewById(R.id.recyclerBookTimeSlot);
        btnBook = findViewById(R.id.btnBook);
        txtLink = findViewById(R.id.txtLink);
        edtAdditionalNote = findViewById(R.id.txtAdditionalNote);
        txtBookTime = findViewById(R.id.txtBookTime);
        txtBookTable = findViewById(R.id.txtBookTable);
        infoLayout = findViewById(R.id.infoLayout);
        txtSelectionTable = findViewById(R.id.txtSelectionTable);
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        edtDate.setText(sdf.format(myCalendar.getTime()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        if (spinnerTableNumber.getCount() > 0) {
        try {
//                if (spinnerTableNumber.getSelectedItemPosition() != 0) {
            String name = spinnerTableNumber.getSelectedItem().toString();
            String tableId = spinnerMap.get(Integer.parseInt(spinnerTableNumber.getSelectedItem().toString()));
            Date date = format.parse(edtDate.getText().toString());
            Log.d("<>date-", "tableId ==> " + tableId + " date of string ---> " + date.toString() + "name ==> " + name);
            String dateTime = dateFormat.format(date);
            Log.d("<>date-", " dateTime of string ---> " + dateTime.toString());
//                    getTimeSlots(tableId, dateTime);
            getGridTimeSlots(restaurantId, dateTime);
               /* } else {
                    Toast.makeText(BookATable.this, "Select Table No", Toast.LENGTH_SHORT).show();
                }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        }
    }

    private void init() {
        if (intent != null) {
            name = intent.getStringExtra("name");
            textHeader.setText(name);
            restaurantId = intent.getStringExtra("restaurantId");
            edtPeople.setText(intent.getStringExtra("people"));
            edtDate.setText(intent.getStringExtra("date"));
            List<OUser> user = MyApplication.getGlobalData().getUserData();
            if (user.size() > 0) {
                for (OUser user1 : user) {
                    edtName.setText(user1.getName());
                    edtName.setSelection(edtName.getText().length());
                    edtMobileNo.setText(user1.getMobile());
                    edtEmail.setText(user1.getEmail());
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date date = null;
            try {
                date = format.parse(edtDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateTime = dateFormat.format(date);
            Log.d("<>date-", " dateTime of string ---> " + dateTime.toString());
//                    getTimeSlots(tableId, dateTime);
            getGridTimeSlots(restaurantId, dateTime);

        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(BookATable.this, SyncGoogleBooking.class);
                startActivity(intent);*/
                finish();
            }
        });

        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataFound) {
                    tableList = new ArrayList<>();
                    Intent i = new Intent(BookATable.this, BookingDetailsForm.class);
                    i.putExtra("restaurantId", restaurantId);
                    i.putExtra("restaurantName", name);
                    i.putExtra("bookDate", edtDate.getText().toString());
                    i.putExtra("people", edtPeople.getText().toString());
                    i.putExtra("timeSlot", selectedTime);
                    i.putStringArrayListExtra("tableList", (ArrayList<String>) tableList);
                    startActivityForResult(i, BOOK_GRID);
                } else {
                    Toast.makeText(BookATable.this, " No tables found !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtSelectionTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BookATable.this, BookingDetailsForm.class);
                i.putExtra("restaurantId", restaurantId);
                i.putExtra("restaurantName", name);
                i.putExtra("bookDate", edtDate.getText().toString());
                i.putExtra("people", edtPeople.getText().toString());
                i.putExtra("timeSlot", selectedTime);
                Log.d("<>tbl-", " table data book in btn a tbl ==> " + selectedTime);
                i.putStringArrayListExtra("tableList", (ArrayList<String>) tableList);
                startActivityForResult(i, BOOK_GRID);
            }
        });
    }

    private void setSpinnerData() {
        ArrayList<OTable> tableData = new ArrayList<>();
        tableData = MyApplication.getGlobalData().getoTableList();
        if (tableData.size() > 0) {
            Integer count = tableData.size() + 1;
            String[] spinnerArray = new String[count];
            List<String> spinnerList = new ArrayList<>();
            spinnerList.add("Select Table");
            spinnerArray[0] = "Select Table";
            for (int i = 0; i < tableData.size(); i++) {
                spinnerMap.put(tableData.get(i).getTableNum(), tableData.get(i).getTableId());
//                spinnerArray[i] = String.valueOf(tableData.get(i).getTableNum()+"");
                spinnerList.add(String.valueOf(tableData.get(i).getTableNum()));
//                Log.d("<>tag-", " spinnerArray ==> " + spinnerArray[i]+"");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookATable.this, R.layout.layout_spinner_book_item,
                    spinnerList);
            adapter.setDropDownViewResource(R.layout.layout_spinner_drop_down_item);
            spinnerTableNumber.setAdapter(adapter);

            spinnerTableNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!(position == 0)) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        try {
                            String name = spinnerTableNumber.getSelectedItem().toString();
                            String tableId = spinnerMap.get(Integer.parseInt(spinnerTableNumber.getSelectedItem().toString()));
                            Date date = format.parse(edtDate.getText().toString());
                            Log.d("<>date-", "tableId ==> " + tableId + " date of string ---> " + date.toString() + "name ==> " + name);
                            String dateTime = dateFormat.format(date);
                            getGridTimeSlots(restaurantId, dateTime);
                            Log.d("<>date-", " dateTime of string ---> " + dateTime.toString());
//                            getTimeSlots(tableId, dateTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /* this will show date picker dialog */
    private void showDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookATable.this,
                R.style.DialogTheme, dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis() - 1000);
        datePickerDialog.show();
    }

    /* Show Number picker dialog click on people edit text*/
    private void showNumberPicker() {
        final Dialog d = new Dialog(BookATable.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.layout_number_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        d.getWindow().setAttributes(lp);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(20);
        np.setMinValue(1);
        if (edtPeople.getText().length() > 0)
            np.setValue(Integer.parseInt(edtPeople.getText().toString()));
        else
            np.setValue(2);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPeople.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    private void setTimeSlots() {
        List<OBookTimeSlot> timeSlots = MyApplication.getGlobalData().getTimeSlotsforBook();
        timeSlotAdapter = new RecyclerBookTimeSlotAdapter(BookATable.this, timeSlots, 1);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(BookATable.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerBookTimeSlot.setLayoutManager(horizontalLayoutManager);
        recyclerBookTimeSlot.setAdapter(timeSlotAdapter);
        timeSlotAdapter.setOnItemClickListener(this);
    }

    private void getTableNumbers() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final JsonObject innerData = new JsonObject();
        innerData.addProperty("restaurant_id", restaurantId);
        Integer noPeople = Integer.valueOf(edtPeople.getText().toString());
        innerData.addProperty("numberOfPeople", noPeople);
        Call<JsonElement> mService = mInterfaceService.getTableNumberByRestaurantId(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of table number ");
                    ArrayList<OTable> tableDetails = new ArrayList<>();
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        JsonArray arrayList = response.body().getAsJsonArray();
                        if (arrayList.size() > 0) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                Integer tableNum = 0, tableCapacity = 0, roomNo = 0, floorNo = 0;
                                String tableId = "";
                                if (!arrayList.get(i).getAsJsonObject().isJsonNull()) {
                                    if (arrayList.get(i).getAsJsonObject().has("tableNum")
                                            && !arrayList.get(i).getAsJsonObject().get("tableNum").isJsonNull()) {
                                        tableNum = arrayList.get(i).getAsJsonObject().get("tableNum").getAsInt();
                                    }
                                    if (arrayList.get(i).getAsJsonObject().has("tableCapacity")
                                            && !arrayList.get(i).getAsJsonObject().get("tableCapacity").isJsonNull()) {
                                        tableCapacity = arrayList.get(i).getAsJsonObject().get("tableCapacity").getAsInt();
                                    }
                                    if (arrayList.get(i).getAsJsonObject().has("tableId")
                                            && !arrayList.get(i).getAsJsonObject().get("tableId").isJsonNull()) {
                                        tableId = arrayList.get(i).getAsJsonObject().get("tableId").getAsString();
                                    }
                                    if (arrayList.get(i).getAsJsonObject().has("roomNo")
                                            && !arrayList.get(i).getAsJsonObject().get("roomNo").isJsonNull()) {
                                        roomNo = arrayList.get(i).getAsJsonObject().get("roomNo").getAsInt();
                                    }
                                    if (arrayList.get(i).getAsJsonObject().has("floorNo")
                                            && !arrayList.get(i).getAsJsonObject().get("floorNo").isJsonNull()) {
                                        floorNo = arrayList.get(i).getAsJsonObject().get("floorNo").getAsInt();
                                    }

                                    tableDetails.add(new OTable(
                                            tableNum,
                                            tableCapacity,
                                            tableId,
                                            roomNo,
                                            floorNo));
                                }
                            }
                            MyApplication.getGlobalData().addTableDetails(tableDetails);
                            setSpinnerData();
                        }
                        if (loadingSpinner != null)
                            dismissSpinner();
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                init();
                Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getTimeSlots(String tableId, String date) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final JsonObject innerData = new JsonObject();
        innerData.addProperty("date", date);
        innerData.addProperty("tableId", tableId);
        innerData.addProperty("restaurantId", restaurantId);
        Log.d("<>log-", " time slot parameters ==> " + innerData.toString());
        Call<JsonElement> mService = mInterfaceService.getTimeSlotsByRestaurantId(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of table number ");
                    ArrayList<OTable> tableDetails = new ArrayList<>();
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (loadingSpinner != null)
                            dismissSpinner();
                        if (response.body().getAsJsonObject().has("timeslots") &&
                                !response.body().getAsJsonObject().get("timeslots").isJsonNull()) {
                            List<OBookTimeSlot> bookTimeSlots = new ArrayList<>();
                            JsonArray timeSlotArray = response.body().getAsJsonObject().get("timeslots").getAsJsonArray();
                            if (timeSlotArray.size() > 0) {
                                for (int i = 0; i < timeSlotArray.size(); i++) {
                                    bookTimeSlots.add(new OBookTimeSlot(
                                            timeSlotArray.get(i).getAsJsonObject().get("timeslot").getAsString(),
                                            timeSlotArray.get(i).getAsJsonObject().get("available").getAsString()
                                    ));
                                }
                                MyApplication.getGlobalData().addTimeSlotsforBook(bookTimeSlots);
                                setTimeSlots();
                            } else {
                                Toast.makeText(BookATable.this, "No time slots found!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                } else if (response.code() == 500) {
                    Log.d("<>log-", " Internal Server Error");
                    Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*show spinner */
    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(BookATable.this, R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    /*dismiss spinner*/
    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }


    @Override
    public void onItemClick(int position) {
        Log.d("<>item-", " click position ==> " + position + "");
        Log.d("<>item-", " click position item text ==> " + timeSlotAdapter.getItem(position).getTime() + "");
        if (timeSlotAdapter.getItem(position).getStatus().equals("true")) {
            timeSlotAdapter.selected(position);
            timeSlot = timeSlotAdapter.getItem(position).getTime();
        } else {
            timeSlot = "00";
        }
    }

    private void checkDetails() {
        final JsonObject innerData = new JsonObject();
        JsonArray tableDataList = new JsonArray();
        if (tableList.size() > 0) {
            List<OTable> tables = MyApplication.getGlobalData().getoTableList();
            if (tables.size() > 0) {
                for (int tab = 0; tab < tables.size(); tab++) {
                    if (tableIds.contains(tables.get(tab).getTableId())) {
                        JsonObject tableData = new JsonObject();
                        tableData.addProperty("floorNo", tables.get(tab).getFloorNo());
                        tableData.addProperty("roomNo", tables.get(tab).getRoomNo());
                        tableData.addProperty("tableId", tables.get(tab).getTableId());
                        tableData.addProperty("tableCapacity", tables.get(tab).getTableCapacity());
                        tableData.addProperty("tableNum", tables.get(tab).getTableNum());
                        tableDataList.add(tableData);
                    }
                }
            }
        } else
            return;
        Log.d("<>tag-", " spinner data ==> " + tableDataList.toString());
        if (edtDate.getText().length() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                String name = spinnerTableNumber.getSelectedItem().toString();
//                String tableId = spinnerMap.get(Integer.parseInt(spinnerTableNumber.getSelectedItem().toString()));
                Date date = format.parse(edtDate.getText().toString());
                String dateTime = dateFormat.format(date);
                innerData.addProperty("bookingDate", dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (edtAdditionalNote.getText().length() > 100) {
            Toast.makeText(BookATable.this, " write note in 100 words.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            innerData.addProperty("additionalNotes", edtAdditionalNote.getText().toString());
        }
        innerData.addProperty("numOfPeople", Integer.parseInt(edtPeople.getText().toString()));
        innerData.addProperty("customerName", edtName.getText().toString());
        innerData.addProperty("customerEmail", edtEmail.getText().toString());
        innerData.addProperty("customerPhone", edtMobileNo.getText().toString());
        if (!selectedTime.equals(null))
            innerData.addProperty("bookingStartTime", selectedTime);
        else {
            Toast.makeText(BookATable.this, "Select Time ", Toast.LENGTH_SHORT).show();
            return;
        }
        innerData.add("table_data", tableDataList);
        innerData.addProperty("restaurant_id", restaurantId);
        innerData.addProperty("restaurantName", name);
        String userId = "";
        userId = SharedData.getUserId(BookATable.this);
        innerData.addProperty("userid", userId);
        sendBookingTableDetails();
    }

    private void sendBookingTableDetails() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final JsonObject innerData = new JsonObject();
        JsonArray tableDataList = new JsonArray();
        if (tableList.size() > 0) {
            List<OTable> tables = MyApplication.getGlobalData().getoTableList();

            if (tables.size() > 0) {
                for (int tab = 0; tab < tables.size(); tab++) {
                    Log.d("<>book-", " table list ==> " + tables.get(tab).toString());
                    Log.d("<>book-", " table ids ==> " + tableIds.toString());
                    if (tableIds.contains(tables.get(tab).getTableId())) {
                        Log.d("<>book-", " in if condition ");
                        JsonObject tableData = new JsonObject();
                        tableData.addProperty("floorNo", tables.get(tab).getFloorNo());
                        tableData.addProperty("roomNo", tables.get(tab).getRoomNo());
                        tableData.addProperty("tableId", tables.get(tab).getTableId());
                        tableData.addProperty("tableCapacity", tables.get(tab).getTableCapacity());
                        tableData.addProperty("tableNum", tables.get(tab).getTableNum());
                        tableDataList.add(tableData);
                    }
                }
            }
        }

//        if (spinnerMap.size() > 0) {
//            if (spinnerTableNumber.getSelectedItemPosition() != 0) {
//                List<OTable> tables = MyApplication.getGlobalData().getoTableList();
//                String tableId = spinnerMap.get(Integer.parseInt(spinnerTableNumber.getSelectedItem().toString()));
//                if (tables.size() > 0) {
//                    for (int tab = 0; tab < tables.size(); tab++) {
//                        if (tableId.equals(tables.get(tab).getTableId())) {
//                            tableData.addProperty("floorNo", tables.get(tab).getFloorNo());
//                            tableData.addProperty("roomNo", tables.get(tab).getRoomNo());
//                            tableData.addProperty("tableId", tables.get(tab).getTableId());
//                            tableData.addProperty("tableCapacity", tables.get(tab).getTableCapacity());
//                            tableData.addProperty("tableNum", tables.get(tab).getTableNum());
//                        }
//                    }
//                }
//            }
//        }
        if (edtDate.getText().length() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                String name = spinnerTableNumber.getSelectedItem().toString();
//                String tableId = spinnerMap.get(Integer.parseInt(spinnerTableNumber.getSelectedItem().toString()));
                Date date = format.parse(edtDate.getText().toString());
//                Log.d("<>date-", "tableId ==> " + tableId + " date of string ---> " + date.toString() + "name ==> " + name);
                String dateTime = dateFormat.format(date);
                innerData.addProperty("bookingDate", dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        innerData.addProperty("additionalNotes", edtAdditionalNote.getText().toString());
        innerData.addProperty("numOfPeople", Integer.parseInt(edtPeople.getText().toString()));
        innerData.addProperty("customerName", edtName.getText().toString());
        innerData.addProperty("customerEmail", edtEmail.getText().toString());
        innerData.addProperty("customerPhone", edtMobileNo.getText().toString());
        if (!selectedTime.equals(null))
            innerData.addProperty("bookingStartTime", selectedTime);

        innerData.add("table_data", tableDataList);
        innerData.addProperty("restaurant_id", restaurantId);
        innerData.addProperty("restaurantName", name);
        String userId = "";
        userId = SharedData.getUserId(BookATable.this);
        innerData.addProperty("userid", userId);

        Log.d("<>log-", " book details slot parameters ==> " + innerData.toString());
        Call<JsonElement> mService = mInterfaceService.sendBookATableData(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body());

        mService.enqueue(new Callback<JsonElement>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of table number ");
                    ArrayList<OTable> tableDetails = new ArrayList<>();
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (loadingSpinner != null)
                            dismissSpinner();

                        String nameCustomer = edtName.getText().toString();

                        /*  *//**//*Toast toast = Toast.makeText(BookATable.this,nameCustomer.substring(0, 1).toUpperCase() + nameCustomer.substring(1)+" your table booking with " +
                                name +" for "+ edtPeople.getText().toString()+" on "+ edtDate.getText().toString()+" at "+timeSlot+" has been " +
                                "received. \n You will receive update on your request shortly. ", Toast.LENGTH_LONG);
                        View toastView = toast.getView(); // This'll return the default View of the Toast.

                    /*//**//**//* And now you can get the TextView of the default View of the Toast. *//**//**//**//*
                        final Typeface roboto = Typeface.createFromAsset(getAssets(),
                                "fonts/Roboto-Light.ttf");
                        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                        toastMessage.setTextSize(14);
                        toastMessage.setTypeface(roboto);
                        toastMessage.setTextColor(getResources().getColor(R.color.colorWhite));
                        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
                        toastMessage.setGravity(Gravity.CENTER);
                        toastMessage.setCompoundDrawablePadding(16);
                        toastView.setBackground(getResources().getDrawable(R.drawable.collection_delivery_popup_style));
                        toast.show();
                        finish();*//**//**/
                        AlertDialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                BookATable.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        builder.setTitle("Success");
                        builder.setCancelable(false);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setMessage(nameCustomer.substring(0, 1).toUpperCase() + nameCustomer.substring(1) + " your table booking with " +
                                name + " for " + edtPeople.getText().toString() + " on " + edtDate.getText().toString() + " at " + selectedTime + " has been " +
                                "received. \n You will receive update on your request shortly. ");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        dialog = builder.create();
                        //*Bitmap map=takeScreenShot(BookATable.this);
                       /* Bitmap fast=fastblur(map, 10);
                        final Drawable draw=new BitmapDrawable(getResources(),fast);
                        dialog.getWindow().setBackgroundDrawable(draw);*//**//**/
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();
                        builder.show();

                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                } else if (response.code() == 500) {
                    Log.d("<>log-", " Internal Server Error");
                    Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getGridTimeSlots(String restaurantId, String date) {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        final JsonObject innerData = new JsonObject();
        innerData.addProperty("date", date);
        innerData.addProperty("pastTimeslotToday", 0);
        innerData.addProperty("restaurant_id", restaurantId);
        Call<JsonElement> mService = mInterfaceService.getBookingCalendar(innerData);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Request Body is  ==> " + mService.request().body());
        Log.d("<>login-", " Request params grid  is  ==> " + innerData.toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>log-", " in response of table number ");
                    if (response.code() == 200) {
                        Log.d("<>log-", " response grid body ===> " + response.body() + "");
                        if (loadingSpinner != null)
                            dismissSpinner();

                        JsonArray responseData = response.body().getAsJsonArray();
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
//                            init();
                        }

                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                    isDataFound = false;
                } else if (response.code() == 500) {
                    Log.d("<>log-", " Internal Server Error");
                    Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
                    if (loadingSpinner != null)
                        dismissSpinner();
                    isDataFound = false;
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                isDataFound = false;
                Toast.makeText(BookATable.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BOOK_GRID) {
            if (resultCode == Activity.RESULT_OK) {
                tableList = new ArrayList<>();
                String capacity = data.getStringExtra("capacity");
                tableIds = new ArrayList<>();
                tableIds = data.getStringArrayListExtra("tableIds");
                selectedTime = data.getStringExtra("timeSlot");
                tableList = data.getStringArrayListExtra("tableList");
                Log.d("<>tbl-", " table data book a tbl ==> " + tableList);
                StringBuilder tabBulider = new StringBuilder();
                List<OTableGrid> gridData = new ArrayList<>();
                gridData = MyApplication.getGlobalData().getAllTableGridData();
                for (int t = 0; t < tableIds.size(); t++) {
                    for (int tab = 0; tab < gridData.size(); tab++) {
                        if (tableIds.get(t).toString().equals(gridData.get(tab).getTableId())) {

                            if (t + 1 == tableIds.size())
                                tabBulider.append(gridData.get(tab).getTableNum());
                            else
                                tabBulider.append(gridData.get(tab).getTableNum() + " , ");
                        }
                    }
                }
                txtBookTable.setText(tabBulider.toString());
                txtBookTime.setText(selectedTime);
                infoLayout.setVisibility(View.VISIBLE);
                txtSelectionTable.setVisibility(View.VISIBLE);
                txtLink.setVisibility(View.GONE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
