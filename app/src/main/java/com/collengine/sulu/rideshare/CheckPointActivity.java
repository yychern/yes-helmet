package com.collengine.sulu.rideshare;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.collengine.sulu.rideshare.Helper.ViewPagerAdapter;
import com.collengine.sulu.rideshare.fragments.ConnectFragment;
import com.collengine.sulu.rideshare.fragments.CurrentFragment;
import com.collengine.sulu.rideshare.fragments.MyWalletFragment;
import com.collengine.sulu.rideshare.fragments.RedeemFragment;

public class CheckPointActivity extends AppCompatActivity {
    private static ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_point);

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary_blue));
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);//Set up View Pager

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorTitle));
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager
    }


    //Setting View Pager
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MyWalletFragment(), "My Wallet");
        adapter.addFrag(new RedeemFragment(), "Redeem");


        viewPager.setAdapter(adapter);
    }


    //Return current fragment on basis of Position
    public Fragment getFragment(int pos) {

        return adapter.getItem(pos);
    }

}
