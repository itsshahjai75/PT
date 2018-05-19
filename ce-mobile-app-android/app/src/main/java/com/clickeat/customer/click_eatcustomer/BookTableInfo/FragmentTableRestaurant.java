package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OTableMap;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by pivotech on 27/3/18.
 */

public class FragmentTableRestaurant extends Fragment {
    private View m_myFragmentView;
    String param1;
    private static final String ARG_PARAM = "param1";
    private LinearLayout layoutTableImages;
    private ViewPager pagerTableImages;
    private CircleIndicator indicator;
    private static int currentPage = 0;
    private ImageView prev, next;

    public static FragmentTableRestaurant newInstance(String param1) {
        FragmentTableRestaurant fragment = new FragmentTableRestaurant();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1 + "");
        fragment.setArguments(args);
        return fragment;
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
        m_myFragmentView = inflater.inflate(R.layout.layout_tablemap_fragment, container, false);
        findIds();
        init();
//        pagerTableImages.setPagingEnabled(false);
        prev.setOnClickListener(onClickListener(0));
        next.setOnClickListener(onClickListener(1));
        return m_myFragmentView;
    }

    private View.OnClickListener onClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i > 0) {
                    //next page
                    if (pagerTableImages.getCurrentItem() < pagerTableImages.getAdapter().getCount() - 1) {
                        pagerTableImages.setCurrentItem(pagerTableImages.getCurrentItem() + 1);
                    }
                } else {
                    //previous page
                    if (pagerTableImages.getCurrentItem() > 0) {
                        pagerTableImages.setCurrentItem(pagerTableImages.getCurrentItem() - 1);
                    }
                }
            }
        };
    }

    private void findIds() {
//        layoutTableImages = m_myFragmentView.findViewById(R.id.layoutTableImages);
        pagerTableImages = m_myFragmentView.findViewById(R.id.pagerTableImages);
        indicator = m_myFragmentView.findViewById(R.id.indicator);
        prev = m_myFragmentView.findViewById(R.id.prev);
        next = m_myFragmentView.findViewById(R.id.next);
    }

    private void init() {
        List<RestaurantDetailModel> data = MyApplication.getGlobalData().getRestaurantsFullData();
        if (data.size() > 0) {
            final RestaurantDetailModel restaurant = data.get(0);
            List<OTableMap> tableImagsMap = new ArrayList<>();
            tableImagsMap = restaurant.getMapImages();
            if (tableImagsMap.size() < 1) {
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }

            pagerTableImages.setAdapter(new MyAdapter(getActivity(), tableImagsMap));
            indicator.setViewPager(pagerTableImages);

            // Auto start of viewpager
            final Handler handler = new Handler();
            final List<OTableMap> finalTableImages = tableImagsMap;
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == finalTableImages.size()) {
                        currentPage = 0;
                    }
                    pagerTableImages.setCurrentItem(currentPage++, true);
                }
            };
           /* Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 2500, 2500);*/
            /*if (tableImages.size() > 0) {
                for (int img = 0; img < tableImages.size(); img++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(7, 5, 7, 5);
                    ImageView imageView = new ImageView(getActivity());
                    String imgPath = tableImages.get(img).toString();
                    imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
                    if (!imgPath.equals("")) {
                        Picasso.with(getActivity())
                                .load(APIConstants.URL + imgPath)
                                .into(imageView);
                    }
                    imageView.setLayoutParams(layoutParams);
                    layoutTableImages.addView(imageView);
                }

            }*/
        }
    }

    public class MyAdapter extends PagerAdapter {

        private List<OTableMap> tableImagsMap;
        private LayoutInflater inflater;
        private Context context;

        public MyAdapter(Context context, List<OTableMap> tableImagsMap) {
            this.context = context;
            this.tableImagsMap = tableImagsMap;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return tableImagsMap.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View myImageLayout = inflater.inflate(R.layout.layout_image_slider, view, false);
            OTableMap tableData = tableImagsMap.get(position);
            TouchImageView myImage = (TouchImageView) myImageLayout
                    .findViewById(R.id.image);
            TextView txtFloorName = myImageLayout.findViewById(R.id.txtFloorName);
            TextView txtRoomName = myImageLayout.findViewById(R.id.txtRoomName);
//            myImage.setImageResource(images.get(position));
            txtFloorName.setText("Floor " + tableData.getFloorName());
            txtRoomName.setText("Room " + tableData.getRoomName());
            String imgPath = tableData.getImages();
            imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
            if (!imgPath.equals("")) {
                Picasso.with(getActivity())
                        .load(APIConstants.URL + imgPath)
                        .into(myImage);
            }
            view.addView(myImageLayout, 0);
            return myImageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
