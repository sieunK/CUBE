package com.example.cube.MoonChang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.cube.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MoonChangFragment extends Fragment /*implements MoonChangPayFragment.OnFragmentInteractionListener*/ {

    private MoonChangBagFragment fragmentbag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moonchang, container, false);
       // fragmentbag = new MoonChangBagFragment();

        //TabLayout
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("결제"));
        tabs.addTab(tabs.newTab().setText("메뉴"));
        tabs.addTab(tabs.newTab().setText("리뷰"));
        tabs.addTab(tabs.newTab().setText("식당정보"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);

        //어답터설정
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final MoonChangPagerAdapter myPagerAdapter = new MoonChangPagerAdapter(getActivity().getSupportFragmentManager(), 4);
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

        return view;
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
  //  @Override
  //  public void onFragmentInteraction(ArrayList<String> selectname) {
  //      fragmentbag.setTextView(selectname);
   // }
}

