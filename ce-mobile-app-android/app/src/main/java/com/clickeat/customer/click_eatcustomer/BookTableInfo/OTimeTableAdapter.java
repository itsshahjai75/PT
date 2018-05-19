package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OTimeTable;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pivotech on 13/12/17.
 */

public class OTimeTableAdapter extends BaseAdapter {
    List<OTimeTable> times;
    Context context;

    public OTimeTableAdapter(Context context, List<OTimeTable> times) {
        super();
        this.context = context;
        this.times = times;
    }

    private class ViewHolder {
        TextView day;
        TextView time;
    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public String getItem(int position) {
        return times.get(position).toString();
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
        if (view == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_time_list_item, null);
            view.day = (TextView) convertView.findViewById(R.id.txtDayName);
            view.time = (TextView) convertView.findViewById(R.id.txtDayTime);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        view.day.setText("" + times.get(position).getDay());
        view.time.setText("" + times.get(position).getTime());
        return convertView;
    }
}
