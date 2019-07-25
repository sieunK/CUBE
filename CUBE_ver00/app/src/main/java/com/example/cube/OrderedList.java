package com.example.cube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class OrderedList extends Fragment {
    OrderedListAdapter adapter;

    public OrderedList() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordered_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_order);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrderedListAdapter();
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

        return view;
    }

    private void getData() {
        // 임의의 데이터입니다.
        List<String> listRestaurant = Arrays.asList(
                "샛벌회관",
                "샛벌회관",
                "학생회관",
                "금정회관",
                "금정회관",
                "금정회관",
                "문창회관",
                "샛벌회관"
        );
        List<String> listDate = Arrays.asList(
                "2019-07-10",
                "2019-07-09",
                "2019-07-08",
                "2019-07-08",
                "2019-07-01",
                "2019-06-28",
                "2019-06-23",
                "2019-06-19"
        );
        List<String> listDetail = Arrays.asList(
                "불닭마요 외 1개",
                "대패삼겹볶음 외 2개",
                "해물된장찌개",
                "일품1 외 1개",
                "정식 외 2개",
                "일품2 외 1개",
                "큐브스테이크덮밥 외 1개",
                "고구마치즈돈가스 외 1개"
        );
        for (int i = 0; i < listRestaurant.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            OrderedData data = new OrderedData();
            data.setDate(listDate.get(i));
            data.setContent(listRestaurant.get(i));
            data.setDetail(listDetail.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}
