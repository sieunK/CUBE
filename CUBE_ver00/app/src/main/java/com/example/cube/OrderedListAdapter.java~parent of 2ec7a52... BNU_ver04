package com.example.cube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderedListAdapter extends RecyclerView.Adapter<OrderedListAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<OrderedData> listData = new ArrayList<>();
    private Context mContext;

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

        void onBind(OrderedData data) {
            RestaurantName.setText(data.getContent());
            orderDate.setText(data.getDate());
            orderDetail.setText(data.getDetail());
            Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"상세보기 클릭됨.", Toast.LENGTH_SHORT).show();
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
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(OrderedData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


}
