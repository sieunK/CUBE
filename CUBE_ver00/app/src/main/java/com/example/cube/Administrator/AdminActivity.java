package com.example.cube.Administrator;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.cube.BackPressCloseHandler;
import com.example.cube.R;
import com.google.android.material.tabs.TabLayout;


public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BackPressCloseHandler backPressCloseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        backPressCloseHandler = new BackPressCloseHandler(this);

        tabLayout = (TabLayout)findViewById(R.id.main_tabs);
        viewPager = (ViewPager)findViewById(R.id.main_pager);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(
                getSupportFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
