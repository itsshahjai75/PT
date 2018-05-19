package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.DishNameDetailModel;
import com.clickeat.customer.click_eatcustomer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pivotech on 13/12/17.
 */

public class MenuItemAdapter extends BaseAdapter {
    List<DishNameDetailModel> menus = new ArrayList<>();
    Context context;

    public MenuItemAdapter(Context context, List<DishNameDetailModel> menus) {
        super();
        for (int t = 0; t < menus.size(); t++) {
            Log.d("<>list-", "in menu ===> " + menus.get(t).toString());
        }
        this.context = context;
        this.menus = menus;
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
        LinearLayout dishSpiceLayout;
        LinearLayout dishMainLayout, layoutAllergy;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public String getItem(int position) {
        return menus.get(position).getDishName().toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ViewHolder view = null;
        DishNameDetailModel model = menus.get(position);
        LayoutInflater inflator = ((Activity) context).getLayoutInflater();
        if (view == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_menu_list_view_item, null);
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
        if (position % 2 == 1) {
            view.dishMainLayout.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            view.dishMainLayout.setBackgroundColor(context.getResources().getColor(R.color.colorListBg));
        }
        view.dishName.setText(model.getDishName());
        view.dishPrice.setText(model.getRetailPriceRegular());
        view.dishCurrency.setText(model.getCurrency());
        view.dishDescription.setText(model.getDescription());
        if (model.getIsVegetarian().equals("true"))
            view.isVeg.setVisibility(View.VISIBLE);
        if (model.getSpiceLevel().equals("Not Applicable"))
            view.dishSpiceLayout.setVisibility(View.GONE);
        else if (model.getSpiceLevel().equals("Mild")) {
            ImageView image = new ImageView(context);
            image.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(params);
            image.setImageResource(R.drawable.ic_chili_pepper);
            view.dishSpiceLayout.addView(image);
        } else if (model.getSpiceLevel().equals("Regular")) {
            for (int i = 0; i <= 1; i++) {
                ImageView image = new ImageView(context);
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
                ImageView image = new ImageView(context);
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
                ImageView image = new ImageView(context);
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
}
