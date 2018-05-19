package com.nexus.locum.locumnexus;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.nexus.locum.locumnexus.customviews.UserTextView;
import com.nexus.locum.locumnexus.fragments.AddressPersonalProfile;
import com.nexus.locum.locumnexus.fragments.DocumentsProfessionalProfile;
import com.nexus.locum.locumnexus.fragments.MainPersonalProfile;
import com.nexus.locum.locumnexus.fragments.NHSsystemProfessionFragment;
import com.nexus.locum.locumnexus.fragments.PracticesProfessionalProfile;
import com.nexus.locum.locumnexus.utilities.Const;

import java.util.ArrayList;
import java.util.List;

import static com.nexus.locum.locumnexus.utilities.Const.MyPREFERENCES;

public class ProfessionalProfileActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    public static ViewPager viewPager;
    UserTextView tabMain,tabAddress;
    SharedPreferences CONST_SHAREDPREFERENCES;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Professional");

        CONST_SHAREDPREFERENCES  = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        viewPager = (ViewPager) findViewById(R.id.viewpagerProfessionalProfile);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabsProfessionalProfile);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).select();

        tabMain = (UserTextView) LayoutInflater.from(this).inflate(R.layout.onlytextview, null);
        tabMain.setText("Practices");
        tabMain.setGravity(Gravity.CENTER);
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.analytics, 0, 0);
        tabMain.setTextColor(getResources().getColor(R.color.colorPrimary,getTheme()));
        tabLayout.getTabAt(0).setCustomView(tabMain);

        tabAddress = (UserTextView) LayoutInflater.from(this).inflate(R.layout.onlytextview, null);
        tabAddress.setText("Documents");
        tabAddress.setGravity(Gravity.CENTER);
        tabLayout.getTabAt(1).setCustomView(tabAddress);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    if (tab.getPosition() == 0) {
                        tabMain.setTextColor(getResources().getColor(R.color.colorPrimary,getTheme()));
                        tabAddress.setTextColor(getResources().getColor(R.color.md_grey_500,getTheme()));
                    } else if (tab.getPosition() == 1) {
                        tabMain.setTextColor(getResources().getColor(R.color.md_grey_500,getTheme()));
                        tabAddress.setTextColor(getResources().getColor(R.color.colorPrimary,getTheme()));
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PracticesProfessionalProfile(), "Practices");
        adapter.addFrag(new DocumentsProfessionalProfile(), "Documents");
        adapter.addFrag(new NHSsystemProfessionFragment(), "NHS System");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("<REstarted ACtivity>","Activity REstaredted");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("<REsumed Activity>","Activity Resumed");
        viewPager.getAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
