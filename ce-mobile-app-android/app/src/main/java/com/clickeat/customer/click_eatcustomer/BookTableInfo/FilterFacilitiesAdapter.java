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

import java.util.ArrayList;

/**
 * Created by pivotech on 13/12/17.
 */

public class FilterFacilitiesAdapter extends BaseAdapter {
    ArrayList<OFacilities> facilities;
    Context context;
    public FilterFacilitiesAdapter(Context context, ArrayList<OFacilities> facilities) {
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

   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OFacilities user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_filter_listview_items, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.filterTxtName);
        IconTextView tvIcon = (IconTextView) convertView.findViewById(R.id.filterIcon);
        CheckBox chkStatus = (CheckBox) convertView.findViewById(R.id.filterCheckbox);
        // Populate the data into the template view using the data object
        tvName.setText(user.getName());
        tvIcon.setText("{" + user.getIcon() + "}");
        // Return the completed view to render on screen
        return convertView;
    }
*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ViewHolder view = null;
        LayoutInflater inflator = ((Activity) context).getLayoutInflater();
        final ArrayList<String> cuisine = MyApplication.getGlobalData().getFacilitiesList();
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
                        Log.d("<>resta-", " item checked true==> ");
                        if (!cuisine.contains(facilities.get(position).getName()))
                            cuisine.add(facilities.get(position).getName());
                        facilities.get(position).setStatus(true);
                    } else {
                        // code here
                        Log.d("<>resta-", " item checked false==> ");
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
        Log.d("<>resta-", " cuisine list ==> " + cuisine.toString());
//        MyApplication.getGlobalData().addCuisineList(cuisine);
        return convertView;
    }
}
