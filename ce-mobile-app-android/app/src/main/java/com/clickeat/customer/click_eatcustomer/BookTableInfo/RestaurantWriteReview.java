package com.clickeat.customer.click_eatcustomer.BookTableInfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.DataModel.OUser;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.PreLoginMainActivity;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestaurantWriteReview extends Fragment implements MaterialRatingBar.OnRatingChangeListener,
        View.OnClickListener {
    private View m_myFragmentView;
    private String restaurantName, restaurantId, noReviews;
    private String userChoosenTask;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static int SELECT_FILE = 111;
    private static int REQUEST_CAMERA = 222;
    private Button btnSelectPhoto;
    private LinearLayout layoutRVImageAdd;
    private static final String ARG_PARAM_Name = "name";
    private static final String ARG_PARAM_RESTAURANT_ID = "restaurantId";
    private static final String ARG_PARAM_NO_REVIEWS = "noReviews";
    private MaterialRatingBar ratingOverall, ratingLocation, ratingFood, ratingStaff, ratingService, ratingFacilities;
    private EditText edtRvSuggestionReview, edtRvPositive;
    private float rateOverall, rateLocation, rateFood, rateStaff, rateService, rateFacility;
    private Button btnRvReset, btnRvSubmit;
    private ProgressDialog loadingSpinner;
    private List<String> imagesPathList;
    Uri imageUri = null;
    private Uri mImageCaptureUri;

    public static RestaurantWriteReview newInstance(String restaurantName, String restaurantId, String noReviews) {
        RestaurantWriteReview fragment = new RestaurantWriteReview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_Name, restaurantName);
        args.putString(ARG_PARAM_RESTAURANT_ID, restaurantId);
        args.putString(ARG_PARAM_NO_REVIEWS, noReviews);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantName = getArguments().getString(ARG_PARAM_Name);
            restaurantId = getArguments().getString(ARG_PARAM_RESTAURANT_ID);
            noReviews = getArguments().getString(ARG_PARAM_NO_REVIEWS);
            Log.d("<>rev-", " args restaurant name --> " + restaurantName + " === id " + restaurantId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final PreLoginMainActivity activity = (PreLoginMainActivity) getActivity();
        activity.setToolbarTitle(restaurantName + " Write Review");
        m_myFragmentView = inflater.inflate(R.layout.activity_restaurant_write_review, container, false);
        findIds();

        init();
        imagesPathList = new ArrayList<>();
        ratingOverall.setOnRatingChangeListener(this);
        ratingFacilities.setOnRatingChangeListener(this);
        ratingFood.setOnRatingChangeListener(this);
        ratingLocation.setOnRatingChangeListener(this);
        ratingService.setOnRatingChangeListener(this);
        ratingStaff.setOnRatingChangeListener(this);
        btnRvReset.setOnClickListener(this);
        btnRvSubmit.setOnClickListener(this);
        return m_myFragmentView;
    }


    private void findIds() {
        btnSelectPhoto = m_myFragmentView.findViewById(R.id.btnSelectPhoto);
        layoutRVImageAdd = m_myFragmentView.findViewById(R.id.layoutRVImageAdd);
        ratingOverall = m_myFragmentView.findViewById(R.id.ratingOverall);
        ratingLocation = m_myFragmentView.findViewById(R.id.ratingLocation);
        ratingFood = m_myFragmentView.findViewById(R.id.ratingFood);
        ratingStaff = m_myFragmentView.findViewById(R.id.ratingStaff);
        ratingService = m_myFragmentView.findViewById(R.id.ratingService);
        ratingFacilities = m_myFragmentView.findViewById(R.id.ratingFacilities);
        edtRvSuggestionReview = m_myFragmentView.findViewById(R.id.edtRvSuggestionReview);
        edtRvPositive = m_myFragmentView.findViewById(R.id.edtRvPositive);
        btnRvReset = m_myFragmentView.findViewById(R.id.btnRvReset);
        btnRvSubmit = m_myFragmentView.findViewById(R.id.btnRvSubmit);
    }

    private void init() {
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        /*Intent intent = new Intent();
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);*/
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Log.d("<>result-", " File URI ==> " + data.getData());

               /* Uri originalUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
                Log.d("<>log-", " get activity called URI ==> "+originalUri+"");*/
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                Log.d("<>req-", " camera Uri==> " + data.getData());
                onCaptureImageResult(data);
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity(), contentURI, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data.getData() != null) {
            ClipData mClipData = data.getClipData();
            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                mArrayUri.add(uri);
            }
            Log.v("<>LOG_TAG", "Selected Images" + mArrayUri.size());
            if (mArrayUri.size() > 0) {
                for (int i = 0; i < mArrayUri.size(); i++) {
                    Bitmap bitmap;
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mArrayUri.get(i));
//                        imagesPathList.add(mArrayUri.get(i).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String type = getActivity().getContentResolver().getType(mArrayUri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    long lengthbmp = imageInByte.length;
                    Log.d("<>type-", " length of image --> " + lengthbmp + "");
                    String realPath = getRealPathFromURI(mArrayUri.get(i));
                    Log.d("<>result-", "real path from URI ==> " + realPath);
                    imagesPathList.add(realPath);
                    if (type.equals("image/jpeg") || type.equals("image/png") &&
                            lengthbmp > 2000000) {
                        ImageView img = new ImageView(getActivity());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_weight),
                                getResources().getDimensionPixelSize(R.dimen.linear_height));
                        img.setImageBitmap(bm);
                        img.setLayoutParams(layoutParams);
                        img.setPadding(15, 5, 15, 5);
                        layoutRVImageAdd.addView(img);
                    }
                }
            }

          /*  try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String type = getActivity().getContentResolver().getType(data.getData());
//            imagesPathList.add(data.getData().toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            long lengthbmp = imageInByte.length;
            Log.d("<>type-", " length of image --> " + lengthbmp + "");
            Log.d("<>type-", " path of image --> " + data.getData() + "");
            Uri filePath = data.getData();
            String realPath = getRealPathFromURI(filePath);
            Log.d("<>result-", "real path from URI ==> " + realPath);
            imagesPathList.add(realPath);
            if (type.equals("image/jpeg") || type.equals("image/png") &&
                    lengthbmp > 2000000) {
                ImageView img = new ImageView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_weight),
                        getResources().getDimensionPixelSize(R.dimen.linear_height));
                img.setImageBitmap(bm);
                img.setLayoutParams(layoutParams);
                img.setPadding(15, 5, 15, 5);
                layoutRVImageAdd.addView(img);
            } else {
                Toast.makeText(getActivity(), "Select suitable format", Toast.LENGTH_SHORT).show();
            }*/
        } else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                }
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                if (mArrayUri.size() > 0) {
                    for (int i = 0; i < mArrayUri.size(); i++) {
                        Bitmap bitmap;
                        try {
                            bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mArrayUri.get(i));
                            imagesPathList.add(mArrayUri.get(i).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String type = getActivity().getContentResolver().getType(mArrayUri.get(i));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageInByte = stream.toByteArray();
                        long lengthbmp = imageInByte.length;
                        Log.d("<>type-", " length of image --> " + lengthbmp + "");
                        if (type.equals("image/jpeg") || type.equals("image/png") &&
                                lengthbmp > 2000000) {
                            ImageView img = new ImageView(getActivity());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_weight),
                                    getResources().getDimensionPixelSize(R.dimen.linear_height));
                            img.setImageBitmap(bm);
                            img.setLayoutParams(layoutParams);
                            img.setPadding(15, 5, 15, 5);
                            layoutRVImageAdd.addView(img);
                        }
                    }
                }
            }
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        Log.d("<>req-", " in capture uri ==> " + data.getData());
        imageUri = data.getData();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("<>rev-", " capture path --> " + destination.getAbsolutePath() + "");
        Log.d("<>type-", " capture length --> " + destination.length());
        imagesPathList.add(destination.getAbsolutePath());
        ImageView img = new ImageView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.linear_weight),
                getResources().getDimensionPixelSize(R.dimen.linear_height));
        img.setImageBitmap(thumbnail);
        img.setPadding(15, 5, 15, 5);
        img.setLayoutParams(layoutParams);
        layoutRVImageAdd.addView(img);
    }

    @Override
    public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
        switch (ratingBar.getId()) {
            case R.id.ratingOverall:
                Log.d("<>cust-", "rating over all --> " + rating);
                this.rateOverall = rating;
                break;
            case R.id.ratingLocation:
                this.rateLocation = rating;
                break;
            case R.id.ratingFacilities:
                this.rateFacility = rating;
                break;
            case R.id.ratingFood:
                this.rateFood = rating;
                break;
            case R.id.ratingService:
                this.rateService = rating;
                break;
            case R.id.ratingStaff:
                this.rateStaff = rating;
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRvSubmit:
                submitReview();
                break;
            case R.id.btnRvReset:
                reviewSubmit();
                break;
        }
    }

    private void reviewSubmit() {
        ratingService.setRating(0.0f);
        ratingStaff.setRating(0.0f);
        ratingOverall.setRating(0.0f);
        ratingFood.setRating(0.0f);
        ratingFacilities.setRating(0.0f);
        ratingLocation.setRating(0.0f);
        edtRvPositive.setText("");
        edtRvSuggestionReview.setText("");
        if (layoutRVImageAdd.getChildCount() > 0)
            layoutRVImageAdd.removeAllViews();
    }

    private void submitReview() {
        List<OUser> userList = MyApplication.getGlobalData().getUserData();
        String customerName = "", userid = "";
        if (userList.size() > 0) {
            customerName = userList.get(0).getName();
            userid = userList.get(0).getId();
        }
        if (ratingOverall.getRating() == 0.0 || ratingLocation.getRating() == 0.0 ||
                ratingFood.getRating() == 0.0 || ratingFacilities.getRating() == 0.0 ||
                ratingStaff.getRating() == 0.0 || ratingService.getRating() == 0.0) {
            Toast.makeText(getActivity(), "Please give us Rating...", Toast.LENGTH_SHORT).show();
            return;
        }
//        MultipartBody.Part body = null;
//        if (imagesPathList.size() > 0) {
//            for (int i = 0; i < imagesPathList.size(); i++) {

//                body = MultipartBody.Part.createFormData("files", file.getName(), reqFile);
         /*   }
        } else {
            reqFile = RequestBody.create(MediaType.parse("image/*"), "");
//            body = MultipartBody.Part.createFormData("files","", reqFile);
        }*/
        MultipartBody.Part[] reviewImagesParts = new MultipartBody.Part[imagesPathList.size()];

        for (int index = 0; index < imagesPathList.size(); index++) {
            if (imagesPathList.get(index).toString() != null) {
                Log.d("<>log-", "image path list ==> " + imagesPathList.get(index).toString());
                File file = new File(imagesPathList.get(index).toString());
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
                reviewImagesParts[index] = MultipartBody.Part.createFormData("files", file.getName(), surveyBody);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String currentDateandTime = format.format(new Date());
        //pass it like this
        /*File file = new File(imagesPathList.get(0));
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);*/

       /* MultipartBody.Part body =
                MultipartBody.Part.createFormData("files", file.getName(), requestFile);*/
        RequestBody restaurant_Id = RequestBody.create(MediaType.parse("multipart/form-data"), restaurantId);
        RequestBody customer_Name = RequestBody.create(MediaType.parse("multipart/form-data"), customerName);
        RequestBody review_date = RequestBody.create(MediaType.parse("multipart/form-data"), currentDateandTime);
        RequestBody rat_ol = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingOverall.getRating()));
        RequestBody rat_faci = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingFacilities.getRating()));
        RequestBody rat_ser = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingService.getRating()));
        RequestBody rat_sta = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingStaff.getRating()));
        RequestBody rat_food = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingFood.getRating()));
        RequestBody rat_loc = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ratingLocation.getRating()));
        RequestBody positive = RequestBody.create(MediaType.parse("multipart/form-data"), edtRvPositive.getText().toString());
        RequestBody suggestion = RequestBody.create(MediaType.parse("multipart/form-data"), edtRvSuggestionReview.getText().toString());
       /* BodyReviewWrite bodyReviewWrite = new BodyReviewWrite(
                restaurantId, customerName, currentDateandTime,
               ratingOverall.getRating(),
        ratingLocation.getRating(),
         ratingFood.getRating(),
         ratingStaff.getRating(),
         ratingService.getRating(),
        ratingFacilities.getRating());*/
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        Log.d("<>log-", " restuarantid ==>" + restaurantId);
        Log.d("<>log-", " userid ==>" + userid);
        Call<JsonElement> mService = mInterfaceService.addReviewWithImages(restaurantId, userid, reviewImagesParts,
                restaurant_Id, customer_Name, review_date, rat_ol, rat_loc, rat_food, rat_sta, rat_ser, rat_faci,
                positive, suggestion);
        Log.d("<>login-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>login-", " Params is  ==> " + mService.request().toString() + "");
        Log.d("<>login-", " Body is  ==> " + mService.request().body().toString() + "");

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>log-", " response msg ===> " + response.message());
                Log.d("<>log-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Log.d("<>log-", " response body ===> " + response.body() + "");
                        if (loadingSpinner != null)
                            dismissSpinner();
//                        {"message":"Review added successfully"}
                        Toast.makeText(getActivity(), response.body().getAsJsonObject().get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();
                        FragmentActivity activity = (FragmentActivity) (getActivity());
                        Integer totalRev = Integer.parseInt(noReviews) + 1;
                        Fragment fragment = RestaurantReviews.newInstance(restaurantName, restaurantId, String.valueOf(totalRev));
                        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.PreLoginFrame, fragment);
                        fragmentTransaction.commit();
                    }
                } else if (response.code() == 401) {
                    Log.d("<>log-", " Unauthorized");
                    if (loadingSpinner != null)
                        dismissSpinner();
                } else if (response.code() == 500) {
                    Log.d("<>log-", " Internal Server");
                    if (loadingSpinner != null)
                        dismissSpinner();
                }
            }


            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(getActivity(), R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        }
        Activity activity = getActivity();
        if (activity != null) {
            loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
            loadingSpinner.setCancelable(false);
            loadingSpinner.show();
        }
    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }

}
