package com.clickeat.customer.click_eatcustomer.MyAccounts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OAddressList;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.List;

/**
 * Created by pivotech on 28/2/18.
 */

public class AccountAddressAdapter extends RecyclerView.Adapter<AccountAddressAdapter.MyViewHolder> {
    private Context mContext;
    private List<OAddressList> addressList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAddType, txtAddText;
        private Button btnAddressEdit, btnAddressDelete;

        public MyViewHolder(View v) {
            super(v);

            txtAddText = v.findViewById(R.id.txtAddressDetails);
            txtAddType = v.findViewById(R.id.txtAddressType);
            btnAddressEdit = v.findViewById(R.id.btnAddressEdit);
            btnAddressDelete = v.findViewById(R.id.btnAddressDelete);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AccountAddressAdapter(Context mContext, List<OAddressList> addressList) {
        this.mContext = mContext;
        this.addressList = addressList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AccountAddressAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_address_item, parent, false);
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
        OAddressList adderss = addressList.get(position);
        try {
            holder.txtAddType.setText(adderss.getAddressType());
            holder.txtAddText.setText(adderss.getAddressText());

            holder.btnAddressEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentEdit = new Intent(mContext, AddressForm.class);
                    intentEdit.putExtra("addressId", adderss.getAddressId());
                    mContext.startActivity(intentEdit);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
