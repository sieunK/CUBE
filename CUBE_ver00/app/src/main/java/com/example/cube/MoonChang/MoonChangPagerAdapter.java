package com.example.cube.MoonChang;

import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//문창회관기능에 필요한 4가지 탭을 위한 탭어댑터입니다
public class MoonChangPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs; //tab의 갯수
    public MoonChangPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MoonChangPayFragment tab1 = new MoonChangPayFragment();
                return tab1;
            case 1:
                MoonChangMenuFragment tab2 = new MoonChangMenuFragment();
                return tab2;
            case 2:
                MoonChangReviewFragment tab3 = new MoonChangReviewFragment();
                return tab3;
            case 3:
                MoonChangInfoFragment tab4=new MoonChangInfoFragment();
                return tab4;

            default:
                return null;
        }
        //return null;
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return "";
    }
}