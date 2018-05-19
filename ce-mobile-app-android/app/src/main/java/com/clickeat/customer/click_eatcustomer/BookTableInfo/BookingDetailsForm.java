package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.DataModel.OTableGrid;
import com.clickeat.customer.click_eatcustomer.DataModel.TimeSlots;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.inqbarna.tablefixheaders.TableFixHeaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDetailsForm extends Activity implements MatrixTableAdapter.TableInterface {
    private ProgressDialog loadingSpinner;
    private String restaurantId, restaurantName, bookingDate, people;
    TableFixHeaders tableFixHeaders;
    private Intent extras;
    private EditText input_person, input_date;
    private TextView textHeader;
    private ImageButton btnBookClose;
    private List<String> times = new ArrayList<>();
    private List<String> tables = new ArrayList<>();
    private Button btnBookDone;
    MatrixTableAdapter.TableInterface tableInterface;
    private HashMap<String, String> tableDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_booking_details_form);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        input_person = findViewById(R.id.input_person);
        input_date = findViewById(R.id.input_date);
        textHeader = findViewById(R.id.textHeader);
        btnBookClose = findViewById(R.id.btnBookClose);
        btnBookDone = findViewById(R.id.btnBookDone);
        try {
            tableInterface = (MatrixTableAdapter.TableInterface) this;
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
            restaurantId = extras.getStringExtra("restaurantId");
            bookingDate = extras.getStringExtra("bookDate");
            restaurantName = extras.getStringExtra("restaurantName");
            people = extras.getStringExtra("people");
            tables = extras.getStringArrayListExtra("tableList");
            String timeExtra = extras.getStringExtra("timeSlot");
            if (timeExtra != null) {
                times = new ArrayList<>();
                times.add(timeExtra);
            }
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
            init();
        }

        btnBookDone.setOnClickListener(new View.OnClickListener() {
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

                                   /* if (t + 1 == selectedList.size())
                                        tabBulider.append(gridData.get(tab).getTableNum());
                                    else
                                        tabBulider.append(gridData.get(tab).getTableNum() + " , ");*/
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
                    Toast.makeText(BookingDetailsForm.this, R.string.unsuffiecent_table, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            for (int y = 1; y <= timeSlots1.size(); y++) {
                shades[i][y] = timeSlots1.get(y - 1).getAvailable();
            }
        }
        Log.d("<>log-", " shades data ==> " + shades.toString());
        Log.d("<>log-", " table list data ==> " + tables.toString());
        MatrixTableAdapter<String> matrixTableAdapter = new MatrixTableAdapter<String>(this, shades, tableInterface,
                times, tables);
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
}
