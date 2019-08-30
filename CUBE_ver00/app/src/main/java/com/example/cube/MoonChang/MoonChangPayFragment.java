package com.example.cube.MoonChang;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.cube.Components.PayMenuData;
import com.example.cube.DBHelper;
import com.example.cube.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoonChangPayFragment extends Fragment {

    PayItemAdapter adapter;
    FloatingActionButton payBag;

    //db추가부분
    private DBHelper helper;
    private static SQLiteDatabase db;
    private ArrayList<PayMenuData> menuData;

    private OnFragmentInteractionListener mListener1;
    private OnFragmentInteractionListener mListener2;


    public MoonChangPayFragment() {
        // Required empty public constructor
    }

    private static final String RESULT_OK = null;
    public Uri CONTENT_URI;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.paymenulist);

        //리니어레이아웃-->그리드레이아웃으로 바꿨습니다
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        int numCol=2;
        GridLayoutManager gridLayoutManager=new GridLayoutManager(view.getContext(),numCol);
        //recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(gridLayoutManager);
        //db추가부분 + 어댑터
        helper = new DBHelper(getContext(), "MC_MENU.db", null, 1);
        db = helper.getWritableDatabase();
        menuData = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT name, photo, price FROM MC_MENU", null);
        while(c.moveToNext()){
            PayMenuData data = new PayMenuData();
            data.setName(c.getString(0));
            data.setPhoto(c.getString(1));
            data.setPrice(c.getInt(2));
            menuData.add(data);
        }

        adapter = new PayItemAdapter(menuData);
        // 까지


        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        //getData();


        //장바구니로가기
        payBag = (FloatingActionButton) view.findViewById(R.id.button_bag);
        payBag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoonChangBagActivity.class);
                startActivity(intent);
//                Fragment fragment = new MoonChangBagFragment();
//                if (fragment != null) {
//                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                    ft.addToBackStack(null);
//                    ft.replace(R.id.content_layout, fragment);
//                    ft.commit();
//                }
                //Intent intent = new Intent(getContext(), MoonChangBagFragment_tmp.class);
                //startActivity(intent);
/*
                //MainActivity mainActivity = (MainActivity) getActivity();
                //mainActivity.onFragmentChanged(0);
                Fragment frag=new MoonChangBagFragment();
                FragmentManager fmanager=getFragmentManager();
                FragmentTransaction ftrans=fmanager.beginTransaction();
                ftrans.replace(R.id.paylinearlayout,frag);
                ftrans.addToBackStack(null);
                ftrans.commit();
                if (mListener1 != null) {
                    //String input = selectname.get(0);
                    mListener1.onFragmentInteraction(selectname);
                }



            */

            }

        });
        return view;
    }

    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener1 = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener1 = null;
    }

    public interface OnFragmentInteractionListener {
        //void onFragmentInteraction(String text);

        void onFragmentInteraction(ArrayList<String> selectname);


    }
/*
    private void getData() {
        // 임의의 데이터입니다.
        List<String> listImage = Arrays.asList(
                "Null",
                "Null",
                "Null",
                "Null",
                "Null",
                "Null",
                "Null",
                "Null"
        );
        List<String> listName = Arrays.asList(
                "불닭마요",
                "치킨마요",
                "해물된장찌개",
                "삼겹살정식",
                "간장치킨",
                "오이냉국",
                "큐브스테이크덮밥",
                "간장계란밥"
        );
        List<String> listPrice = Arrays.asList(
                "3000",
                "4000",
                "5000",
                "6000",
                "4000",
                "5000",
                "7000",
                "12000"
        );
        for (int i = 0; i < listName.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            PayMenuData data = new PayMenuData();
            data.setName(listName.get(i));
            data.setPrice(listPrice.get(i));
            data.setImage(listImage.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
*/
}
