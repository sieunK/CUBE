package com.example.cube.MoonChang;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;


import com.example.cube.BNUDialog;
import com.example.cube.CurrentApplication;
import com.example.cube.R;
import com.example.cube.Review.ReviewAdapter;
import com.example.cube.Review.ReviewParent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MoonChangReviewFragment extends Fragment{

    FirebaseFirestore mStore;
    CurrentApplication currentUserInfo;
    ArrayList<ReviewParent> reviewList; //부모 리스트

    RecyclerView recyclerView;
    ReviewAdapter adapter;

    FloatingActionButton writeReview;

    public MoonChangReviewFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        AppBarLayout toolbarLayout =  view.findViewById(R.id.review_toolbar_layout);
        toolbarLayout.setVisibility(View.GONE);

        currentUserInfo = (CurrentApplication) (getActivity().getApplication());
        mStore = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.view_reviewList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));

        String collectionPath = "foodcourt/moonchang/review";
        final Query reviewQuery = mStore.collection(collectionPath).orderBy("date", Query.Direction.ASCENDING);

        reviewList = new ArrayList<>();
        final BNUDialog dialog = BNUDialog.newInstance("로딩 중입니다...");
        dialog.setCancelable(false);
        dialog.show(getActivity().getSupportFragmentManager(), BNUDialog.TAG);
        reviewQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot qs, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : qs.getDocumentChanges()) {
                    ReviewParent data = dc.getDocument().toObject(ReviewParent.class);
                    Log.d("Data Inserted  ", data.getComment());
                    reviewList.add(data);
                }
                adapter = new ReviewAdapter(getActivity(), reviewQuery);
                adapter.setHasStableIds(true);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();

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
        dialog.dismiss();
        return view;
    }



//    //리스트 초기화 함수
//    public void setListItems() {
//        reviewList.clear();
//
//        adapter.notifyDataSetChanged();
//    }
}

