package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OCuisines;
import com.clickeat.customer.click_eatcustomer.DataModel.OFacilities;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.joanzapata.iconify.widget.IconTextView;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

/**
 * Created by pivotech on 13/12/17.
 */

public class FilterCuisinesAdapter extends BaseAdapter {
    ArrayList<OCuisines> facilities;
    Context context;

    public FilterCuisinesAdapter(Context context, ArrayList<OCuisines> facilities) {
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
        return facilities.get(position).getName().toString();
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
            view.status = (CheckBox) convertView.findViewById(R.id.filterCheckbox);
            view.status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag(); // Here
                    // we get  the position that we have set for the checkbox using setTag.
                    facilities.get(getPosition).setStatus(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if (isChecked) {
                        //do sometheing here
                        if (!cuisine.contains(facilities.get(position).getName()))
                            cuisine.add(facilities.get(position).getName());
                        facilities.get(position).setStatus(true);
                    } else {
                        // code here
                        facilities.get(position).setStatus(false);
                        if (cuisine.contains(facilities.get(position).getName()))
                            cuisine.remove(facilities.get(position).getName());
                    }
                }
            });
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        view.status.setTag(position);
        view.name.setText("" + facilities.get(position).getName());
        if (cuisine.contains(facilities.get(position).getName()))
            facilities.get(position).setStatus(true);
        view.status.setChecked(facilities.get(position).getStatus());
        return convertView;
    }
}
