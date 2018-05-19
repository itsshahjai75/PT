package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
        Log.d("<>map=", "in adapter ");
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.layout_map_info_window, null);

        TextView txtInfoName = view.findViewById(R.id.txtInfoName);
        TextView txtInfoCuisines = view.findViewById(R.id.txtInfoCuisines);
        ImageView imgInfoLogo = view.findViewById(R.id.imgInfoLogo);
        TextView textInfoLogoNot = view.findViewById(R.id.textInfoLogoNot);

        TextView txtInfoAvgInfo = view.findViewById(R.id.txtInfoAvgInfo);
        TextView txtInfoCollection = view.findViewById(R.id.txtInfoCollection);
        TextView txtInfoDelivery = view.findViewById(R.id.txtInfoDelivery);

        RestaurantDetailModel model = (RestaurantDetailModel) marker.getTag();
        txtInfoName.setText(model.getTitle());
        if (model.getDelivery() == true)
            txtInfoDelivery.setText(model.getAverageDeliveryTime() + " " + context.getString(R.string.mins_title));
        else
            txtInfoDelivery.setText(R.string.no_title);
        if (model.getCollection() == true)
            txtInfoCollection.setText(model.getAverageCollectionTime() + " " + context.getString(R.string.mins_title));
        else
            txtInfoCollection.setText(R.string.no_title);

        List<String> cuisines = new ArrayList<>();
        cuisines = model.getCuisines();
        if (cuisines.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < cuisines.size(); c++) {
                if (c + 1 == cuisines.size())
                    builder.append(cuisines.get(c));
                else
                    builder.append(cuisines.get(c) + ",");
            }
            txtInfoCuisines.setText(builder.toString());
        }

        String imgPath = model.getImage();
        imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
        Log.d("<>image-", " img path ==> " + APIConstants.URL + imgPath);
        if (!imgPath.equals("")) {
            Picasso.with(context)
                    .load(APIConstants.URL + imgPath)
                    .into(imgInfoLogo);
        } else {
            //get first letter of each String item
            textInfoLogoNot.setVisibility(View.VISIBLE);
            imgInfoLogo.setVisibility(View.GONE);
            String firstLetter = String.valueOf(model.getTitle().charAt(0));
            textInfoLogoNot.setText(firstLetter);
        }

        txtInfoAvgInfo.setText(model.getAveragePriceForTwo() + "");

        return view;
    }
}
