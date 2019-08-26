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

// NoticeActivity (Fragment) 와 기능은 같지만 이름만 BoardActivity (Activity) 로 달리함.
public class BoardActivity extends AppCompatActivity {
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
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noticepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.board_toolbar);
        setSupportActionBar(toolbar);

        currentUserInfo = (CurrentApplication)getApplication();
        WriteNotice = (FloatingActionButton)findViewById(R.id.write_notice);
        WriteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoticeAddActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        collectionPath = "foodcourt/moonchang/board";
        boardQuery = mStore.collection(collectionPath)
                .orderBy("date", Query.Direction.DESCENDING);
        boardRC = findViewById(R.id.notice_recycler);
    }


    @Override
    protected void onStart() {
        super.onStart();

        boardRC.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        boardRC.setHasFixedSize(true);
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
        boardRC.addItemDecoration(new DividerItemDecoration(boardRC.getContext(), 1));
        boardRC.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), boardRC,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //     Log.d(this.getClass().getName(),"push");
                                String id = boardList.get(position).getId();
                                int postNumClicks = boardList.get(position).getNumClicks();
                                postNumClicks++;
                                mStore.collection(collectionPath).document(id).update("numClicks", postNumClicks);
                                Intent intent = new Intent(getApplicationContext(), NoticeReadActivity.class);
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

