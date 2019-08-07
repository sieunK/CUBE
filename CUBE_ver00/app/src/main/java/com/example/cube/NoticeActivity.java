package com.example.cube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class NoticeActivity extends Fragment {

    FirebaseFirestore mStore;
    FirebaseAuth mAuth;

    private Query noticeQuery;
    private String collectionPath;

    NoticeAdapter adapter;
    RecyclerView noticeRC;
    ArrayList<NoticeData> noticeList;

    FloatingActionButton WriteNotice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticepage, container, false);

        WriteNotice = (FloatingActionButton)view.findViewById(R.id.write_notice);
        WriteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser().getEmail().equals("ysc9606@naver.com")) {
                    Intent intent = new Intent(getContext(), NoticeAddActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(getContext(), "관리자가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        collectionPath = "foodcourt/moonchang/board";
        noticeQuery = mStore.collection(collectionPath)
                .orderBy("date", Query.Direction.DESCENDING);
        noticeRC = view.findViewById(R.id.notice_recycler);
        noticeRC.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        noticeList = new ArrayList<>();


        noticeQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot qs, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : qs.getDocumentChanges()) {
                    NoticeData data = dc.getDocument().toObject(NoticeData.class);
                    noticeList.add(data);
                    //   Log.d("content", data.getContent());                    Log.d("numclicks", Integer.toString( data.getNumClicks()));
                    //   Log.d("numcomments", Integer.toString(data.getNumComments()));

                }
                adapter = new NoticeAdapter(noticeList);
                noticeRC.setAdapter(adapter);
            }
        });
        noticeRC.addItemDecoration(new DividerItemDecoration(noticeRC.getContext(), 1));
        noticeRC.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), noticeRC,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //     Log.d(this.getClass().getName(),"push");
                                String id = noticeList.get(position).getId();
                                int postNumClicks = noticeList.get(position).getNumClicks();
                                postNumClicks++;
                                mStore.collection(collectionPath).document(id).update("numClicks", postNumClicks);
                                Intent intent = new Intent(getContext(), NoticeReadActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        })
        );
    }

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        Log.d("long", "press");
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
    /*
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
    }*/

}
