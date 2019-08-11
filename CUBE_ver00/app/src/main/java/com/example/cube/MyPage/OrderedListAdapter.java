package com.example.cube.MyPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.Order;
import com.example.cube.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderedListAdapter extends RecyclerView.Adapter<OrderedListAdapter.ItemViewHolder> {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    // adapter에 들어갈 list 입니다.
    private ArrayList<Order> listData = new ArrayList<>();
    private Context mContext;
    private FragmentManager fm;

    public OrderedListAdapter(ArrayList<Order> orderList, FragmentManager fragmentManager){
        listData = orderList;
        fm = fragmentManager;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_ordered, parent, false);
        return new ItemViewHolder(view);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView RestaurantName;
        private TextView orderDate;
        private TextView orderDetail;
        private int pos;
        private Button writeReview;
        public LinearLayout Item;

        ItemViewHolder(View itemView) {
            super(itemView);

            RestaurantName = itemView.findViewById(R.id.ordered_itemView_title);
            orderDate = itemView.findViewById(R.id.ordered_itemView_date);
            orderDetail = itemView.findViewById(R.id.ordered_itemView_content);

            writeReview = itemView.findViewById(R.id.button_write_review);
            Item = itemView.findViewById(R.id.ordered_itemView);
        }

        void onBind(Order data, int position) {
            pos = position; // ***아이템의 리스트에서의 위치를 기억.

            // order collection은 식당별로 있기도하고 일단 문창만 주문을 했을 때 리뷰를 쓰는 것으로 하였음.
            RestaurantName.setText("문창회관");

            orderDate.setText(simpleDateFormat.format(data.getOrder_time()));

            List<Map<String, Object>> orderedList = data.getOrder_list();
            String orderDetailStr = "";
            Map<String, Object> _map = orderedList.get(0);
                orderDetailStr += (_map.get("name") + "X" + _map.get("num") + "외" + (orderedList.size()-1)+"개");

            orderDetail.setText(orderDetailStr);


            Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"상세보기", Toast.LENGTH_SHORT).show();
                    Order order = listData.get(pos);
                    OrderDetailDialogFragment oddf
                            = new OrderDetailDialogFragment(order);
                    oddf.show(fm, OrderDetailDialogFragment.TAG);
                }
            });
            writeReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "리뷰쓰기 버튼 클릭됨.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.

        holder.onBind(listData.get(position), position);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Order data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


}
