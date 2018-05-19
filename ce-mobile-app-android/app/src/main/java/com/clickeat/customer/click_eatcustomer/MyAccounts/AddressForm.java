package com.clickeat.customer.click_eatcustomer.MyAccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.clickeat.customer.click_eatcustomer.DataModel.OAddressList;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.List;

public class AddressForm extends AppCompatActivity {
    private EditText edtAddressType;
    private EditText edtAddressText;
    private Button btnAddressSave;
    private String addressId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        findIds();

        Intent extras = getIntent();
        addressId = extras.getStringExtra("addressId");

        init();

    }

    private void findIds() {
        edtAddressType = findViewById(R.id.edtAddressType);
        edtAddressText = findViewById(R.id.edtAddressText);
        btnAddressSave = findViewById(R.id.btnAddressSave);
    }

    private void init() {
        if (addressId != null) {
            getSupportActionBar().setTitle("Edit");
            List<OAddressList> list = MyApplication.getGlobalData().getAllAddressList();
            if (list.size() > 0) {
                for (int ad = 0; ad < list.size(); ad++) {
                    if (list.get(ad).getAddressId().equals(addressId)) {
                        edtAddressText.setText(list.get(ad).getAddressText());
                        edtAddressType.setText(list.get(ad).getAddressType());
                    }
                }
            }
        } else {
            getSupportActionBar().setTitle("New");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}
