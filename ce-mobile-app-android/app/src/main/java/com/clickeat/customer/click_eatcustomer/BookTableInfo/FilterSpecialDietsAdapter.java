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
import com.clickeat.customer.click_eatcustomer.DataModel.OSpecialDiet;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by pivotech on 13/12/17.
 */

public class FilterSpecialDietsAdapter extends BaseAdapter {
    ArrayList<OSpecialDiet> specialDiets;
    Context context;

    public FilterSpecialDietsAdapter(Context context, ArrayList<OSpecialDiet> specialDiets) {
        super();
        this.context = context;
        this.specialDiets = specialDiets;
    }

    private class ViewHolder {
        TextView name;
        CheckBox status;
    }

    @Override
    public int getCount() {
        return specialDiets.size();
    }

    @Override
    public String getItem(int position) {
        return specialDiets.get(position).getName().toString();
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
        final ArrayList<String> diets = MyApplication.getGlobalData().getDietsList();
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
                    specialDiets.get(getPosition).setStatus(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    if (isChecked) {
                        //do sometheing here
                        Log.d("<>diet-", " item checked true==> ");
                        if (!diets.contains(specialDiets.get(position).getName()))
                            diets.add(specialDiets.get(position).getName());
                        specialDiets.get(position).setStatus(true);
                    } else {
                        // code here
                        Log.d("<>diet-", " item checked false==> ");
                        specialDiets.get(position).setStatus(false);
                        if (diets.contains(specialDiets.get(position).getName()))
                            diets.remove(specialDiets.get(position).getName());
                    }
                    Log.d("<>diet-", " list of diet ==> "+diets.toString());
                }
            });
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        view.status.setTag(position);
        view.name.setText("" + specialDiets.get(position).getName());
        if (diets.contains(specialDiets.get(position).getName()))
            specialDiets.get(position).setStatus(true);
        view.status.setChecked(specialDiets.get(position).getStatus());
        return convertView;
    }
}
