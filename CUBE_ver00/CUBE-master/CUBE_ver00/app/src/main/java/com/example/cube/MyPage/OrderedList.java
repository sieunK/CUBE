package com.example.cube.MyPage;

import android.os.Bundle;
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
                    adapter = new OrderedListAdapter(orderList, fm);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(getContext(), "불러오기 실패!", Toast.LENGTH_SHORT).show();
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
/*
    private void getData() {
        // 임의의 데이터입니다.
        List<String> listRestaurant = Arrays.asList(
                "샛벌회관",
                "샛벌회관",
                "학생회관",
                "금정회관",
                "금정회관",
                "금정회관",
                "문창회관",
                "샛벌회관"
        );
        List<String> listDate = Arrays.asList(
                "2019-07-10",
                "2019-07-09",
                "2019-07-08",
                "2019-07-08",
                "2019-07-01",
                "2019-06-28",
                "2019-06-23",
                "2019-06-19"
        );
        List<String> listDetail = Arrays.asList(
                "불닭마요 외 1개",
                "대패삼겹볶음 외 2개",
                "해물된장찌개",
                "일품1 외 1개",
                "정식 외 2개",
                "일품2 외 1개",
                "큐브스테이크덮밥 외 1개",
                "고구마치즈돈가스 외 1개"
        );
        for (int i = 0; i < listRestaurant.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Order data = new Order();
            data.setDate(listDate.get(i));
            data.setContent(listRestaurant.get(i));
            data.setDetail(listDetail.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
    */
}
