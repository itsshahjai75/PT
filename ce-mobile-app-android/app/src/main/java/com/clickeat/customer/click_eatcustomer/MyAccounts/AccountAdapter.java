package com.clickeat.customer.click_eatcustomer.MyAccounts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.R;

/**
 * Created by pivotech on 28/2/18.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {
    private Context mContext;
    private String[] accountName;
    private Integer[] accountImage;
    private final RecyclerViewClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView card_view_account;
        private TextView txtAccount;
        private ImageView imgAccount;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);

            card_view_account = v.findViewById(R.id.card_view_account);
            txtAccount = v.findViewById(R.id.txtAccount);
            imgAccount = v.findViewById(R.id.imgAccount);
            mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AccountAdapter(Context mContext, String[] accountName, Integer[] accountImage, RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.accountImage = accountImage;
        this.accountName = accountName;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AccountAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cardview_item_account, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v, listener);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Typeface roboto = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Roboto-Regular.ttf");
        try {
            holder.imgAccount.setImageResource(accountImage[position]);
            holder.txtAccount.setText(accountName[position]);
            holder.txtAccount.setTypeface(roboto_regular);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return accountName.length;
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
