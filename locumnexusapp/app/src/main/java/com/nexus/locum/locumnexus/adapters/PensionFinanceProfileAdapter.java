package com.nexus.locum.locumnexus.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexus.locum.locumnexus.ProfessionalProfileActivity;
import com.nexus.locum.locumnexus.R;
import com.nexus.locum.locumnexus.modelPOJO.PensionFinanceProfileModel;
import com.nexus.locum.locumnexus.modelPOJO.PensionFinanceProfileModel;
import com.nexus.locum.locumnexus.utilities.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nexus.locum.locumnexus.utilities.APICall.ResponseWithAuthAPI;
import static com.nexus.locum.locumnexus.utilities.Const.CONST_PROFILE_JSON;
import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_IS_PROFILECOMPLETED;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_ID;
import static com.nexus.locum.locumnexus.utilities.Const.PREF_USER_TOKEN;

public class PensionFinanceProfileAdapter extends
        RecyclerView.Adapter<PensionFinanceProfileAdapter.MyViewHolder>  {

    public static List<PensionFinanceProfileModel> pensionRateList;

    private Context mContext;
    SharedPreferences CONST_SHAREDPREFERENCES;

    /**
     * View holder class
     */

    public PensionFinanceProfileAdapter(ArrayList<PensionFinanceProfileModel> pensinList, Context mContext) {
        this.pensionRateList = pensinList;
        this.mContext = mContext;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final PensionFinanceProfileModel dataModel = pensionRateList.get(position);


        if(position==0) {
            holder.tvamontFrom.setText(dataModel.getAmountFrom());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.50f);
            holder.tvamontFrom.setLayoutParams(params);
            holder.tvamountTo.setVisibility(View.GONE);
            holder.tvrate.setText(dataModel.getRate());
            holder.cbpensionrate.setVisibility(View.GONE);

        }else if (position==1){
            holder.tvamontFrom.setText(dataModel.getAmountFrom());
            holder.tvamountTo.setText(dataModel.getAmountTo());
            holder.tvrate.setText(dataModel.getRate());
            holder.cbpensionrate.setVisibility(View.GONE);

        }else{

            holder.tvamontFrom.setText(mContext.getString(R.string.pound_currency)+" "+dataModel.getAmountFrom());
            holder.tvamontFrom.setTextColor(mContext.getColor(R.color.md_grey_500));
            holder.tvamountTo.setText(mContext.getString(R.string.pound_currency)+" "+dataModel.getAmountTo());
            holder.tvamountTo.setTextColor(mContext.getColor(R.color.md_grey_500));
            holder.tvrate.setText(dataModel.getRate() +" % ");



            holder.cbpensionrate.setClickable(false);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int a=0;a<pensionRateList.size();a++){

                        if(a==position){
                            pensionRateList.get(a).setIschecked(true);
                        }else{
                            pensionRateList.get(a).setIschecked(false);
                        }

                    }
                    notifyDataSetChanged();


                }
            });

            if(dataModel.isIschecked()){
                holder.cbpensionrate.setChecked(true);
            }else{
                holder.cbpensionrate.setChecked(false);
            }

           /* holder.cbpensionrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for(int a=0;a<pensionRateList.size();a++){
                        if(a==position){
                            pensionRateList.get(a).setIschecked(true);
                        }else{
                            pensionRateList.get(a).setIschecked(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            });*/
        }

    }

    @Override
    public int getItemCount() {
        return pensionRateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvamontFrom,tvamountTo,tvrate;

        CheckBox cbpensionrate;

        public MyViewHolder(View view) {
            super(view);
            tvamontFrom = (TextView)view.findViewById(R.id.tvamontFrom);
            tvamountTo = (TextView)view.findViewById(R.id.tvamountTo);
            tvrate = (TextView)view.findViewById(R.id.tvrate);
            cbpensionrate = (CheckBox) view.findViewById(R.id.cbpensionrate);

        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pension_rate, parent, false);

        CONST_SHAREDPREFERENCES  = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);



        return new MyViewHolder(view);
    }



}