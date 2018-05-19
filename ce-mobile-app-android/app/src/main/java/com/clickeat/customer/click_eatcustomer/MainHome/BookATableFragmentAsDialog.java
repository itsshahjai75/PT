package com.clickeat.customer.click_eatcustomer.MainHome;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.BookTableInfo.RestaurantDetailsFragment;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by pivotech on 9/10/17.
 */

public class BookATableFragmentAsDialog extends DialogFragment implements View.OnClickListener,
        NumberPicker.OnValueChangeListener, TimePickerDialog.OnTimeSetListener {

    TextView txtCuisine;
    EditText edtBookingDate, edtBookingTime, edtBookingAddress, edtTotalPeople;
    Button btnBookATable;
    String addressName;
    RelativeLayout layoutCanel, layoutOk;

    private View m_myFragmentView;
    private ArrayList<String> checkedList = new ArrayList<>();
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    static Dialog d;
    AppLocationService appLocationService;
    final private int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;

    private static final String ARG_PARAM = "param1";
    private static final String ARG_PARAM_TIME = "time";
    private static final String ARG_PARAM_PEOPLE = "people";
    private static final String ARG_PARAM_DATE = "date";
    private static final String ARG_PARAM_ADDRESS = "address";
    private static final String ARG_PARAM_CUISINES = "cuisines";
    private static final String ARG_PARAM_LATITUDE = "latitude";
    private static final String ARG_PARAM_LONGITUDE = "longitude";
    private String time, people, dateStr, address, cuisineResource;
    private Double latitude, longitude;
    private Boolean param1 = false;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 111;

    public static BookATableFragmentAsDialog newInstance(Boolean dialog, String time, String people, String dateStr, String address, ArrayList<String> checkedList, Double latitude, Double longitude) {
        BookATableFragmentAsDialog fragment = new BookATableFragmentAsDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM, dialog);
        args.putString(ARG_PARAM_TIME, time);
        args.putString(ARG_PARAM_PEOPLE, people);
        args.putString(ARG_PARAM_DATE, dateStr);
        args.putString(ARG_PARAM_ADDRESS, address);
        args.putStringArrayList(ARG_PARAM_CUISINES, checkedList);
        args.putDouble(ARG_PARAM_LATITUDE, latitude);
        args.putDouble(ARG_PARAM_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT; // specify a value here
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT; // specify a value here
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getBoolean(ARG_PARAM);
            address = getArguments().getString(ARG_PARAM_ADDRESS);
            dateStr = getArguments().getString(ARG_PARAM_DATE);
            time = getArguments().getString(ARG_PARAM_TIME);
            people = getArguments().getString(ARG_PARAM_PEOPLE);
            checkedList = getArguments().getStringArrayList(ARG_PARAM_CUISINES);
            latitude = getArguments().getDouble(ARG_PARAM_LATITUDE);
            longitude = getArguments().getDouble(ARG_PARAM_LONGITUDE);
            Log.d("<>dialog-", " adress ==> " + address + " date ==> " + dateStr + "time ==> " + time + " ==>people" + people +
                    "===> checked list == " + checkedList.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        m_myFragmentView = inflater.inflate(R.layout.layout_dialog_book_a_table, container, false);
        findIds();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        Calendar now = Calendar.getInstance();
        Log.d("<>log-", " time======> " + now.getTime());
        int unroundedMinutes = now.get(Calendar.MINUTE);
        Log.d("<>log-", " unroundedMinutes======> " + unroundedMinutes + "");
        int mod = unroundedMinutes % 30;
        Log.d("<>log-", " mod======> " + mod + "");
        now.add(Calendar.MINUTE, mod < 0 ? -mod : (30 - mod));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Log.d("<>cancel-", " cuisine count size ==> " + MyApplication.getGlobalData().getCuisineList() + "");
        cuisineResource = checkedList.size() == 1 ? getString(R.string.one_cuisine) : getString(R.string.pural_selected_cuisines);
        Integer count = 0;
        if (MyApplication.getGlobalData().getCuisineCount() != null)
            count = MyApplication.getGlobalData().getCuisineCount();
        if (count == checkedList.size())
            txtCuisine.setText(getResources().getString(R.string.all_selected_cuisines));
        else
            txtCuisine.setText(checkedList.size() + " " + cuisineResource);

        String time1 = sdf.format(now.getTime());
        edtBookingTime.setText(time1);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        edtBookingDate.setText(sdf1.format(new Date()));
        edtTotalPeople.setText("2");
        edtBookingDate.setOnClickListener(this);
        edtBookingTime.setOnClickListener(this);
        edtTotalPeople.setOnClickListener(this);
        edtBookingAddress.setOnClickListener(this);
        btnBookATable.setOnClickListener(this);
        layoutOk.setOnClickListener(this);
        layoutCanel.setOnClickListener(this);

        edtBookingAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtBookingAddress.getRight() -
                            edtBookingAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (isNetworkConnected()) {
                            if (checkLocationPermission())
                                getLocations();
                            else
                                showSettingsAlert();
                        } else {
                            Toast.makeText(getActivity(), "Please enable your network", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        if (getArguments() != null) {
            edtBookingDate.setText(dateStr);
            edtTotalPeople.setText(people);
            edtBookingAddress.setText(address);
            edtBookingTime.setText(time);
            addressName = address;
        }

        return m_myFragmentView;
    }

    private void findIds() {
        txtCuisine = m_myFragmentView.findViewById(R.id.txtCuisine);
        edtBookingDate = m_myFragmentView.findViewById(R.id.edtBookingDate);
        edtBookingTime = m_myFragmentView.findViewById(R.id.edtBookingTime);
        edtBookingAddress = m_myFragmentView.findViewById(R.id.edtRestaurantAddress);
        edtTotalPeople = m_myFragmentView.findViewById(R.id.edtTotalPeople);
        btnBookATable = m_myFragmentView.findViewById(R.id.btnBookATable);
        layoutCanel = m_myFragmentView.findViewById(R.id.relativeLayoutCancel);
        layoutOk = m_myFragmentView.findViewById(R.id.relativeLayoutOk);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void getLocations() {
        // check if GPS enabled
        appLocationService = new AppLocationService(getActivity());
        if (appLocationService.canGetLocation()) {
            Log.d("<>log-", " in get location");
            double latitude = appLocationService.getLatitude();
            double longitude = appLocationService.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getActivity(), new GeocoderHandler());
            // \n is for new line
//            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            Log.d("<>log-", " in else get location");
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            showSettingsAlert();
        }
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        edtBookingDate.setText(sdf.format(myCalendar.getTime()));
        Log.d("<>log-", "today's date ==> " + new Date() + "");
        if (myCalendar.getTime().before(new Date())) {
            Log.d("<>log-", " it is tomorrows date");
            edtBookingDate.setText(sdf.format(new Date()));
        }
        if (myCalendar.getTime().after(new Date())) {
            Log.d("<>log-", " it is tomorrows date");
            edtBookingTime.setText("00:00");
        } else {
            Calendar now = Calendar.getInstance();
//        now.add(Calendar.MINUTE, 30);
            Log.d("<>log-", " time======> " + now.getTime());
            int unroundedMinutes = now.get(Calendar.MINUTE);
            Log.d("<>log-", " unroundedMinutes======> " + unroundedMinutes + "");
            int mod = unroundedMinutes % 30;
            Log.d("<>log-", " mod======> " + mod + "");
            now.add(Calendar.MINUTE, mod < 0 ? -mod : (30 - mod));
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
            String time = sdf1.format(now.getTime());
            edtBookingTime.setText(time);
        }
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("<>map-", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

//            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            edtBookingAddress.setText(Html.fromHtml(place.getAddress() + ""));
            LatLng attributionLatLang = places.get(0).getLatLng();
            Log.d("<>map-", places.get(0).getAddress() + "  ===> " +
                    places.get(0).getName());
            latitude = attributionLatLang.latitude;
            longitude = attributionLatLang.longitude;
            addressName = places.get(0).getName().toString();
           /* mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }*/
        }
    };

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    break;
                default:
                    locationAddress = null;
            }
            edtBookingAddress.setText(locationAddress);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // some code
        txtCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CuisineList.class);
                intent.putStringArrayListExtra("data", checkedList);
                startActivityForResult(intent, 1);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        Log.d("<>onAct-", " on Activity returnedData id ===> ");
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                final int carId = data.getIntExtra("result", -1);
//                Log.d("<>onAct-", " on Activity card id ===> " + carId + "");
//                txtCuisine.setText("  " + carId + " selected ");
                checkedList = data.getStringArrayListExtra("data");
                cuisineResource = checkedList.size() == 1 ? getString(R.string.one_cuisine) : getString(R.string.pural_selected_cuisines);
                if (MyApplication.getGlobalData().getCuisineCount() == checkedList.size())
                    txtCuisine.setText(getResources().getString(R.string.all_selected_cuisines));
                else if (checkedList.size() == 0)
                    txtCuisine.setText(getResources().getString(R.string.all_selected_cuisines));
                else
                    txtCuisine.setText(checkedList.size() + " " + cuisineResource);
                Log.d("<>resta-", " on Activity returnedData id ===> " + checkedList.toString() + "");
                MyApplication.getGlobalData().addCuisineList(checkedList);
            } else {
                // You can handle a case where no selection was made if you want
            }
        } else if (requestCode == REQUEST_CODE_ASK_PERMISSIONS_LOCATION) {
            insertLocationWrapper();
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i("<>intentMap-", "Place: " + place.getName());
                StringBuilder sb = new StringBuilder();
                sb.append(place.getAddress());
                LatLng latLng = place.getLatLng();
                addressName = place.getName().toString();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                edtBookingAddress.setText(sb.toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("<>intentMap-", status.getStatusMessage());

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtRestaurantAddress:
                try {
//                    Filter for restricted country
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(Place.TYPE_COUNTRY)
                            .setCountry("UK")
//                            .setCountry("IN")
                            .build();

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(autocompleteFilter)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.edtBookingDate:
                showDateDialog();
                break;
            case R.id.edtBookingTime:
//                onCreateDialog();
//                showTimeDialog();
                showTimePicker(edtBookingTime.getText().toString());
                break;
            case R.id.edtTotalPeople:
                showNumberPicker();
                break;
            case R.id.action_book_table:
                Toast.makeText(getActivity(), "this will show action on book a table", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnBookATable:
                /*String time = edtBookingTime.getText().toString();
                String people = edtTotalPeople.getText().toString();
                String date = edtBookingDate.getText().toString();
                if (edtBookingAddress.getText().length() <= 0) {
                    Toast.makeText(getActivity(), "Choose place first !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (param1)
                    dismiss();
                Fragment attachFragment = RestaurantDetailsFragment.newInstance(time, people, date, longitude, latitude, addressName);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
                break;
            case R.id.relativeLayoutOk:
                String time = edtBookingTime.getText().toString();
                String people = edtTotalPeople.getText().toString();
                String date = edtBookingDate.getText().toString();
                if (edtBookingAddress.getText().length() <= 0) {
                    Toast.makeText(getActivity(), "Choose place first !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (param1)
                    dismiss();
                Fragment attachFragment = RestaurantDetailsFragment.newInstance(time, people, date, longitude, latitude, addressName);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.PreLoginFrame, attachFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.relativeLayoutCancel:
                dismiss();
                break;
        }
    }

    private void showDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                getActivity());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void showTimeDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtBookingTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute * 30, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void onCreateDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // Convert current minutes to tens
        // 55 = 50, 56 = 00
        int minute = c.get(Calendar.MINUTE) / 30;
        Log.d("<>log-", " minute 1 ===> " + minute + "");
        minute = (minute > 5) ? 0 : minute;
        Log.d("<>log-", " minute 2 ===> " + minute + "");
        // Create a new instance of TimePickerDialog and return it
        final TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth
                , this, hour, minute * 30,
                DateFormat.is24HourFormat(getActivity()));

        tpd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int tpLayoutId = getResources().getIdentifier("timePickerLayout", "id", "android");

                ViewGroup tpLayout = (ViewGroup) tpd.findViewById(tpLayoutId);
                ViewGroup layout = (ViewGroup) tpLayout.getChildAt(0);

                // Customize minute NumberPicker
                NumberPicker minutePicker = (NumberPicker) layout.getChildAt(2);
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(1);
                minutePicker.setDisplayedValues(new String[]{"00", "30"});
            }
        });
        tpd.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        minute = minute * 30;
        edtBookingTime.setText(hourOfDay + ":" + minute);
        Toast.makeText(getActivity(), " Selected hour " + hourOfDay + " : Selected minute: " + minute, Toast.LENGTH_LONG).show();
    }

    private void showNumberPicker() {
        final Dialog d = new Dialog(getActivity());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.layout_number_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        d.getWindow().setAttributes(lp);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(np, getResources().getColor(R.color.colorPrimary));
        setDividerColor(np, getResources().getColor(R.color.colorPrimary));

        np.setMaxValue(20);
        np.setMinValue(1);
        if (edtTotalPeople.getText().length() > 0)
            np.setValue(Integer.parseInt(edtTotalPeople.getText().toString()));
        else
            np.setValue(2);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTotalPeople.setText(String.valueOf(np.getValue()));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    /* Show time picker dialog click on Time Edittext */
    private void showTimePicker(String currentSlot) {
        final Dialog d = new Dialog(getActivity());
        d.setTitle("Time Picker");
        d.setContentView(R.layout.layout_time_selection_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        d.getWindow().setAttributes(lp);
        final String[] finalStockArr;
        final NumberPicker min = (NumberPicker) d.findViewById(R.id.numberpickerMin);
        final String[] values = getResources().getStringArray(R.array.items_time_dialog_selection);
        List<String> list = new ArrayList<>();
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        try {
            Date newdate = sdf.parse(edtBookingDate.getText().toString());

            Log.d("<>log-", "today's date ==> " + new Date() + "");
            if (newdate.after(new Date())) {

                //Specify the maximum value/number of NumberPicker
                min.setMinValue(0);
                min.setMaxValue(values.length - 1); //to array last value
                Log.d("<>time-", " modified list ==> " + list.toString());
                //Specify the NumberPicker data source as array elements
                min.setDisplayedValues(values);
                for (int i = 0; i < values.length; i++) {
                    if (currentSlot.equals(values[i]))
                        min.setValue(i); //from array first value
                }

                finalStockArr = values;
            } else {
                for (int time = 0; time < values.length; time++) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    try {
                        Date EndTime = dateFormat.parse(values[time].toString());
                        Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));
                        if (!EndTime.before(CurrentTime)) {
                            list.add(values[time]);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String[] stockArr = new String[list.size()];
                stockArr = list.toArray(stockArr);
                min.setMinValue(0);
                //Specify the maximum value/number of NumberPicker
                min.setMaxValue(stockArr.length - 1); //to array last value
                Log.d("<>time-", " modified list ==> " + list.toString());
                //Specify the NumberPicker data source as array elements
                min.setDisplayedValues(stockArr);
                for (int i = 0; i < list.size(); i++) {
                    if (currentSlot.equals(list.get(i))) {
                        min.setValue(i); //from array first value
                        Log.d("<>log-", " ========== eqal" + i + "   " + list.get(i));
                    }
                }
                finalStockArr = stockArr;
            }

            min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            setDividerColor(min, getResources().getColor(R.color.colorPrimary));
            setNumberPickerTextColor(min, getResources().getColor(R.color.colorPrimary));

            //Gets whether the selector wheel wraps when reaching the min/max value.
            min.setWrapSelectorWheel(true);
            final Button btnCancel = (Button) d.findViewById(R.id.btnDialogTimeCancel);
            final Button btnOk = (Button) d.findViewById(R.id.btnDialogTimeOk);
            final Spinner spinnerTime = (Spinner) d.findViewById(R.id.dialogSpinnerTime);
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.items_time_dialog_selection, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTime.setAdapter(adapter);
            final String compareValue = edtBookingTime.getText().toString();
            if (!compareValue.equals(null)) {
                int spinnerPosition = adapter.getPosition(compareValue);
                spinnerTime.setSelection(spinnerPosition);
            }
            spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!edtBookingTime.getText().toString().equals("00:00")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        try {
                            Date EndTime = dateFormat.parse(String.valueOf(spinnerTime.getSelectedItem().toString()));
                            Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));
                            if (EndTime.before(CurrentTime)) {
                                Log.d("<>time-", " you can't select past time");
                                Toast.makeText(getActivity(), "You can't select past time", Toast.LENGTH_SHORT).show();
                                if (!compareValue.equals(null)) {
                                    int spinnerPosition = adapter.getPosition(compareValue);
                                    spinnerTime.setSelection(spinnerPosition);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtBookingTime.setText(finalStockArr[min.getValue()]);
                    d.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    // Ask for permission (START DIALOG) in Android APIs >= 23.
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Prompt the user once explanation has been shown.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
            }
            Log.d("<>log-", " in if");
            return false;
        } else {
            Log.d("<>log-", " in else");
            return true;
        }
    }

    /* Ask for permission (START DIALOG) in Android APIs >= 23. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void insertLocationWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int hasWriteLocationsPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteLocationsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                Log.d("<>log-", " insertLocationWrapper");
                return;
            }
        }
        getLocations();
    }
}
