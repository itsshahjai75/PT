package com.clickeat.restaurant.click_eatrestaurant.adapters.customspinner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.restaurant.click_eatrestaurant.DataModel.FloorModel;
import com.clickeat.restaurant.click_eatrestaurant.R;

import java.util.List;

/**
 * Created by android on 16/4/18.
 */

public class FloorSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<FloorModel> items;
    private final int mResource;

    public FloorSpinnerAdapter(Context context,  @LayoutRes int resource, List objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;

    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FloorModel rowItem = items.get(position);

        View rowview = mInflater.inflate(R.layout.custom_spinner_items_with_left_image,null,true);

        ImageView icon = (ImageView) rowview.findViewById(R.id.imageView);
        icon.setImageResource(R.drawable.ic_floor);
        TextView names = (TextView) rowview.findViewById(R.id.textView);
        names.setText(rowItem.getName());

        return rowview;
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view= mInflater.inflate(R.layout.custom_spinner_items_with_left_image, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        icon.setImageResource(R.drawable.ic_floor);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(items.get(position).getName().toString());
        return view;

    }

}