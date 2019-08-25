package com.example.cube.MoonChang;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cube.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MoonChangMenuFragment extends Fragment {

    public MoonChangMenuFragment() {
        // Required empty public constructor
    }

    //ViewPager pager;
    ViewPager vp;
    String param1 = "nope";
    int dayofweek;
    int year;
    int month;
    int day;
    private String htmlPageUrl = "http://www.pusan.ac.kr/kor/CMS/MenuMgr/menuListOnBuilding.do?mCode=MN202#childTab_tmp"; //파싱할 홈페이지의 URL주소
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat="";

    int cnt=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //TextView textView = (TextView) view.findViewById(R.id.menuview);
        //TextView textView1 = (TextView) view.findViewById(R.id.todayview);
        //textView1.setText(String.valueOf(dayofweek) + String.valueOf(year) + String.valueOf(month) + String.valueOf(day));

        // 액티비티에서 param1이라는 string을 받아오려고 시도하는중인데 계속 null이네요ㅠ
        //if (this.getArguments() != null) {
        //
        //    param1 = this.getArguments().getString("param1");
        //} else
        //    param1 = "nonono";

        //textView.setText(param1);


        //뷰페이저(한주씩 옆으로 넘기는용) - 안에 식단정보띄우는건 시도중이에요
        vp = (ViewPager)view.findViewById(R.id.pager);
        Button btn_first = (Button)view.findViewById(R.id.btn_first);
        Button btn_second = (Button)view.findViewById(R.id.btn_second);
        Button btn_third = (Button)view.findViewById(R.id.btn_third);
        Button btn_fourth = (Button)view.findViewById(R.id.btn_fourth);
        Button btn_fifth = (Button)view.findViewById(R.id.btn_fifth);

        vp.setAdapter(new pagerAdapter(getActivity().getSupportFragmentManager()));
        vp.setOffscreenPageLimit(4);
        vp.setCurrentItem(0);

        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);
        btn_fourth.setOnClickListener(movePageListener);
        btn_fourth.setTag(3);
        btn_fifth.setOnClickListener(movePageListener);
        btn_fifth.setTag(4);
/*
        //크롤링
        textviewHtmlDocument = (TextView)view.findViewById(R.id.menuview);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기

        Button htmlTitleButton = (Button)view.findViewById(R.id.internetbutton);
        htmlTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println((cnt + 1) + "번째 파싱");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;
            }
        });
        */

        return view;
    }


    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(htmlPageUrl).get();


                //테스트1
                Elements titles= doc.select("p");
                int idx=0;
                System.out.println("-------------------------------------------------------------");
                for(Element e: titles){
                    if(0<idx) {
                        System.out.println(e.text());
                        htmlContentInStringFormat += e.text().trim() + "\n";
                    }
                    ++idx;

                }

                //테스트2
                //titles= doc.select("div.news-con h2.tit-news");

                System.out.println("-------------------------------------------------------------");
                //for(Element e: titles){
                //    System.out.println("title: " + e.text());
                //    htmlContentInStringFormat += e.text().trim() + "\n";
                //}

                //테스트3
                //titles= doc.select("li.section02 div.con h2.news-tl");

                System.out.println("-------------------------------------------------------------");
                //for(Element e: titles){
                //    System.out.println("title: " + e.text());
                //    htmlContentInStringFormat += e.text().trim() + "\n";
                //}
                System.out.println("-------------------------------------------------------------");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }




    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(androidx.fragment.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public androidx.fragment.app.Fragment getItem(int position)
        {
            MoonChangWeekMenu menu = new MoonChangWeekMenu();
            switch(position)
            {
                case 0:
                    return menu.setup(R.layout.moonchang_menu_week1);
                case 1:
                    return menu.setup(R.layout.moonchang_menu_week2);
                case 2:
                    return menu.setup(R.layout.moonchang_menu_week3);
                case 3:
                    return menu.setup(R.layout.moonchang_menu_week4);
                case 4:
                    return menu.setup(R.layout.moonchang_menu_week5);
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 5;
        }
    }


}



