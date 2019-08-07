package com.example.cube;

import android.content.Context;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MoonChangBagFragment extends Fragment {
    public static TextView textView;
    public static String selected;
    public static ArrayList<String> selectnames;
    public static ArrayList<String> selectprices;

    ArrayList<String>menuname;
    ArrayList<String>menuprice;
    int pricesum=0;



    public MoonChangBagFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState){

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_bag, null);

        menuname = new ArrayList<String>(
                Arrays.asList("바나나", "아보카도", "체리"));
        menuprice = new ArrayList<String>(
                Arrays.asList("3000", "7000", "5000"));

        selectprices=new ArrayList<String>();


        //결제하기버튼
        Button paybtn = (Button)view.findViewById(R.id.gotopaybutton);
        paybtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), selectnames.get(0),Toast.LENGTH_SHORT).show();


            }
        });



        for(int i=0;i<selectnames.size();++i){
            for(int j=0;j<menuname.size();++j){
                if(selectnames.get(i)==menuname.get(j)){
                    selectprices.add(menuprice.get(j));
                }
            }
        }
        for(int i=0;i<selectprices.size();++i)
            pricesum+=Integer.valueOf(selectprices.get(i));
        Toast.makeText(getActivity(),  String.valueOf(pricesum),Toast.LENGTH_SHORT).show();

        textView=(TextView)view.findViewById(R.id.priceSumView);
        textView.setText(String.valueOf(pricesum));

        ArrayList<HashMap<String, String>> selectfoodlist = new ArrayList<HashMap<String, String>> ();
        for(int i=0;i<selectprices.size();++i){

            HashMap<String,String> hashMap = new HashMap<>();

            hashMap.put("menu", selectnames.get(i) );
            hashMap.put("price",selectprices.get(i));

            selectfoodlist.add(hashMap);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),selectfoodlist,android.R.layout.simple_expandable_list_item_2,
                new String[]{"menu","price"},new int[]{android.R.id.text1,android.R.id.text2});
        ListView list=(ListView) view.findViewById(R.id.bagMenuListView);
        list.setAdapter(simpleAdapter);


        return view;

    }

    public  void setListViewText(ArrayList<HashMap<String,String>> Members){

    }


    public void setTextView(ArrayList<String> selectname) {

        //System.out.println("-------------------------------------------------------------");
        //System.out.println(selectname.get(0));
        //System.out.println("-------------------------------------------------------------");

        selectnames=selectname;

    }




}


