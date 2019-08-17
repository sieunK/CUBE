package com.example.cube.MoonChang;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import com.example.cube.R;
import com.example.cube.Review.ReviewAdapter;
import com.example.cube.Review.ReviewParent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MoonChangReviewFragment extends Fragment implements View.OnClickListener{

    RecyclerView recyclerView;
    ReviewAdapter adapter;
    ArrayList<ReviewParent> reviewList = new ArrayList<>(); //부모 리스트
    FloatingActionButton writeReview;

    public MoonChangReviewFragment() { }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.view_reviewList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //어댑터에 각각의 배열 등록
        adapter = new ReviewAdapter(getActivity());
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

        writeReview = (FloatingActionButton) view.findViewById(R.id.review_write);
        writeReview.setOnClickListener(this);
//        writeReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View v = getLayoutInflater().inflate(R.layout.popup_write_review, null);
//                final RatingBar ratingBar = (RatingBar) v. findViewById(R.id.rating_write);
//                final EditText reviewMain = (EditText) v.findViewById(R.id.review_write);
//
//                AlertDialog Ad = new AlertDialog.Builder(v.getContext()).setTitle("리뷰 쓰기").setView(v)
//                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setPositiveButton("완료", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //입력받은 월 값에 맞는 month 배열을 찾아 새로운 요소를 등록한다
//                                Bundle bundle = new Bundle();
//                                bundle.putString("comment",reviewMain.getText().toString());
//                                bundle.putFloat("rating",ratingBar.getRating());
//                                b.setArguments();
//                                FragmentManager fm = getFragmentManager();
//                                fm.begintransaction().replace(R.id.content, b).commit();
//                                dialog.dismiss();
//                            }
//                        }).show();
//                Ad.setOnDismissListener((DialogInterface.OnDismissListener)getActivity());
//            }
//        });
//        //monthArray에 1월~12월 배열을 모두 추가
//        for (int i = 0; i < 12; i++)
//            monthArray.add(new ArrayList<ReviewChild>());



        //listView.setGroupIndicator(null); //리스트뷰 기본 아이콘 표시 여부
        setListItems();


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.review_write:
                // 데이터를 다이얼로그로 보내는 코드
                Bundle args = new Bundle();
                args.putString("key", "value");
                //---------------------------------------.//
                WriteReviewPopUp dialog = new WriteReviewPopUp();
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(),"review");
                dialog.setDialogResult(new WriteReviewPopUp.OnMyDialogResult() {
                    @Override
                    public void finish(Bundle result) {
                        ReviewParent reviewParent = new ReviewParent();
                        reviewParent.setReview(result.getString("review"));
                        reviewParent.setRating(result.getFloat("rating"));
                        adapter.addItem(reviewParent);
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    //리스트 초기화 함수
    public void setListItems() {
        reviewList.clear();

        adapter.notifyDataSetChanged();
    }
}


