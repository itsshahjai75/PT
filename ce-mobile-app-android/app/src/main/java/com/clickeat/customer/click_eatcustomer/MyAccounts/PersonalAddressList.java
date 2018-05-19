package com.clickeat.customer.click_eatcustomer.MyAccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.clickeat.customer.click_eatcustomer.DataModel.OAddressList;
import com.clickeat.customer.click_eatcustomer.MyApplication;
import com.clickeat.customer.click_eatcustomer.R;

import java.util.ArrayList;
import java.util.List;

public class PersonalAddressList extends AppCompatActivity {
    private RecyclerView recycler_view_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Address");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalAddressList.this, AddressForm.class);
                intent.putExtra("addressId", "");
                startActivity(intent);
            }
        });

        findIds();

        List<OAddressList> addressLists = new ArrayList<>();
        addressLists.add(new OAddressList("1", "Home Address", " Edinburgh Building, City Campus, Chester Road,\n" +
                " Sunderland\n" +
                " SR1 3SD,\n" +
                " United Kingdom"));

        addressLists.add(new OAddressList("2", "Work Address", " Edinburgh Building, City Campus, Chester Road,\n" +
                " Sunderland\n" +
                " SR1 3SD,\n" +
                " United Kingdom"));
        MyApplication.getGlobalData().addAddressInList(addressLists);

        AccountAddressAdapter addressAdapter = new AccountAddressAdapter(PersonalAddressList.this, addressLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PersonalAddressList.this);
        recycler_view_address.setLayoutManager(mLayoutManager);
        recycler_view_address.setAdapter(addressAdapter);
    }

    private void findIds() {
        recycler_view_address = findViewById(R.id.recycler_view_address);
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
