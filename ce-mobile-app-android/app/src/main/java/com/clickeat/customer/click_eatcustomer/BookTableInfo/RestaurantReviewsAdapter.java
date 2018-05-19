package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickeat.customer.click_eatcustomer.DataModel.OReview;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pivotech on 28/2/18.
 */

public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.MyViewHolder> {
    private Context mContext;
    private List<OReview> reviews;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCustomer, txtRvDate, txtRvRate, txtRvPositive, txtRvNegative;
        private LinearLayout layoutPositive, layoutSuggestion, layoutImages;
        private ImageView imgViewShare;

        public MyViewHolder(View v) {
            super(v);
            txtCustomer = v.findViewById(R.id.txtRvCustomerName);
            txtRvDate = v.findViewById(R.id.txtRvDate);
            txtRvRate = v.findViewById(R.id.txtRvRate);
            txtRvPositive = v.findViewById(R.id.txtRvPositive);
            txtRvNegative = v.findViewById(R.id.txtRvNegative);
            layoutSuggestion = v.findViewById(R.id.layoutSuggestion);
            layoutPositive = v.findViewById(R.id.layoutPositive);
            layoutImages = v.findViewById(R.id.layoutImages);
            imgViewShare = v.findViewById(R.id.imgViewShare);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestaurantReviewsAdapter(Context mContext, List<OReview> reviews) {
        this.mContext = mContext;
        this.reviews = reviews;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RestaurantReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_reviews_items, parent, false);
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        OReview rev = reviews.get(position);
        try {
            holder.txtCustomer.setText(rev.getCustomerName());
            Log.d("<>log-", " name of customer ==> " + rev.getCustomerName());
            holder.txtCustomer.setTypeface(roboto);
            Date dateStart = df.parse(rev.getReviewDate().replaceAll("Z$", "+0000"));
            String reviewDate = sdf.format(dateStart);
            holder.txtRvDate.setText(reviewDate);
            holder.txtRvRate.setBackground(setBGDrawable(rev.getOverallRating()));
            holder.txtRvRate.setText(String.valueOf(rev.getOverallRating()) + "/5");
            if (!rev.getSuggestions().equals("")) {
                holder.layoutSuggestion.setVisibility(View.VISIBLE);
                holder.txtRvNegative.setText(rev.getSuggestions());
            }
            String positiveText = "";

            if (!rev.getPositives().equals("")) {
                holder.layoutPositive.setVisibility(View.VISIBLE);
                holder.txtRvPositive.setText(rev.getPositives());
                positiveText = "Positive review is :" + holder.txtRvPositive.getText().toString();
            }

            List<String> imagesList = new ArrayList<>();
            imagesList = rev.getImagesPath();
            Log.d("<>img=", " images lit count => " + imagesList.size());
            if (imagesList.size() > 0) {
                List<String> thumblinesPaths = new ArrayList<>();
                for (int im = 0; im < imagesList.size(); im++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.linear_weight),
                            mContext.getResources().getDimensionPixelSize(R.dimen.linear_height));
                    layoutParams.setMargins(0, 5, 0, 5);
                    ImageView image = new ImageView(mContext);
                    image.setPadding(5, 5, 5, 5);
                    image.setLayoutParams(layoutParams);

                    String imgPath1 = imagesList.get(im).toString();
                    imgPath1 = imgPath1.startsWith(".") ? imgPath1.substring(1) : imgPath1;
                    Picasso.with(mContext)
                            .load(APIConstants.URL + "/" + imgPath1)
                            .into(image);
                    holder.layoutImages.addView(image);
                    thumblinesPaths.add(APIConstants.URL + "/" + imgPath1);
                    Log.d("<>img=", " images path => " + APIConstants.URL + "/" + imgPath1);
                    image.setTag(im);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("<>log-", "position " + v.getTag() + "");
                            Integer tag = Integer.parseInt(v.getTag() + "");
                            Intent i = new Intent(mContext, ImageFullscreenView.class);
                            i.putStringArrayListExtra("thumblines", (ArrayList<String>) thumblinesPaths);
                            i.putExtra("position", tag);
                            mContext.startActivity(i);
                        }
                    });
                }
                MyApplication.getGlobalData().addImageThumblinePath(thumblinesPaths);
            } else {
                holder.layoutImages.setVisibility(View.GONE);
            }
            String reviewText = positiveText + "\n Overall rating of this restaurant is : " + rev.getOverallRating();

            holder.imgViewShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareBodyText = reviewText + " \n" +
                            "You should visit " + "https://test.pivotaltechnology.co.uk";
                   /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Restaurant Review");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));*/
                    try {
                        URI uri = null;

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        List<LabeledIntent> targetedShareIntents = new ArrayList<>();

                        PackageManager pm = mContext.getApplicationContext().getPackageManager();
                        List<ResolveInfo> activityList =
                                pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (final ResolveInfo app : activityList) {
                            String packageName = app.activityInfo.packageName;
                            String className = app.activityInfo.name;

                            if (packageName.equalsIgnoreCase(mContext.getPackageName())) {
                                continue;
                            }
                            Intent dummyIntent = new Intent();
                            dummyIntent.setAction(Intent.ACTION_SEND);
                            dummyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            dummyIntent.setComponent(new ComponentName(packageName, className));
                            LabeledIntent targetedShareIntent = new LabeledIntent(dummyIntent, packageName, app.loadLabel(pm), app.icon);

                            if (packageName.equalsIgnoreCase("com.whatsapp")) {
                                if (uri != null) {
                                    targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    targetedShareIntent.setType("image/jpg");
                                } else {
                                    targetedShareIntent.setType("text/plain");
                                }
                                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntents.add(0, targetedShareIntent);
                            } else if (packageName.equalsIgnoreCase("com.facebook.orca")
                                    || packageName.equalsIgnoreCase("com.facebook.katana")) {
                                targetedShareIntent.setComponent(new ComponentName(packageName, className));
                                targetedShareIntent.setAction(Intent.ACTION_SEND);
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntent.setType("text/plain");
                                targetedShareIntents.add(0, targetedShareIntent);
                            } /*else if (packageName.equalsIgnoreCase("com.android.mms")
                                    || (packageName.equalsIgnoreCase("com.google.android.talk"))) {
                                if (uri != null) {
                                    targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    targetedShareIntent.setType("image/jpg");
                                } else {
                                    targetedShareIntent.setType("text/plain");
                                }
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntents.add(0, targetedShareIntent);
                            } else if (packageName.equalsIgnoreCase("com.google.android.gm")) {
                                if (uri != null) {
                                    targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    targetedShareIntent.setType("image/jpg");
                                } else {
                                    targetedShareIntent.setType("text/plain");
                                }
                                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntents.add(0, targetedShareIntent);
                            }*/ else if (packageName.equalsIgnoreCase("com.twitter.android")
                                    || packageName.equalsIgnoreCase("com.twidroid")
                                    || packageName.equalsIgnoreCase("com.handmark.tweetcaster")
                                    || packageName.equalsIgnoreCase("com.thedeck.android")) {
                                targetedShareIntent.setType("text/plain");
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntents.add(0, targetedShareIntent);
                            } else {
                                if (uri != null) {
                                    targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    targetedShareIntent.setType("image/jpg");
                                } else {
                                    targetedShareIntent.setType("text/plain");
                                }
                                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                targetedShareIntents.add(targetedShareIntent);
                            }
                        }
                        Log.d("<>tags-", " arrays => " + targetedShareIntents.toArray(new Parcelable[]{}).toString());
                        Log.d("<>tags-", " arrays length => " + targetedShareIntents.toArray(new Parcelable[]{}).length + "");

                        Intent chooserIntent =
                                Intent.createChooser((targetedShareIntents.remove(targetedShareIntents.size() - 1)),
                                        "Share on ..");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                targetedShareIntents.toArray(new Parcelable[]{}));
                        mContext.startActivity(chooserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable setBGDrawable(float rating) {
        Drawable myDrawable;
        if (rating <= 1.9) {
            myDrawable = mContext.getResources().getDrawable(R.drawable.rating_rad_style);
        } else if (rating >= 2.0 && rating < 3.0) {
            myDrawable = mContext.getResources().getDrawable(R.drawable.rating_yellow_style);
        } else {
            myDrawable = mContext.getResources().getDrawable(R.drawable.rating_green_style);
        }
        return myDrawable;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
