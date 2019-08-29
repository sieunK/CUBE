package com.example.cube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.NoticeData;
import com.example.cube.MoonChang.MoonChangFragment;
import com.example.cube.Notice.NoticeActivity;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Fragment {
    FirebaseFirestore mStore;
    ImageView moonChang;
    ImageView saetBul;
    ImageView geumJeong;
    ImageView hakSaeng;
    //Button notice_more;
    RecyclerView notice_shortView;
    MainAdapter adapter;
    ArrayList<NoticeData> noticeList;
    Fragment fragment;
    ScrollView scrollView;

    public HomeActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mStore = FirebaseFirestore.getInstance();
        //notice_more = (Button) view.findViewById(R.id.button_more_notice);
        moonChang = (ImageView) view.findViewById(R.id.button_moon);
        saetBul = (ImageView) view.findViewById(R.id.button_saet);
        geumJeong = (ImageView) view.findViewById(R.id.button_geum);
        hakSaeng = (ImageView) view.findViewById(R.id.button_hak);
        notice_shortView = (RecyclerView) view.findViewById(R.id.notice_short);
        notice_shortView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        scrollView = (ScrollView)view.findViewById(R.id.scroll_home);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

//        notice_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new NoticeActivity();
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.content_layout, fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });
        fragment = new MoonChangFragment();
        moonChang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.content_layout, fragment);
                    ft.commit();
                }
                //DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(GravityCompat.START);
                //startActivity(new Intent(getActivity().getApplicationContext(),MoonChangActivity.class));
                // Toast.makeText(getActivity().getApplicationContext(),"문창회관 클릭됨.",Toast.LENGTH_SHORT).show();
            }
        });
        saetBul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "샛벌회관 클릭됨.", Toast.LENGTH_SHORT).show();
            }
        });
        geumJeong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "금정회관 클릭됨.", Toast.LENGTH_SHORT).show();
            }
        });
        hakSaeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "학생회관 클릭됨.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeList = new ArrayList<>();
        String collectionPath = "foodcourt/moonchang/board";
        Query noticeQuery = mStore.collection(collectionPath)
                .orderBy("date", Query.Direction.DESCENDING).limit(3);
        noticeQuery.
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            NoticeData data = dc.getDocument().toObject(NoticeData.class);
                            //String id = (String) dc.getDocument().getData().get("id");
                            //String title = (String) dc.getDocument().getData().get("title");
                            //String name = (String) dc.getDocument().getData().get("name");
                            //String content = (String) dc.getDocument().getData().get("content");
                            //Integer numClicks = (int)(long) dc.getDocument().getData().get("numclicks");
                            //Timestamp date = (Date) dc.getDocument().getData().get("date");
                            //if(numClicks == null) data = new Board(id, title, content, name, 0, date);
                            //else data = new Board(id, title, content, name, numClicks , date);
                            noticeList.add(data);
                        }
                        adapter = new MainAdapter(noticeList);
                        notice_shortView.setAdapter(adapter);
                    }
                });
        MainAdapter adapter = new MainAdapter(noticeList);
        notice_shortView.setAdapter(adapter);
        notice_shortView.addItemDecoration(new DividerItemDecoration(notice_shortView.getContext(), 1));

    }

    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
        private List<NoticeData> noticeList;

        public MainAdapter(List<NoticeData> noticeList) {
            this.noticeList = noticeList;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_short, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int pos) {
            NoticeData data = noticeList.get(pos);
            holder.mTitleTextView.setText(data.getTitle());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
            holder.mDateTextView.setText(simpleDateFormat.format(data.getDate()));
        }

        @Override
        public int getItemCount() {
            return noticeList.size();
        }

        public void cleanupListener() {

        }

        class MainViewHolder extends RecyclerView.ViewHolder {
            private TextView mTitleTextView;
            private TextView mDateTextView;
            private TextView mNewTagView;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
                mTitleTextView = itemView.findViewById(R.id.board_item_title_short);
                mDateTextView = itemView.findViewById(R.id.board_item_date_short);
                mNewTagView = itemView.findViewById(R.id.board_item_newtag_short);
            }
        }
    }
}
