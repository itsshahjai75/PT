package com.clickeat.customer.click_eatcustomer.MainHome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.R;

/**
 * Created by pivotech on 9/10/17.
 */

public class CustomMenuListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final int[] imgid;

    public CustomMenuListAdapter(Activity context, String[] itemname, int[] imgid) {
        super(context, R.layout.layout_drawer_list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.layout_drawer_list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.drawerTextview);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawerImageview);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    };
}
