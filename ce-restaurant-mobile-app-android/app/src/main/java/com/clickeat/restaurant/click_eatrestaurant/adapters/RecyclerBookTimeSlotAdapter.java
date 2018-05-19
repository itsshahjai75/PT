package com.clickeat.restaurant.click_eatrestaurant.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.OBookTimeSlot;
import com.clickeat.restaurant.click_eatrestaurant.R;

import java.util.List;

/**
 * Created by android on 3/4/18.
 */


public class RecyclerBookTimeSlotAdapter extends RecyclerView.Adapter
        <RecyclerBookTimeSlotAdapter.ListItemViewHolder> {
    private List<OBookTimeSlot> items;
    private static SparseBooleanArray sSelectedItems;
    private static Context mContext;
    private static UpdateDataClickListener sClickListener;
    private static int sPosition;
    private static final int MULTIPLE = 0;
    private static final int SINGLE = 1;
    private static int sModo = 0;
    public boolean isClickable = true;

    public RecyclerBookTimeSlotAdapter(Context mContext, List<OBookTimeSlot> modelData, int modo) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        this.mContext = mContext;
        items = modelData;
        sSelectedItems = new SparseBooleanArray();
        sModo = modo;
    }

    public OBookTimeSlot getItem(int position) {
        return items.get(position);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.layout_menu_listview_item, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        OBookTimeSlot model = items.get(position);
        viewHolder.label.setText(model.getTime());
        viewHolder.layout.setSelected(sSelectedItems.get(position, false));

        if (sSelectedItems.get(position)) {
            viewHolder.label.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_time_slot_style_selected));
            viewHolder.label.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
//            sSelectedItems.clear();
        } else if (model.getStatus().equals("true")) {
            viewHolder.layout.setTag("0");
            viewHolder.label.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_time_slot_style));
            viewHolder.label.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            viewHolder.layout.setTag("1");
            viewHolder.label.setBackground(ContextCompat.getDrawable(mContext, R.drawable.closed_list_time_slot_style));
            viewHolder.label.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void selected(int position) {
        switch (sModo) {
            case SINGLE:
                sPosition = position;
                notifyDataSetChanged();
                break;
            case MULTIPLE:
            default:
                break;
        }
    }

    public void changeMode(int modo) {
        sModo = modo;
        sSelectedItems.clear();
        notifyDataSetChanged();
    }

    void setOnItemClickListener(UpdateDataClickListener clickListener) {
        Log.d("<>test-", " hello is in on item click");
        sClickListener = clickListener;
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView label;
        LinearLayout layout;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.txtMenuName);
            layout = (LinearLayout) itemView.findViewById(R.id.layoutBG);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("<>tag-", " hello is in on click"+v.getTag());
            if (v.getTag().equals("0")) {
                if (sSelectedItems.get(getAdapterPosition(), false)) {
                    sSelectedItems.delete(getAdapterPosition());
                    layout.setSelected(false);
//                label.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
                } else {
                    switch (sModo) {
                        case SINGLE:
                            sSelectedItems.put(sPosition, false);
                            break;
                        case MULTIPLE:
                        default:
                            break;
                    }
//                label.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    sSelectedItems.put(getAdapterPosition(), true);
                    layout.setSelected(true);
                }
                sClickListener.onItemClick(getAdapterPosition());
            }
        }
    }


    interface UpdateDataClickListener {
        void onItemClick(int position);
    }
}
