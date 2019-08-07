package com.example.cube;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MoonChangActivity extends AppCompatActivity implements MoonChangPayFragment.OnFragmentInteractionListener{

    private MoonChangBagFragment fragmentbag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonchang);

        fragmentbag=new MoonChangBagFragment();


        //TabLayout
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("결제"));
        tabs.addTab(tabs.newTab().setText("메뉴"));
        tabs.addTab(tabs.newTab().setText("리뷰"));
        tabs.addTab(tabs.newTab().setText("식당정보"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);

        //어답터설정
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final MoonChangPagerAdapter myPagerAdapter = new MoonChangPagerAdapter(getSupportFragmentManager(), 4);
        viewPager.setAdapter(myPagerAdapter);

/*
        //정보전달
        fragment_menu tab2 = new fragment_menu();


        Bundle bundle = new Bundle(1);
        bundle.putString("param1", "hello"); // Key, Value
        tab2.setArguments(bundle);
*/


        //탭메뉴를 클릭하면 해당 프래그먼트로 변경-싱크화
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));




    }

    /*
        public void onFragmentChanged(int index){

            fragment_bag fragmentBag= new fragment_bag();
            fragment_pay fragmentPay= new fragment_pay();
            if(index == 0){
                getSupportFragmentManager().beginTransaction().replace(R.id.parentView,fragmentBag).commit();
            } else if(index == 1){
                getSupportFragmentManager().beginTransaction().replace(R.id.parentView,fragmentPay).commit();
            }
        }
    */
    @Override
    public void onFragmentInteraction(ArrayList<String> selectname) {
        fragmentbag.setTextView(selectname);
    }
}

