package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OTableMap;
import com.clickeat.customer.click_eatcustomer.DataModel.RestaurantDetailModel;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TableMaps extends Activity {

    private ViewPager pagerTableImages;
    private TextView txtTitleTableMap, txtMapImagesStatus;
    private Button btnTableMapClose;
    private ImageView prev, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_maps);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        findIds();
        init();

        btnTableMapClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerTableImages.setCurrentItem(i);
            }
        };
    }

    private void findIds() {
        pagerTableImages = findViewById(R.id.view_pager_table_map);
//        txtTitleTableMap = findViewById(R.id.txtTitleTableMap);
        btnTableMapClose = findViewById(R.id.btnTableMapClose);
        txtMapImagesStatus = findViewById(R.id.txtMapImagesStatus);
    }

    private void init() {
        List<RestaurantDetailModel> data = MyApplication.getGlobalData().getRestaurantsFullData();
        Log.d("<>tab-", " data of size --> "+data.size());
        if (data.size() > 0) {
            txtMapImagesStatus.setVisibility(View.GONE);
            pagerTableImages.setVisibility(View.VISIBLE);
            final RestaurantDetailModel restaurant = data.get(0);
            List<OTableMap> tableImagsMap = new ArrayList<>();
            Log.d("<>tab-", " table images ==> "+tableImagsMap.size());
            tableImagsMap = restaurant.getMapImages();
            pagerTableImages.setAdapter(new MyAdapter(TableMaps.this, tableImagsMap));
            String name = "<font color=#FF4823>" + getResources().getString(R.string.maps) +
                    "</font> <font color=#000>" + restaurant.getTitle() + "</font>";
//            txtTitleTableMap.setText(Html.fromHtml(name));
            /*// Auto start of viewpager
            final Handler handler = new Handler();
            final  List<OTableMap> finalTableImages = tableImagsMap;
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == finalTableImages.size()) {
                        currentPage = 0;
                    }
                    pagerTableImages.setCurrentItem(currentPage++, true);
                }
            };*/
        }else {
            txtMapImagesStatus.setVisibility(View.VISIBLE);
            pagerTableImages.setVisibility(View.GONE);
        }
    }

   /* private void inflateThumbnails(String img, Integer position) {
        View imageLayout = getLayoutInflater().inflate(R.layout.item_image, null);
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_thumb);
        imageView.setTag(position);
        Integer i= (Integer) imageView.getTag();
        imageView.setOnClickListener(onChagePageClickListener(i));
        String imgPath = img;
        imgPath = imgPath.startsWith(".") ? imgPath.substring(1) : imgPath;
        if (!imgPath.equals("")) {
            Picasso.with(TableMaps.this)
                    .load(APIConstants.URL + imgPath)
                    .into(imageView);
        }
        //add imageview
        thumbnailsContainer.addView(imageLayout);

    }*/

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
                Picasso.with(TableMaps.this)
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
