package com.example.cube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class NoticeActivity extends Fragment {
    RecyclerAdapter adapter;
    FloatingActionButton WriteNotice;

    public NoticeActivity () {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticepage, container, false);
        WriteNotice = (FloatingActionButton)view.findViewById(R.id.write_notice);

        WriteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getView().getContext(),"공지쓰기버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
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
        List<String> listTitle = Arrays.asList(
                "[공지] 쏜다 쿠폰에 대해 사과드립니다.",
                "[공지] L.POINT 시스템 점검 안내 (6/24(월) 00시~09시)",
                "[공지] 2019년 7월 카드사 무이자 할부 안내",
                "[공지] 토스 시스템점검 안내",
                "[공지] 개인정보처리방침 일부 변경에 관한 안내",
                "[공지] 배달의민족 앱 서비스 변경사항 안내",
                "[공지/일정변경] 서비스 점검 안내(7/18(목) 오전 6시~8시)",
                "[공지] 식품위생법 개정안 시행에 따른 배달앱 이물 신고 통보 의무화 안내"
        );
        List<String> listContent = Arrays.asList(
                "2019-06-19",
                "2019-06-23",
                "2019-06-28",
                "2019-07-01",
                "2019-07-08",
                "2019-07-08",
                "2019-07-09",
                "2019-07-10"
        );
        for (int i = listTitle.size()-1; i >= 0; --i) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            NoticeData data = new NoticeData();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }

}
