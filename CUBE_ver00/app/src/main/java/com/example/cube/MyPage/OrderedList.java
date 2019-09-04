package com.example.cube.MyPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.BNUDialog;
import com.example.cube.Components.Order;
import com.example.cube.CurrentApplication;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderedList extends Fragment {
    private ArrayList<Order> orderList;
    private OrderedListAdapter adapter;
    private FirebaseFirestore mStore;
    private RecyclerView recyclerView;
    private BNUDialog dialog;

    public OrderedList() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStore = FirebaseFirestore.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordered_list,container,false);
        recyclerView = view.findViewById(R.id.recycler_order);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CurrentApplication currentUserInfo = (CurrentApplication) (getActivity().getApplication());
        String UserNickName = currentUserInfo.getNickname();

        orderList = new ArrayList<>();
        dialog = BNUDialog.newInstance("로딩 중입니다...");
        dialog.setCancelable(false);
        dialog.show(getActivity().getSupportFragmentManager(), BNUDialog.TAG);
        mStore.collection("foodcourt/moonchang/order").whereEqualTo("user", UserNickName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()){
                        Order order = ds.toObject(Order.class);
                        orderList.add(order);
                    }
                    FragmentManager fm = getFragmentManager();
                    adapter = new OrderedListAdapter(orderList, fm,getActivity());
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getContext(), "불러오기 실패!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
      //  getData();

        return view;
    }
}
