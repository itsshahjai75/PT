package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.TimeSlotDetails;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;

import java.util.List;

public class TimeSlotRecyclerAdapter extends
        RecyclerView.Adapter<TimeSlotRecyclerAdapter.SingleItemRowHolder> {

    private Context mContext;
    private RecyclerView recyclerView;
    private String bookDate;
    List<TimeSlotDetails> timeTableList;
    private String restaurantId;
    private String restaurantName;
    private String people;
    private timeSlotInterface timeSlotInterface;


    public TimeSlotRecyclerAdapter(List<TimeSlotDetails> timeTableList, Context mContext,
                                   RecyclerView recyclerView, String bookDate, timeSlotInterface timeSlotInterface) {
        this.timeTableList = timeTableList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.bookDate = bookDate;
        this.timeSlotInterface = timeSlotInterface;
    }

    @Override
    public int getItemCount() {
        return (null != timeTableList ? timeTableList.size() : 0);
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_menu_listview_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/fontawesome-webfont.ttf");
        TimeSlotDetails items = timeTableList.get(position);
        holder.txtName.setText(items.getName());
        holder.txtName.setTypeface(font);
        people = items.getNoPeople();
        restaurantId = items.getRestaurantId();
        restaurantName = items.getRestaurantName();
        if (items.getTag().equals("1")) {
            holder.itemView.setTag("1");
            recyclerView.scrollToPosition(position);
            holder.txtName.setTag("1");
            holder.txtName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_time_slot_style));
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        } else {
            holder.itemView.setTag("0");
            holder.txtName.setTag("0");
            holder.txtName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.closed_list_time_slot_style));
            holder.txtName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        }
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView txtName;


        public SingleItemRowHolder(final View view) {
            super(view);
            this.txtName = (TextView) view.findViewById(R.id.txtMenuName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("<>click-", "view clicked ==> " + view.getTag());
                    if (view.getTag().equals("1")) {
                        String userId = "";
                        if (SharedData.getIsRemember(mContext) == true)
                            userId = SharedData.getUserId(mContext);
                        else
                            userId = MyApplication.getGlobalData().getUserId();

                        if (userId != "" && txtName.getTag().toString().equals("1")) {
                            Intent filter = new Intent(v.getContext(), BookATable.class);
                            filter.putExtra("restaurantId", restaurantId);
                            filter.putExtra("people", people);
                            filter.putExtra("date", bookDate);
                            filter.putExtra("name", restaurantName);
                            mContext.startActivity(filter);
                        } else {
                            if (timeSlotInterface != null)
                                timeSlotInterface.onTimeSlotClick(restaurantId);
                        }
                    }
                }
            });
        }
    }

    public interface timeSlotInterface {
        void onTimeSlotClick(String restaurantId);
    }

}