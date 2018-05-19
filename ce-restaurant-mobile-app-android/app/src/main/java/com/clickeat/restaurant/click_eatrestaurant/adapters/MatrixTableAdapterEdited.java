package com.clickeat.restaurant.click_eatrestaurant.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.clickeat.restaurant.click_eatrestaurant.BookingDetailsForm.btnAmendBooking;
import static com.clickeat.restaurant.click_eatrestaurant.BookingDetailsForm.btnConfirmBooking;
import static com.clickeat.restaurant.click_eatrestaurant.BookingDetailsForm.people;
import static com.clickeat.restaurant.click_eatrestaurant.BookingDetailsForm.tv_noteforOpsManager;

public class MatrixTableAdapterEdited<T> extends BaseTableAdapter {


    int requestedPerson=0;
    int amendTotalTableCapacity = 0;

    int totalcapacityofAllRequestTables =0;

    private final static int WIDTH_DIP = 80;
    private final static int HEIGHT_DIP = 32;
    private TableInterface tableInterface;
    private final Context context;

    private T[][] table;

    private final int width;
    private final int height;
    private Integer selectedRow = 0;
    private Integer selectedRow_plusnext ;

    private List<String> timeSlots = new ArrayList<>();
    private List<String> tableName = new ArrayList<>();
    public static JsonArray tableAndSlotsJSONAdapter;
    String timeFrom,timeTo,ReqTableNos;

    //public MatrixTableAdapterEdited(Context context) {this(context, null, null, null, null);}

    public MatrixTableAdapterEdited(Context context, T[][] table, TableInterface tableInterface,
                                    List<String> timeSlots, List<String> tableName ,
                                    String ReqTableNos, String timeFrom,String timeTo,
                                    JsonArray tableAndSlotsJSONAdapter) {

        Log.d("<>JarraySize---",""+ tableAndSlotsJSONAdapter.size());
        requestedPerson = Integer.parseInt(people);

        this.context = context;
        this.tableAndSlotsJSONAdapter = tableAndSlotsJSONAdapter;
        this.ReqTableNos = ReqTableNos;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;

        this.tableName = tableName;
        Log.d("<>tbl-", " table namelist ==> " + timeSlots);
        this.timeSlots = timeSlots;
        Resources r = context.getResources();
        if (timeSlots.size() > 0) {
            for (int i = 0; i < table.length -1 ; i++) {
                if (timeSlots.contains(table[0][i +1])){
                    Log.d("<>tble-", " time slot ==> "+table[0][i+1]);
                    selectedRow =i+1;
                    selectedRow_plusnext = selectedRow +1;
                    break;
                }
            }
        }
        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));
        try {
            this.tableInterface = tableInterface;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
            Log.e("MyAdapter", "Must implement the CallbackInterface in the Activity", ex);
        }
        setInformation(table);
    }

    public void setInformation(T[][] table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return table.length - 1;
    }

    @Override
    public int getColumnCount() {
        return table[0].length - 1;
    }

    @Override
    public View getView(final int row, final int column, View convertView, ViewGroup parent) {

        if (getItemViewType(row, column) != 0) {
            if (convertView == null) {
                convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.item_table, parent, false);
            }

            LinearLayout layout = convertView.findViewById(R.id.layoutTable);
            layout.setBackgroundColor(row % 2 == 0 ? context.getResources().getColor(R.color.bg_table_1) :
                    context.getResources().getColor(R.color.bg_table_2));
            final Button btn = ((Button) convertView.findViewById(R.id.btnTable));
            final String text = (table[row ][column].toString());



            for (int a = 0; a< tableAndSlotsJSONAdapter.size(); a++){

                JsonObject tableA = tableAndSlotsJSONAdapter.get(a).getAsJsonObject().get("table").getAsJsonObject();

                String tableNumA = tableA.get("tableNum").getAsString();
                String tableCapacityA = tableA.get("tableCapacity").getAsString();
                String tableIdA = tableA.get("tableId").getAsString();
                String roomNoA = tableA.get("roomNo").getAsString();
                String floorNoA = tableA.get("floorNo").getAsString();

                JsonArray timeSlotA = tableAndSlotsJSONAdapter.get(a).getAsJsonObject().get("timeslot").getAsJsonArray();

                for (int ts=0;ts<timeSlotA.size();ts++){
                    String timeslot = timeSlotA.get(ts).getAsJsonObject().get("timeslot").getAsString();
                    String status  = timeSlotA.get(ts).getAsJsonObject().get("status").getAsString();



                    String firstColoumnTableNo = table[row+1][0].toString();
                    firstColoumnTableNo = firstColoumnTableNo.substring(firstColoumnTableNo.indexOf("Table")+6,firstColoumnTableNo.indexOf("(")-1);

                    //Log.d("1 tbale & got table---",firstColoumnTableNo+"---"+tableNumA/*tableNumA+"******"+timeslot*/);

                    String firstRowTimes = table[0][column+1].toString();
                    //Log.d("1 time & got time---",firstRowTimes+"---"+timeslot/*tableNumA+"******"+timeslot*/);

                    if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("available")){
                        /*Log.d("match  status---",
                                "Table "+firstColoumnTableNo+"---"+tableNumA
                                +"\nTimes "+firstRowTimes+"---"+timeslot);*/

                        if(status.equalsIgnoreCase("available")
                                && timeSlotA.get(ts).getAsJsonObject().has("isTimeSlotAmended")){

                            if(timeSlotA.get(ts).getAsJsonObject().get("isTimeSlotAmended").getAsString().equalsIgnoreCase("true")){
                                btn.setBackground(context.getDrawable(R.drawable.round_corner_yellow_border));
                            }else if(!status.equalsIgnoreCase("amend") &&
                                    timeSlotA.get(ts).getAsJsonObject().get("isTimeSlotAmended")
                                            .getAsString().equalsIgnoreCase("false")){

                                btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_filled));
                                //btn.setText(context.getString(R.string.checkedMark));
                                btn.setClickable(false);
                                btn.setEnabled(false);

                            }else{
                                btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_border));
                            }

                        }else{
                            btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_border));
                        }


                    }else if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("unavailable")){

                        btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_filled));
                        btn.setClickable(false);
                        btn.setEnabled(false);

                    }else if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("requested-available")){
                       /* Log.d("match cases booked---",
                                "Table "+firstColoumnTableNo+"---"+tableNumA
                                        +"\nTimes "+firstRowTimes+"---"+timeslot);*/

                        /*@SuppressLint({"NewApi", "LocalSuppress"})
                        String result = String.join("", tableName);
                        Log.d("tablename req----",""+result);*/

                        if(ReqTableNos.contains(firstColoumnTableNo)
                                && (firstRowTimes.contains(timeFrom.substring(0,3))
                                || firstRowTimes.contains(timeTo.substring(0,3)) ) )
                        {
                            Log.d("match  requested---",
                                    "Table "+firstColoumnTableNo+"---"+tableNumA
                                            +"\nTimes "+firstRowTimes+"---"+timeslot);

                            if(ReqTableNos.contains(firstColoumnTableNo)
                                    && (firstRowTimes.contains(timeFrom.substring(0,3)))){
                                String ReqtableCapacity = table[row+1][0].toString();
                                ReqtableCapacity = ReqtableCapacity.substring(ReqtableCapacity.indexOf("(")+1,ReqtableCapacity.indexOf(")"));

                                totalcapacityofAllRequestTables = totalcapacityofAllRequestTables + Integer.parseInt(ReqtableCapacity);
                                Log.e("<>TotalBookingfor---",""+totalcapacityofAllRequestTables);
                            }

                            btn.setBackground(context.getDrawable(R.drawable.round_corner_green_filled));
                            btn.setText(context.getString(R.string.checkedMark));
                            btn.setClickable(false);
                            btn.setEnabled(false);

                        }
                    }else if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("requested-unavailable")){

                        Log.d("match  booked---",
                                "Table "+firstColoumnTableNo+"---"+tableNumA
                                        +"\nTimes "+firstRowTimes+"---"+timeslot);


                        btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_filled));
                        btn.setText(context.getString(R.string.checkedMark));
                        btn.setClickable(false);
                        btn.setEnabled(false);


                    }else if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("booked")){

                        btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_filled));
                        //btn.setText("B");
                        btn.setClickable(false);
                        btn.setEnabled(false);

                    }else if(firstColoumnTableNo.equalsIgnoreCase(tableNumA)
                            && firstRowTimes.equalsIgnoreCase(timeslot)
                            && status.equalsIgnoreCase("amend")){

                        if(status.equalsIgnoreCase("amend")
                                && timeSlotA.get(ts).getAsJsonObject().has("isTimeSlotAmended")){


                            //Log.w("Json Changed---",tableAndSlotsJSONAdapter.toString());

                            if(status.equalsIgnoreCase("amend") &&
                                    timeSlotA.get(ts).getAsJsonObject().get("isTimeSlotAmended")
                                            .getAsString().equalsIgnoreCase("true")){
                                btn.setBackground(context.getDrawable(R.drawable.round_corner_yellow_filled));
                                btn.setText(context.getResources().getString(R.string.checkedMark));
                            }else{
                                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));
                            }

                           /* if(status.equalsIgnoreCase("amend") &&
                                    timeSlotA.get(ts).getAsJsonObject().get("isTimeSlotAmended").getAsString().equalsIgnoreCase("false")){

                                timeSlotA.get(ts).getAsJsonObject().addProperty("status","true");

                                Log.w("Json Changed---",tableAndSlotsJSONAdapter.toString());
                            }*/

                        }

                        btnConfirmBooking.setVisibility(View.INVISIBLE);
                        btnConfirmBooking.setClickable(false);

                    }

                }
            }

            if(totalcapacityofAllRequestTables > requestedPerson){

                tv_noteforOpsManager.setText("Note : \nBooked table capacity is more than request number of people.");

            }

            btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onClick(View v) {

                    String selectedButtonTableNum = table[row+1][0].toString();
                    selectedButtonTableNum = selectedButtonTableNum.substring(selectedButtonTableNum.indexOf("Table")+6,
                            selectedButtonTableNum.indexOf("(")-1);

                    String selectedButtonTimeSlot = table[0][column+1].toString();

                    for(int j = 0; j< tableAndSlotsJSONAdapter.size(); j++){

                        JsonObject tableA = tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("table").getAsJsonObject();

                        String tableNumA = tableA.get("tableNum").getAsString();

                        if(tableNumA.equalsIgnoreCase(selectedButtonTableNum)){

                            JsonArray timeSlotA = tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray();

                            for (int ts=0;ts<timeSlotA.size();ts++) {
                                String timeslot = timeSlotA.get(ts).getAsJsonObject().get("timeslot").getAsString();



                                if(selectedButtonTimeSlot.equalsIgnoreCase(timeslot)){
                                    /*Log.d("selected timeslot ----",tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                            .get(ts).getAsJsonObject().get("status").getAsString());*/

                                    if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                            .get(ts).getAsJsonObject().get("status").getAsString().equalsIgnoreCase("amend")) {



                                        tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                .get(ts).getAsJsonObject().addProperty("status", "available");

                                        if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                .get(ts).getAsJsonObject().has("isTimeSlotAmended")){

                                            boolean isAmendFound = false;
                                            for (int amend=0;amend<timeSlotA.size();amend++) {
                                                String status = timeSlotA.get(amend).getAsJsonObject().get("status").getAsString();
                                                if(status.equalsIgnoreCase("amend")){
                                                    isAmendFound=true;
                                                    break;
                                                }
                                            }

                                            if(isAmendFound==true) {
                                                tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts).getAsJsonObject().addProperty("isTimeSlotAmended", "true");
                                            }else {
                                                tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts).getAsJsonObject().addProperty("isTimeSlotAmended", "false");
                                            }
                                        }


                                        if ((ts + 1) < tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray().size()
                                                && tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                .get(ts + 1).getAsJsonObject().get("status").getAsString().equalsIgnoreCase("amend"))
                                        {

                                            if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                    .get(ts+1).getAsJsonObject().has("sub_status")){

                                                if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts+1).getAsJsonObject().get("sub_status").getAsString().equalsIgnoreCase("requested-available")){
                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("status", "requested-available");

                                                    if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts+1).getAsJsonObject().has("isTimeSlotAmended")){

                                                        tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                .get(ts+1).getAsJsonObject().addProperty("isTimeSlotAmended", "false");

                                                    }
                                                }
                                            }else {


                                                tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts + 1).getAsJsonObject().addProperty("status", "available");

                                                if (tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts + 1).getAsJsonObject().has("isTimeSlotAmended")) {

                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("isTimeSlotAmended", "false");

                                                }
                                            }

                                        }

                                    }else {



                                        if ((ts + 1) < tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray().size()) {

                                            if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                    .get(ts + 1).getAsJsonObject().get("status").getAsString()
                                                    .equalsIgnoreCase("booked")
                                                    || tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                    .get(ts + 1).getAsJsonObject().get("status").getAsString()
                                                    .equalsIgnoreCase("unavailable")
                                                    || tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                    .get(ts + 1).getAsJsonObject().get("status").getAsString()
                                                    .equalsIgnoreCase("requested-unavailable")){

                                                /*tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts).getAsJsonObject().addProperty("status", "true");*/

                                            }else {



                                                tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts).getAsJsonObject().addProperty("status", "amend");
                                                tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts).getAsJsonObject().addProperty("isTimeSlotAmended", "true");

                                                if(tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                        .get(ts + 1).getAsJsonObject().get("status").getAsString()
                                                        .equalsIgnoreCase("requested-available")){

                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("status", "amend");
                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("isTimeSlotAmended", "true");

                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("sub_status", "requested-available");

                                                }else {

                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("status", "amend");
                                                    tableAndSlotsJSONAdapter.get(j).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                            .get(ts + 1).getAsJsonObject().addProperty("isTimeSlotAmended", "true");
                                                }

                                                //================================================================================

                                                for(int mainJsonArray = 0; mainJsonArray< tableAndSlotsJSONAdapter.size(); mainJsonArray++) {

                                                    JsonArray timeSlotAllArray = tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray();

                                                    for (int tsall = 0; tsall < timeSlotAllArray.size(); tsall++) {

                                                        String str_timeslotall = timeSlotAllArray.get(tsall).getAsJsonObject().get("timeslot").getAsString();
                                                        String availableall = timeSlotAllArray.get(tsall).getAsJsonObject().get("status").getAsString();


                                                        /*
                                                        if(str_timeslotall.
                                                                equalsIgnoreCase(table[0][column+1].toString())
                                                                && status.equalsIgnoreCase("amend")) {

                                                            timeSlotAllArray.get(tsall).getAsJsonObject().addProperty("status","true");
                                                            timeSlotAllArray.get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended","false");

                                                        }

                                                        if(!str_timeslotall.
                                                                equalsIgnoreCase(table[0][column+2].toString())
                                                                && status.equalsIgnoreCase("amend")) {

                                                            timeSlotAllArray.get(tsall).getAsJsonObject().addProperty("status","true");
                                                            timeSlotAllArray.get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended","false");

                                                        }*/

                                                        if(str_timeslotall.
                                                                equalsIgnoreCase(table[0][column+1].toString())){

                                                            tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                    .get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended", "true");

                                                            if (timeSlotAllArray.get(tsall+1).getAsJsonObject().get("status").getAsString()
                                                                    .equalsIgnoreCase("booked")
                                                                    ||timeSlotAllArray.get(tsall+1).getAsJsonObject().get("status").getAsString()
                                                                    .equalsIgnoreCase("unavailable")) {
                                                                tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                        .get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended", "false");
                                                            }

                                                            if(timeSlotAllArray.get(tsall).getAsJsonObject().get("status").getAsString()
                                                                    .equalsIgnoreCase("amend")
                                                                    && !timeSlotAllArray.get(tsall+1).getAsJsonObject().get("status").getAsString()
                                                                    .equalsIgnoreCase("amend")){
                                                                tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                        .get(tsall).getAsJsonObject().addProperty("status", "available");
                                                            }

                                                        }else{

                                                            if(availableall.equalsIgnoreCase("amend")
                                                                    && str_timeslotall.equalsIgnoreCase(table[0][column+2].toString())){

                                                                Log.e("<>clicked next slot" , availableall);


                                                            }else if(availableall.equalsIgnoreCase("booked")){


                                                            }else if(availableall.equalsIgnoreCase("unavailable")){


                                                            }else if(availableall.equalsIgnoreCase("requested-available")){


                                                            }else if(availableall.equalsIgnoreCase("requested-unavailable")){


                                                            }else {

                                                                tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                        .get(tsall).getAsJsonObject().addProperty("status", "available");
                                                                tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                                                        .get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended", "false");

                                                            }

                                                        }


                                                    }

                                                }

                                                //Log.w("button clicked Json Changed---",tableAndSlotsJSONAdapter.toString());

                                                //=====================================================================================


                                            }
                                        }
                                    }

                                    notifyDataSetChanged();
                                }

                            }
                        }


                    }

                    int amendedcellCount=0;
                    for(int updatedJson = 0; updatedJson< tableAndSlotsJSONAdapter.size(); updatedJson++) {

                        JsonObject tableupdatedJson= tableAndSlotsJSONAdapter.get(updatedJson).getAsJsonObject().get("table").getAsJsonObject();

                        JsonArray timeSlotupdatedJson = tableAndSlotsJSONAdapter.get(updatedJson).getAsJsonObject().get("timeslot").getAsJsonArray();

                        for (int tsupdatedJson = 0; tsupdatedJson < timeSlotupdatedJson.size(); tsupdatedJson++) {
                            //String timeslot = timeSlotA.get(ts).getAsJsonObject().get("timeslot").getAsString();
                            String status = timeSlotupdatedJson.get(tsupdatedJson).getAsJsonObject()
                                    .get("status").getAsString();
                            String isTimeSlotAmended = timeSlotupdatedJson.get(tsupdatedJson).getAsJsonObject().has("isTimeSlotAmended")
                                    ?timeSlotupdatedJson.get(tsupdatedJson).getAsJsonObject()
                                    .get("isTimeSlotAmended").getAsString()
                                    :"";

                            if(status.equalsIgnoreCase("amend")
                                    && isTimeSlotAmended.equalsIgnoreCase("true")){
                                amendedcellCount=amendedcellCount+1;
                                Log.e("<totalAmendedcellCount>",""+amendedcellCount+"---"+status);
                            }

                        }
                    }

                    if(amendedcellCount==0){
                        Log.e("<totalAmendedcellCount000>",""+amendedcellCount+"---");

                        btnConfirmBooking.setVisibility(View.VISIBLE);
                        btnConfirmBooking.setClickable(true);

                        btnAmendBooking.setVisibility(View.INVISIBLE);
                        btnAmendBooking.setClickable(false);

                        for(int mainJsonArray = 0; mainJsonArray< tableAndSlotsJSONAdapter.size(); mainJsonArray++) {

                            JsonArray timeSlotAllArray = tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray();

                            for (int tsall = 0; tsall < timeSlotAllArray.size(); tsall++) {

                                String str_timeslotall = timeSlotAllArray.get(tsall).getAsJsonObject().get("timeslot").getAsString();

                                tableAndSlotsJSONAdapter.get(mainJsonArray).getAsJsonObject().get("timeslot").getAsJsonArray()
                                        .get(tsall).getAsJsonObject().addProperty("isTimeSlotAmended", "0");

                            }

                            notifyDataSetChanged();
                        }


                    }else{
                        btnConfirmBooking.setVisibility(View.INVISIBLE);
                        btnConfirmBooking.setClickable(false);

                        btnAmendBooking.setVisibility(View.VISIBLE);
                        btnAmendBooking.setClickable(true);
                    }

                }
            });



            return convertView;

        } else {
            if (convertView == null) {
                convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.item_table_header, parent, false);
            }
            TextView text = ((TextView) convertView.findViewById(android.R.id.text1));
            text.setText(table[row + 1][column + 1].toString());
            return convertView;
        }
    }

    @Override
    public int getHeight(int row) {
        return height;
    }

    @Override
    public int getWidth(int column) {
        return width;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row < 0) {
            return 0;
        } else if (column < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public interface TableInterface {

        void getListBookedTable(List<String> tables);

        void getListBookedTimeSlots(List<String> times);
    }
}
