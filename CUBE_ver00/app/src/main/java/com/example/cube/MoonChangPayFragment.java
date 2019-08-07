package com.example.cube;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoonChangPayFragment extends Fragment {


    private Context context;
    private Button mBtnGet;
    private String[] data = { "바나나", "아보카도", "체리" };
    ListView listview ;
    ArrayList<String>menuname;
    ArrayList<String>menuprice;
    ArrayList<String>selectname= new ArrayList<String>();
    ArrayList<String>selectprice= new ArrayList<String>();
    PayItemAdapter adapter;

    int pricesum=0;
    private OnFragmentInteractionListener mListener1;
    private OnFragmentInteractionListener mListener2;




    public MoonChangPayFragment() {
        // Required empty public constructor
    }

    private static final String RESULT_OK = null;
    public Uri CONTENT_URI;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pay,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.paymenulist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PayItemAdapter();
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
        getData();

//        //장바구니에 넣기
//        listview.getCheckedItemPositions();
//
//        mBtnGet = (Button)view.findViewById(R.id.btnGet);
//
//        mBtnGet.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btnGet:
//                        SparseBooleanArray booleans = listview.getCheckedItemPositions();
//                        StringBuilder sb = new StringBuilder();
//                        for (int i = 0; i < data.length; i++) {
//                            if (booleans.get(i)) {
//                                sb.append(data[i]);
//                                selectname.add(menuname.get(i));
//                                selectprice.add(menuprice.get(i));
//                                pricesum+=Integer.parseInt(menuprice.get(i));
//                            }
//                        }
//                        Toast.makeText(getActivity(), sb.toString(),
//                                Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getActivity(), String.valueOf(pricesum),
//                                Toast.LENGTH_SHORT).show();
//                        listview.clearChoices();
//                        break;
//
//
//
//                    default:
//                        break;
//                }
//            }
//        });
        //액티비티로뭐보내기
        Fragment f = new Fragment();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) menuname); // list 넘기기
        f.setArguments(bundle);

        //장바구니로가기
        Button btn = (Button)view.findViewById(R.id.btnbag);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

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


            }


        });

        //장바구니초기화
        Button erasebtn = (Button)view.findViewById(R.id.eraseallbutton);
        erasebtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                selectname.clear();
                selectprice.clear();


            }



        });






        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener1 = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener1 = null;
    }

    public interface OnFragmentInteractionListener {
        //void onFragmentInteraction(String text);

        void onFragmentInteraction(ArrayList<String> selectname);


    }
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
                "3000원",
                "4000원",
                "5000원",
                "6000원",
                "4000원",
                "5000원",
                "7000원",
                "12000원"
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

}
