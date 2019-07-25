package com.example.cube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MyPageActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_mypage,container,false);

        //viewpager
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewPager);

        //프래그먼트 배열 - 만들어 놓은 프래그먼트를 차례대로 넣어 준다.
        Fragment[] arrFragments = new Fragment[2];
        arrFragments[0] = new MyInformation();
        arrFragments[1] = new OrderedList();

        //어답터 생성후 연결 - 배열을 인자로 추가해 준다.
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(),arrFragments));
        return view;
    }

    //pager adapter class(빨간줄에서 Alt+Enter키로 '구현')
    private class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] arrFragments;

        //생성자
        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments = arrFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }

    }
}
