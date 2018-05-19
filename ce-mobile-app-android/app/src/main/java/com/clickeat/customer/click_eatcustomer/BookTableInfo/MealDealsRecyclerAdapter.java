package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OMealDeals;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pivotech on 25/4/18.
 */

public class MealDealsRecyclerAdapter extends RecyclerView.Adapter<MealDealsRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<OMealDeals> meals;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mdTitle, mdDescription, mdPrice;
        private ImageView mdImage;

        public MyViewHolder(View v) {
            super(v);

            mdImage = v.findViewById(R.id.mdImage);
            mdTitle = v.findViewById(R.id.mdTitle);
            mdDescription = v.findViewById(R.id.mdDescription);
            mdPrice = v.findViewById(R.id.mdPrice);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MealDealsRecyclerAdapter(Context mContext, List<OMealDeals> meals) {
        this.mContext = mContext;
        this.meals = meals;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MealDealsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_deals, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Regular.ttf");
        OMealDeals datamodel = meals.get(position);

        holder.mdTitle.setText(datamodel.getMealName());
        holder.mdTitle.setTypeface(roboto_regular);
        holder.mdDescription.setText(datamodel.getMealDescription());
        holder.mdDescription.setTypeface(roboto);
        holder.mdPrice.setText(datamodel.getMealCurrency() + datamodel.getMealPrice());
        holder.mdPrice.setTypeface(roboto);
        Picasso.with(mContext)
                .load(APIConstants.URL + "/" + datamodel.getMealImage())
                .into(holder.mdImage);

    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}
