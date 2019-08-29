package com.example.cube.Administrator.Board;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.NoticeData;
import com.example.cube.CurrentApplication;

import com.example.cube.Notice.NoticeAddActivity;
import com.example.cube.Notice.NoticeReadActivity;
import com.example.cube.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// NoticeActivity (Fragment) 와 같음.
public class BoardFragment extends Fragment {
    @Nullable
    private Bundle savedInstanceState;

    FirebaseFirestore mStore;
    FirebaseAuth mAuth;

    private Query boardQuery;
    private String collectionPath;

    BoardAdapter adapter;
    RecyclerView boardRC;
    ArrayList<NoticeData> boardList;

    FloatingActionButton WriteNotice;

    CurrentApplication currentUserInfo;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticepage, null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.board_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        currentUserInfo = (CurrentApplication)getActivity().getApplication();
        WriteNotice = view.findViewById(R.id.write_notice);
        WriteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NoticeAddActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        collectionPath = "foodcourt/moonchang/board";
        boardQuery = mStore.collection(collectionPath)
                .orderBy("date", Query.Direction.DESCENDING);
        boardRC = view.findViewById(R.id.notice_recycler);
        boardRC.setLayoutManager(new LinearLayoutManager(getContext()));
        boardRC.setHasFixedSize(true);
        boardRC.addItemDecoration(new DividerItemDecoration(boardRC.getContext(), 1));

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();

        boardList = new ArrayList<>();

        boardQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot qs, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : qs.getDocumentChanges()) {
                    NoticeData data = dc.getDocument().toObject(NoticeData.class);
                    boardList.add(data);
                }
                adapter = new BoardAdapter(boardList);
                boardRC.setAdapter(adapter);
            }
        });
        boardRC.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), boardRC,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //     Log.d(this.getClass().getName(),"push");
                                String id = boardList.get(position).getId();
                                int postNumClicks = boardList.get(position).getNumClicks();
                                postNumClicks++;
                                mStore.collection(collectionPath).document(id).update("numClicks", postNumClicks);
                                Intent intent = new Intent(getContext(), NoticeReadActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        })
        );
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            boardRC.clearOnChildAttachStateChangeListeners();
            boardList.clear();
        }
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

}

