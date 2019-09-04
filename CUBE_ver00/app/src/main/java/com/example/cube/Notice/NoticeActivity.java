package com.example.cube.Notice;

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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.NoticeData;
import com.example.cube.CurrentApplication;
import com.example.cube.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    CurrentApplication currentUserInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserInfo = (CurrentApplication)(getActivity().getApplication());

    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticepage, container, false);
        final boolean isAdmin  =currentUserInfo.isAdmin();

        AppBarLayout toolbarLayout = view.findViewById(R.id.board_toolbar_layout);
        toolbarLayout.setVisibility(View.GONE);

        WriteNotice = (FloatingActionButton)view.findViewById(R.id.write_notice);
        WriteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdmin) {
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
        noticeRC.setHasFixedSize(true);
        noticeRC.addItemDecoration(new DividerItemDecoration(noticeRC.getContext(), 1));

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


}
