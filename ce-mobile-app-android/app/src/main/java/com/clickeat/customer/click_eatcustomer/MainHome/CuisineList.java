package com.clickeat.customer.click_eatcustomer.MainHome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.clickeat.customer.click_eatcustomer.ApiInterface;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;
import com.clickeat.customer.click_eatcustomer.Utils.APIConstants;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CuisineList extends Activity implements CuisineData {
    ListView listCuisineType;
    Button btnSelectAll;
    Button btnUnSelectAll;
    Button btnCuisineOk;
    Button btnCuisineCancel;

    private ArrayAdapter<String> adapter;
    private String mCurfilter = null;
    private ProgressDialog loadingSpinner;
    ArrayList<String> CuisineTypeList;
    ArrayList<String> checkedList = new ArrayList<>();
    ArrayList<String> updatedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_list);
        findIds();
        final FiftyShadesOf fiftyShadesOf = FiftyShadesOf.with(CuisineList.this)
                .on(R.id.listCuisineType)
                .fadein(true)
                .start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fiftyShadesOf.stop();
            }
        }, 1000);
        updatedList = getIntent().getStringArrayListExtra("data");
        Log.d("<>cancel-", " getting the list from intent == >" + updatedList.toString());
        CuisineTypeList = new ArrayList<>();
        getCuisineList();

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < CuisineTypeList.size(); i++) {
                    listCuisineType.setItemChecked(i, true);
                    if (!updatedList.contains(CuisineTypeList.get(i).toString()))
                        updatedList.add(CuisineTypeList.get(i).toString());
                }
            }
        });

        btnUnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < CuisineTypeList.size(); j++) {
                    listCuisineType.setItemChecked(j, false);
                    if (updatedList.contains(CuisineTypeList.get(j).toString()))
                        updatedList.remove(CuisineTypeList.get(j).toString());
                }
            }
        });

        btnCuisineOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                Integer totalCount = listCuisineType.getCheckedItemCount();
                /*if (totalCount == 0) {
                    Toast.makeText(CuisineList.this, getString(R.string.title_cuisine_select_error), Toast.LENGTH_SHORT).show();
                } else {*/
                returnIntent.putStringArrayListExtra("data", updatedList);
                Log.d("<>search-", " updated data ==> " + updatedList.toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
//                }
            }
        });

        btnCuisineCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void findIds() {
        listCuisineType = findViewById(R.id.listCuisineType);
        btnSelectAll = findViewById(R.id.btnSelectAllCuisine);
        btnUnSelectAll = findViewById(R.id.btnUnSelectAllCuisine);
        btnCuisineOk = findViewById(R.id.btnCuisineOk);
        btnCuisineCancel = findViewById(R.id.btnCuisineCancel);
    }

    public void getCuisineList() {
        showSpinner();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        Call<JsonElement> mService = mInterfaceService.getCuisineList(MyApplication.sDefSystemLanguage);
        Log.d("<>cuisine-", " URL is  ==> " + mService.request().url().toString());
        Log.d("<>cuisine-", " Headers is  ==> " + mService.request().headers().toString());

        mService.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d("<>cuisine-", " response msg ===> " + response.message());
                Log.d("<>cuisine-", " response code ===> " + response.code() + "");
                if (response.isSuccessful()) {
                    if (loadingSpinner != null)
                        dismissSpinner();
                    Log.d("<>cuisine-", " result from server " + response.body().toString());
                    Log.d("<>cuisine-", " result from server " + response.body().getAsJsonArray());
                    JsonArray listData = response.body().getAsJsonArray();
                    Log.d("<>cuisine-", "list data of json ===> " + listData.size() + "");
                    CuisineTypeList = new ArrayList<String>();
                    for (int i = 0; i < listData.size(); i++) {
                        String json = String.valueOf(listData.get(i).getAsJsonObject().getAsJsonObject(MyApplication.sDefSystemLanguage).get("cuisineType").getAsString());
                        Log.d("<>cuisine-", "json ===> " + json.toString());
                        CuisineTypeList.add(json);
                    }
                    init(mCurfilter);
                } else {
                    call.cancel();
                    if (loadingSpinner != null)
                        dismissSpinner();
//                        Toast.makeText(getActivity(), R.string.title_something_wrong, Toast.LENGTH_LONG).show();
                    Toast.makeText(CuisineList.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
//                    Toast.makeText(CuisineList.this, R.string.title_something_wrong, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
                if (loadingSpinner != null)
                    dismissSpinner();
                Toast.makeText(CuisineList.this, R.string.check_server_connection, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void init(String filter) {
        if (mCurfilter == null) {
            Collections.sort(CuisineTypeList, String.CASE_INSENSITIVE_ORDER);
            adapter = new ArrayAdapter<String>(this,
                    R.layout.layout_checkbox_spinner_cuisine, CuisineTypeList);
            listCuisineType.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listCuisineType.setAdapter(adapter);

            if (updatedList != null) {
                if (updatedList.size() == 0) {
                    Log.d("<>chek-", " checked already item --> " + updatedList.size() + "");
                   /* for (int i = 0; i < CuisineTypeList.size(); i++) {
                        listCuisineType.setItemChecked(i, true);
                        updatedList.add(listCuisineType.getItemAtPosition(i).toString());
                    }*/
                } else {
                    Log.d("<>chek-", " in else  --> " + updatedList.size() + "");
                    for (int item = 0; item < updatedList.size(); item++) {
                        listCuisineType.setItemChecked(CuisineTypeList.indexOf(updatedList.get(item)), true);
                    }
                }
            }
        }

        listCuisineType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("<>tag-", " list of item ==> " + listCuisineType.getItemAtPosition(position).toString() + "");
                if (!updatedList.contains(listCuisineType.getItemAtPosition(position).toString())) {
                    Log.d("<>tag-", "equal1");
                    updatedList.add(listCuisineType.getItemAtPosition(position).toString());
                } else if (updatedList.contains(listCuisineType.getItemAtPosition(position).toString())) {
                    Log.d("<>tag-", "in else part of item click2");
                    updatedList.remove(listCuisineType.getItemAtPosition(position).toString());
                } else {
                    Log.d("<>tag-", "in else 3");
                    updatedList.add(listCuisineType.getItemAtPosition(position).toString());
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("data", updatedList);
        Log.d("<>search-", " updated data ==> " + updatedList.toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
//
    }

    @Override
    public void data(ArrayList<String> checkedList) {
        this.checkedList = checkedList;
    }

    private void showSpinner() {
        if (loadingSpinner == null) {
            this.loadingSpinner = new ProgressDialog(CuisineList.this, R.style.AppCompatAlertDialogStyle);
        }
//        loadingSpinner.setTitle("Loading ...");
        loadingSpinner.setMessage(getResources().getString(R.string.lbl_loading));
        loadingSpinner.setCancelable(false);
        loadingSpinner.show();
    }

    private void dismissSpinner() {
        if (loadingSpinner != null) {
            loadingSpinner.dismiss();
        }
    }
}
