package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.SharedData;

import java.util.List;

/**
 * Created by pivotech on 30/10/17.
 */

public class BottomSheetAdapter extends BaseAdapter{
    private List<BottomSheetPOJO> mItems;
    private ItemListener mListener;
    private Context mContext;
    public static int selected_item = 0;
    private ListView lstView;

    public BottomSheetAdapter(List<BottomSheetPOJO> items, ItemListener listener, Context mContext, ListView lstView) {
        mItems = items;
        this.mContext = mContext;
        mListener = listener;
        this.lstView = lstView;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }

    private class SheetViewHolder {
        public CheckedTextView textView;

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position).getmTitle().toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SheetViewHolder view = null;
        BottomSheetPOJO model = mItems.get(position);
        Integer defaultSelectItem = 0;
        LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
        if (view == null) {
            view = new SheetViewHolder();
            convertView = inflator.inflate(R.layout.layout_bottom_sheet_item, null);
            view.textView = (CheckedTextView) convertView.findViewById(R.id.textView);
            convertView.setTag(view);
        } else {
            view = (SheetViewHolder) convertView.getTag();
        }
        view.textView.setText(model.getmTitle());
        Log.d("<>list-", " shared data name ==> "+SharedData.getSortingType(mContext));
        if (SharedData.getSortingType(mContext).equals(model.getmTitle())) {
            Log.d("<>list-", " equals same ");

        }
        for (int bt = 0; bt < mItems.size(); bt++) {
            if (mItems.get(bt).getmTitle().equals(SharedData.getSortingType(mContext))) {
                defaultSelectItem = mItems.get(bt).getmState();
                selected_item = defaultSelectItem;
            }
        }
        if (position == selected_item) {
            view.textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            /*view.textView.setChecked(true);
            view.textView.setFocusable(true);
            view.textView.setSelected(true);*/
            view.textView.setPressed(true);
            lstView.setItemChecked(position, true);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(mItems.get(position));
                }
            }
        });

        return convertView;
    }

   /* @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bottom_sheet_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
        Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
        Integer defaultSelectItem = 0;
        for (int bt = 0; bt < mItems.size(); bt++) {
            if (mItems.get(bt).getmTitle().equals(SharedData.getSortingType(mContext))) {
                defaultSelectItem = mItems.get(bt).getmState();
                selected_item = defaultSelectItem;
            }
        }
        *//*if (position == 0) {
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.textView.setClickable(false);
            holder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
//            holder.textView.setGravity(Gravity.CENTER);
            Resources r = mContext.getResources();
            holder.textView.setPadding(Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 22,r.getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 7,r.getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 7,r.getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 7,r.getDisplayMetrics())));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            holder.textView.setLayoutParams(params);
        }*//*

        if (position == selected_item)
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        holder.textView.setTypeface(roboto);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public BottomSheetPOJO item;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void setData(BottomSheetPOJO item) {
            this.item = item;
            textView.setText(item.getmTitle());
            Log.d("<>bottom-", " state of item --> " + item.getmState());
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }*/

    public interface ItemListener {
        void onItemClick(BottomSheetPOJO item);
    }
}
