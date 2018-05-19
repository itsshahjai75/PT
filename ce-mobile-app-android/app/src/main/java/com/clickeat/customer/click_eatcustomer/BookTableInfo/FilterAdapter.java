package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OCuisines;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pivotech on 13/12/17.
 */

public class FilterAdapter extends BaseAdapter {
    List<String> facilities;
    Context context;

    public FilterAdapter(Context context, List<String> facilities) {
        super();
        this.context = context;
        this.facilities = facilities;
    }

    private class ViewHolder {
        TextView name;
        CheckBox status;
    }

    @Override
    public int getCount() {
        return facilities.size();
    }

    @Override
    public String getItem(int position) {
        return facilities.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) context).getLayoutInflater();
        final ArrayList<String> cuisine = MyApplication.getGlobalData().getCuisineList();
        if (view == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_filter_listview_items, null);
            view.name = (TextView) convertView.findViewById(R.id.filterTxtName);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        Log.d("<>list-", " item name ==> "+facilities.get(position).toString());
        view.name.setText("" + facilities.get(position).toString());
        return convertView;
    }
}
