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

public class AdminReviewFragment extends Fragment{

    FirebaseFirestore mStore;
    CurrentApplication currentUserInfo;

    RecyclerView recyclerView;
    ReviewAdapter adapter;
    ArrayList<ReviewParent> reviewList; //부모 리스트

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

        return view;
    }


    //리스트 초기화 함수
    public void setListItems() {
        reviewList.clear();

        adapter.notifyDataSetChanged();
    }
}


