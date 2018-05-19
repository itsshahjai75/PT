package com.clickeat.customer.click_eatcustomer.MyBookings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OBookings;
import com.clickeat.customer.click_eatcustomer.DataModel.OBookingsforPast;
import com.clickeat.customer.click_eatcustomer.DataModel.OTable;
import com.clickeat.customer.click_eatcustomer.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pivotech on 28/2/18.
 */

public class BookingPastAdapter extends RecyclerView.Adapter<BookingPastAdapter.MyViewHolder> {
    private Context mContext;
    private List<OBookingsforPast> bookings;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView, bookingDate, bookingDateMonth, bookingPeopleNo, bookingTableNo,
                bookingTime, bookingStatus;
        Button btnBookEdit, btnBookCancel;
        public MyViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.card_view);
            mTextView = v.findViewById(R.id.txtBookingName);
            bookingDate = v.findViewById(R.id.bookingDate);
            bookingDateMonth = v.findViewById(R.id.bookingDateMonth);
            bookingPeopleNo = v.findViewById(R.id.bookingPeopleNo);
            bookingTableNo = v.findViewById(R.id.bookingTableNo);
            bookingTime = v.findViewById(R.id.bookingTime);
            bookingStatus = v.findViewById(R.id.bookingStatus);
            btnBookCancel = v.findViewById(R.id.btnBookCancel);
            btnBookEdit = v.findViewById(R.id.btnBookEdit);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookingPastAdapter(Context mContext, List<OBookingsforPast> bookings) {
        this.mContext = mContext;
        this.bookings = bookings;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookingPastAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cardview_item_booking, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Regular.ttf");
        OBookingsforPast datamodel = bookings.get(position);
        holder.mTextView.setText(datamodel.getRestaurantName());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date bookingDate = df.parse(datamodel.getBookingDate().replaceAll("Z$", "+0000"));
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            holder.bookingDate.setText(dd.format(bookingDate));
            holder.bookingDate.setTypeface(roboto_regular);
            SimpleDateFormat mmm = new SimpleDateFormat("MMM");
            holder.bookingDateMonth.setText(mmm.format(bookingDate));
            holder.bookingDateMonth.setAllCaps(true);
            holder.bookingDateMonth.setTypeface(roboto_regular);
            holder.bookingPeopleNo.setText(datamodel.getNumberofPeople() + "");
            holder.bookingPeopleNo.setTypeface(roboto);
            holder.bookingStatus.setText(datamodel.getStatus());
            holder.bookingStatus.setTypeface(roboto);
            if (holder.bookingStatus.getText().toString().equals("Awaiting Confirmation")){
                Log.d("<>log-", " in awaiting confirmation");
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_awaitingconfirm3x, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#ff4823"));
            }else if (holder.bookingStatus.getText().toString().equals("Confirmed")){
                holder.btnBookCancel.setVisibility(View.VISIBLE);
                holder.btnBookEdit.setVisibility(View.VISIBLE);
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_confirmed3x, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#1bcf1b"));
            }else if (holder.bookingStatus.getText().toString().equals("Amend")){
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_amend3x, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#db8509"));
            }else if (holder.bookingStatus.getText().toString().equals("Attended")){
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attended3x, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#00b400"));
            }else if (holder.bookingStatus.getText().toString().equals("Cancelled")){
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cancel_button, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#d80027"));
            }else if (holder.bookingStatus.getText().toString().equals("Waiting List")){
//                holder.bookingStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_waiting_confirmation3x, 0);
                holder.bookingStatus.setTextColor(Color.parseColor("#e0a800"));
            }
            List<OTable> tableData = new ArrayList<>();
            tableData = datamodel.getTableData();
            StringBuilder tableBulider = new StringBuilder();
            if (tableData.size() > 0){
                for (int i=0; i < tableData.size(); i++){
                    if (i + 1 == tableData.size())
                        tableBulider.append(tableData.get(i).getTableNum()+"");
                    else
                        tableBulider.append(tableData.get(i).getTableNum()+"" + " , ");
                }
            }
            holder.bookingTableNo.setText(tableBulider.toString());
            holder.bookingTableNo.setTypeface(roboto);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            Date startTime = df.parse(datamodel.getBookingStartTime().replaceAll("Z$", "+0000"));
            Date endTime = df.parse(datamodel.getBookingEndTime().replaceAll("Z$", "+0000"));
            holder.bookingTime.setText(timeFormat.format(startTime) + " to " + timeFormat.format(endTime));
            holder.bookingTime.setTypeface(roboto);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
}
