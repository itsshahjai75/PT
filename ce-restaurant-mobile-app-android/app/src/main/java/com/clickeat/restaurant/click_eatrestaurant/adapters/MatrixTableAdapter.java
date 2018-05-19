package com.clickeat.restaurant.click_eatrestaurant.adapters;

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
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatrixTableAdapter<T> extends BaseTableAdapter {

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

   // public MatrixTableAdapter(Context context) {this(context, null, null, null, null);}

    public MatrixTableAdapter(Context context, T[][] table, TableInterface tableInterface,
                              List<String> timeSlots, List<String> tableName) {
        this.context = context;
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

//            final String text = (table[row + 1][column + 1].toString());
            final String text = (table[row ][column].toString());
            /*if (tableName.contains(table[row + 1][0].toString()) &&
                    timeSlots.contains(table[0][column + 1].toString())) {
                Log.d("<>tbl-", "in if slots --> " + table[row + 1][0].toString());
                selectedRow = column + 1;
                btn.setBackground(context.getDrawable(R.drawable.button_booked));
                notifyDataSetChanged();
            }*/

            /*if (text.toString().equals("booked")) {
                btn.setBackground(context.getDrawable(R.drawable.round_corner_green_filled));
            } else {
                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));
            }

           // btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));

            if(text.toString().equals("booked"))
            {
                btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_filled));

            } else if (!text.toString().equals("booked") && text.toString().equalsIgnoreCase("true")) {
                if ((selectedRow == column + 1) &&
                        tableName.contains(table[row + 1][0].toString()))
                {

                    btn.setBackground(context.getDrawable(R.drawable.button_booked));
                } else if (selectedRow == column + 1)
                {
                    btn.setBackground(context.getDrawable(R.drawable.book_btn_style));
                }
                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));
            }else if (!text.toString().equals("booked") && text.toString().equalsIgnoreCase("false")){
                btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_border));
            }*/


            if (text.toString().equals("booked")) {
                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_filled));
            }else if(text.toString().equals("true")){
                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));
            } else {
                btn.setBackground(context.getDrawable(R.drawable.round_corner_grey_border));
            }

            if (!text.toString().equals("booked")) {
                if ((selectedRow == column + 1) &&
                        tableName.contains(table[row + 1][0].toString())) {
                    btn.setBackground(context.getDrawable(R.drawable.round_corner_green_filled));
                }else if (selectedRow_plusnext == column +1
                        && tableName.contains(table[row + 1][0].toString())){
                    btn.setBackground(context.getDrawable(R.drawable.round_corner_green_filled));
                }else if (selectedRow == column + 1) {
                    btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_border));
                }
            }

            if (text.toString().equals("booked") && selectedRow_plusnext == column +1
                    && tableName.contains(table[row + 1][0].toString())){
                btn.setBackground(context.getDrawable(R.drawable.round_corner_primary_filled));
            }


            /*btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("<>getBody-", " table row 0 --> " + table[row + 1][0].toString() +
                            table[0][column + 1].toString());
                    if (!text.toString().equals("booked")) {
                        if ((selectedRow == column + 1 || selectedRow == 0) &&
                                !tableName.contains(table[row + 1][0].toString())) {
                            selectedRow = column + 1;
                            btn.setBackground(context.getDrawable(R.drawable.button_booked));
                            if (!timeSlots.contains(table[0][column + 1].toString()))
                                timeSlots.add(table[0][column + 1].toString());
                            if (!tableName.contains(table[row + 1][0].toString()))
                                tableName.add(table[row + 1][0].toString());
                            notifyDataSetChanged();
                        } else if (selectedRow == column + 1 && timeSlots.contains(table[0][column + 1].toString())) {
                            btn.setBackground(context.getDrawable(R.drawable.book_btn_style));
                            if (tableName.contains(table[row + 1][0].toString()))
                                tableName.remove(table[row + 1][0].toString());
                            if (!timeSlots.contains(table[0][column + 1].toString()))
                                timeSlots.remove(table[0][column + 1].toString());
                            if (tableName.size() == 0) {
                                Log.d("<>tbl-", " table name is emty" + tableName.size() + "");
                                timeSlots = new ArrayList<>();
                                selectedRow = 0;
                            }
                            notifyDataSetChanged();
                        } else {
                            Log.d("<>getBody-", "in else  table row 0 --> " + table[row + 1][0].toString() +
                                    table[0][column + 1].toString());
                            selectedRow = column + 1;
                            btn.setBackground(context.getDrawable(R.drawable.button_booked));
                            tableName = new ArrayList<>();
                            timeSlots = new ArrayList<>();
                            if (!timeSlots.contains(table[0][column + 1].toString()))
                                timeSlots.add(table[0][column + 1].toString());
                            if (!tableName.contains(table[row + 1][0].toString()))
                                tableName.add(table[row + 1][0].toString());
                            notifyDataSetChanged();
                        }
                    }

                    if (tableInterface != null) {
                        tableInterface.getListBookedTable(tableName);
                        tableInterface.getListBookedTimeSlots(timeSlots);
                    }
                }
            });*/

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
