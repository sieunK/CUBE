package com.example.cube.Administrator.Review;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.CurrentApplication;
import com.example.cube.MoonChang.WriteReviewPopUp;
import com.example.cube.R;
import com.example.cube.Review.ReviewAdapter;
import com.example.cube.Review.ReviewParent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminReviewFragment extends Fragment implements View.OnClickListener{

    FirebaseFirestore mStore;
    CurrentApplication currentUserInfo;

    RecyclerView recyclerView;
    ReviewAdapter adapter;
    ArrayList<ReviewParent> reviewList; //부모 리스트
    FloatingActionButton writeReview;

    public AdminReviewFragment() { }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);

        currentUserInfo = (CurrentApplication) (getActivity().getApplication());
        mStore = FirebaseFirestore.getInstance();

        String collectionPath = "foodcourt/moonchang/review";
        final Query reviewQuery = mStore.collection(collectionPath).orderBy("date", Query.Direction.DESCENDING);
        final Query reviewQuery = mStore.collection(collectionPath).orderBy("date", Query.Direction.ASCENDING);

        reviewList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.view_reviewList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));

        reviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot qs, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : qs.getDocumentChanges()) {
                    ReviewParent data = dc.getDocument().toObject(ReviewParent.class);
                    reviewList.add(data);
                }
                adapter = new ReviewAdapter(getActivity(), reviewQuery);
                recyclerView.setAdapter(adapter);
            }
        });

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



       // listView.setGroupIndicator(null); //리스트뷰 기본 아이콘 표시 여부
      //  setListItems();


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


