package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.DishNameDetailModel;
import com.clickeat.customer.click_eatcustomer.DataModel.MenuDetailsModel;
import com.clickeat.customer.click_eatcustomer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pivotech on 14/3/18.
 */

public class ExapandableListMenuAdapter extends BaseExpandableListAdapter{
    private Context mContext;
    private List<MenuDetailsModel> dataMenuModel;
    public ExapandableListMenuAdapter(Context mContext, List<MenuDetailsModel> dataMenuModel) {
        this.mContext = mContext;
        this.dataMenuModel = dataMenuModel;
    }

    @Override
    public int getGroupCount() {
        return dataMenuModel.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dataMenuModel.get(groupPosition).getDishnameDetails().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dataMenuModel.get(groupPosition).getCategoryName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.dataMenuModel.get(groupPosition).getDishnameDetails().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_exp_group_item, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }
    private class ViewHolder {
        TextView dishName, dishCurrency;
        TextView dishPrice;
        ImageView isNew;
        ImageView isVeg;
        ImageView isCalory;
        ImageView isSpecialDiet;
        ImageView isChilly;
        TextView dishDescription;
        TextView txtAllergyContent;
        TextView txtCalory;
        TextView txtSpecialDiet;
        LinearLayout dishSpiceLayout, dishMainLayout, layoutAllergy;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder view = null;
        final DishNameDetailModel model = (DishNameDetailModel) getChild(groupPosition, childPosition);

        LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
        if (view == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_exp_child_items, null);
            view.dishName = (TextView) convertView.findViewById(R.id.dishName);
            view.dishPrice = (TextView) convertView.findViewById(R.id.dishPrice);
            view.dishDescription = (TextView) convertView.findViewById(R.id.dishDescription);
            view.isVeg = (ImageView) convertView.findViewById(R.id.dishIsVegetarian);
            view.isCalory = (ImageView) convertView.findViewById(R.id.dishCalloryIcon);
            view.isNew = (ImageView) convertView.findViewById(R.id.dishIsNew);
            view.isSpecialDiet = (ImageView) convertView.findViewById(R.id.dishSpecialDietIcon);
            view.txtAllergyContent = (TextView) convertView.findViewById(R.id.dishAllergyList);
            view.txtCalory = (TextView) convertView.findViewById(R.id.txtCaloryText);
            view.txtSpecialDiet = (TextView) convertView.findViewById(R.id.txtSpecialDietText);
            view.dishSpiceLayout = (LinearLayout) convertView.findViewById(R.id.dishSpiceLayout);
            view.dishMainLayout = (LinearLayout) convertView.findViewById(R.id.dishMainLayout);
            view.layoutAllergy = convertView.findViewById(R.id.layoutAllergy);
            view.dishCurrency = convertView.findViewById(R.id.dishCurrency);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }


        /*if (childPosition % 2 == 1) {
            view.dishMainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            view.dishMainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorListBg));
        }*/
        view.dishName.setText(model.getDishName());
        view.dishPrice.setText(model.getRetailPriceRegular());
        view.dishCurrency.setText(model.getCurrency());
        view.dishDescription.setText(model.getDescription());
        if (model.getIsVegetarian().equals("true"))
            view.isVeg.setVisibility(View.VISIBLE);
        if (model.getSpiceLevel().equals("Not Applicable"))
            view.dishSpiceLayout.setVisibility(View.GONE);
        else if (model.getSpiceLevel().equals("Mild")) {
            ImageView image = new ImageView(mContext);
            image.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(params);
            image.setImageResource(R.drawable.ic_chili_pepper);
            view.dishSpiceLayout.addView(image);
        } else if (model.getSpiceLevel().equals("Regular")) {
            for (int i = 0; i <= 1; i++) {
                ImageView image = new ImageView(mContext);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                image.setLayoutParams(params);
                image.setImageResource(R.drawable.ic_chili_pepper);
                image.setPadding(0, 0, 0, 0);
                view.dishSpiceLayout.addView(image);
            }
        } else if (model.getSpiceLevel().equals("Hot")) {
            for (int i = 0; i <= 2; i++) {
                ImageView image = new ImageView(mContext);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                image.setLayoutParams(params);
                image.setImageResource(R.drawable.ic_chili_pepper);
                image.setPadding(0, 0, 0, 0);
                view.dishSpiceLayout.addView(image);
            }
        } else if (model.getSpiceLevel().equals("Extra Hot")) {
            for (int i = 0; i <= 3; i++) {
                ImageView image = new ImageView(mContext);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                image.setLayoutParams(params);
                image.setImageResource(R.drawable.ic_chili_pepper);
                view.dishSpiceLayout.addView(image);
            }
        }

        List<String> allergyList = new ArrayList<>();
        allergyList = model.getAllergyList();
        if (allergyList.size() > 0) {
            StringBuilder allergyText = new StringBuilder();
            for (int allergy = 0; allergy < allergyList.size(); allergy++) {
                if (allergy + 1 == allergyList.size())
                    allergyText.append(allergyList.get(allergy));
                else
                    allergyText.append(allergyList.get(allergy) + ",");
            }
            view.txtAllergyContent.setText(allergyText);
        } else {
            view.txtAllergyContent.setText("none");
        }

        List<String> dietList = new ArrayList<>();
        dietList = model.getDiet();
        if (dietList.size() > 0) {
            StringBuilder dietText = new StringBuilder();
            for (int diet = 0; diet < dietList.size(); diet++) {
                if (diet + 1 == dietList.size())
                    dietText.append(dietList.get(diet));
                else
                    dietText.append(dietList.get(diet) + ",");
            }
            view.txtSpecialDiet.setText(dietText);
        } else {
            view.txtSpecialDiet.setVisibility(View.GONE);
            view.isSpecialDiet.setVisibility(View.GONE);
        }

        if (model.getCalorieRegular() != "") {
            view.isCalory.setVisibility(View.VISIBLE);
            view.txtCalory.setVisibility(View.VISIBLE);
            view.txtCalory.setText(model.getCalorieRegular().toString() + " kcal approx.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date strDate = null;
        Boolean your_date_is_outdated = false;
        try {
            if (model.getEndDate() != "") {
                strDate = sdf.parse(model.getEndDate());
                if (System.currentTimeMillis() < strDate.getTime()) {
                    your_date_is_outdated = true;
                } else {
                    your_date_is_outdated = false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (your_date_is_outdated)
            view.isNew.setVisibility(View.VISIBLE);

        if (model.getDishData() == false){
            view.layoutAllergy.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
