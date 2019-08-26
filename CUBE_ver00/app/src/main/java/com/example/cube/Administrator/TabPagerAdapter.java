package com.example.cube.Administrator;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.cube.Administrator.Order.OrderListFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public TabPagerAdapter(FragmentManager FM, int tabCount){
        super(FM);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: {
                OrderListFragment olf = new OrderListFragment();
                return olf;
            }
            case 1: {
                ConfigFragment cf = new ConfigFragment();
                return cf;
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
