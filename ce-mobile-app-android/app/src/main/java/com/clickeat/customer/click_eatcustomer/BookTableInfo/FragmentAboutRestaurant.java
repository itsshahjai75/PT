package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.google.android.gms.maps.model.LatLng;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pivotech on 22/11/17.
 */

public class FragmentAboutRestaurant extends Fragment {
    private View m_myFragmentView;
    String param1;
    private IconTextView img_btn_map;
    private ArrayList<RestaurantDetailModel> dataModel;
    private TextView txtAddress, textAbout;
    private LinearLayout layoutFacilitiesDetails, layoutThumblinesDetails, layoutOffersDetails,
            layoutMapDetails, layoutPaymentDetails;
    private static final String ARG_PARAM = "param1";
    private ListView listviewServices;

    public static FragmentAboutRestaurant newInstance(String param1) {
        FragmentAboutRestaurant fragment = new FragmentAboutRestaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1 + "");
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAboutRestaurant() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_myFragmentView = inflater.inflate(R.layout.layout_about_fragment, container, false);

        findIds();
        inits();
        return m_myFragmentView;
    }

    private void findIds() {
        textAbout = m_myFragmentView.findViewById(R.id.status);
        img_btn_map = m_myFragmentView.findViewById(R.id.img_btn_map);
        txtAddress = m_myFragmentView.findViewById(R.id.txtAddress);
        layoutFacilitiesDetails = m_myFragmentView.findViewById(R.id.layoutFacilitiesDetails);
        layoutThumblinesDetails = m_myFragmentView.findViewById(R.id.layoutThumblinesDetails);
        layoutOffersDetails = m_myFragmentView.findViewById(R.id.layoutOffersDetails);
        layoutMapDetails = m_myFragmentView.findViewById(R.id.layoutMapDetails);
        layoutPaymentDetails = m_myFragmentView.findViewById(R.id.layoutPaymentDetails);
        listviewServices = m_myFragmentView.findViewById(R.id.listviewServices);
    }

    private void inits() {
        final Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface roboto_regular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");
        List<RestaurantDetailModel> data = MyApplication.getGlobalData().getRestaurantsFullData();
        if (data.size() > 0) {
            final RestaurantDetailModel restaurant = data.get(0);
            textAbout.setText(restaurant.getAboutRestaurant());
            txtAddress.setText(restaurant.getAddress());

            List<String> imagesList = new ArrayList<>();
            imagesList = restaurant.getImagesList();
            List<String> thumblinesPaths = new ArrayList<>();
            if (imagesList.size() > 0) {
                for (int im = 0; im < imagesList.size(); im++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_height),
                            getResources().getDimensionPixelSize(R.dimen.linear_height));
                    layoutParams.setMargins(7, 5, 7, 5);
                    ImageView image = new ImageView(getActivity());
                    image.setPadding(15, 5, 15, 5);
                    image.setLayoutParams(layoutParams);
                    String imgPath1 = imagesList.get(im);
                    imgPath1 = imgPath1.startsWith(".") ? imgPath1.substring(1) : imgPath1;
                    Picasso.with(getActivity())
                            .load(APIConstants.URL + "/" + imgPath1)
                            .into(image);
                    layoutThumblinesDetails.addView(image);
                    thumblinesPaths.add(APIConstants.URL + "/" + imgPath1);
                    image.setTag(im);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("<>log-", "position " + v.getTag() + "");
                            Integer tag = Integer.parseInt(v.getTag() + "");
                            Intent i = new Intent(getActivity(), ImageFullscreenView.class);
                            i.putStringArrayListExtra("thumblines", (ArrayList<String>) thumblinesPaths);
                            i.putExtra("position", tag);
                            startActivity(i);
                        }
                    });
                }
            } else {
                TextView textView = new TextView(getActivity());
                textView.setTextSize(11);
                textView.setText("No photos found");
                layoutThumblinesDetails.addView(textView);
            }

            HashMap<String, String> facilitiesList = new HashMap<>();
            facilitiesList = restaurant.getFacilities();
            if (facilitiesList.size() > 0) {
                for (Map.Entry<String, String> map : facilitiesList.entrySet()) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_height),
                            getResources().getDimensionPixelSize(R.dimen.linear_height));
                    layoutParams.setMargins(7, 5, 7, 5);
                    LinearLayout linear = new LinearLayout(getActivity());
                    linear.setOrientation(LinearLayout.VERTICAL);
                    linear.setGravity(Gravity.CENTER);
                    linear.setBackground(getResources().getDrawable(R.drawable.menu_list_style));
                    TextView text = new TextView(getActivity());
                    text.setTextColor(getResources().getColor(R.color.colorBlack));
                    text.setTypeface(roboto);
                    text.setLines(2);
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTextSize(8);
                    text.setGravity(Gravity.CENTER);
//                TextView textImg = new TextView(mContext);
                    IconTextView textImg = new IconTextView(getActivity());
                    textImg.setTextSize(10);
                    textImg.setTypeface(roboto_regular);
                    textImg.setGravity(Gravity.CENTER);
                    textImg.setPadding(5, 5, 5, 5);
                    textImg.setTextColor(getResources().getColor(R.color.colorBlack));
                    text.setText(map.getValue().toString());
                    Log.d("<>log-", " get value from map ==> " + map.getValue());
                    textImg.setText("{" + map.getKey() + "}");
                /*int resId = mContext.getResources().getIdentifier(map.getKey(), "string", packageName);
                Log.d("<>log-", " res id ===> " + resId + "");
                if (resId != 0)
                    textImg.setText(mContext.getString(resId));*/
                    linear.addView(textImg);
                    linear.addView(text);
                    layoutFacilitiesDetails.addView(linear, layoutParams);
                }
            } else {
                TextView textView = new TextView(getActivity());
                textView.setTextSize(11);
                textView.setText("No facilities found");
                layoutFacilitiesDetails.addView(textView);
            }

            TextView textView1 = new TextView(getActivity());
            textView1.setTextSize(11);
            textView1.setText("No payment methods found");
            layoutPaymentDetails.addView(textView1);

            layoutMapDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = (FragmentActivity) (getActivity());
                    FragmentManager fm = activity.getSupportFragmentManager();
                    MapsActivity dialog = MapsActivity.newInstance(APIConstants.getLatitude(),
                            APIConstants.getLongitude(), Double.parseDouble(restaurant.getLongitude()),
                            Double.parseDouble(restaurant.getLatitude()), new ArrayList<LatLng>());
                    dialog.show(fm, "dialog");
                }
            });

            HashMap<Integer, String> serviceList = new HashMap<>();
            serviceList = restaurant.getAdditionalService();
            if (serviceList.size() > 0) {
                List<String> items = new ArrayList<>();
                for (Map.Entry<Integer, String> service : serviceList.entrySet()) {
                    items.add(service.getValue());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.item_listview_additional_service, items);
                listviewServices.setAdapter(adapter);
            } else {
                TextView textView = new TextView(getActivity());
                textView.setTextSize(11);
                textView.setText("No services found");
                layoutOffersDetails.addView(textView);
            }

        }
    }
}
