package com.example.cube.Administrator.Order;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.Order;
import com.example.cube.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    protected List<String> mOrderIds = new ArrayList<>();
    protected List<Order> mOrderList = new ArrayList<>();
    protected boolean[] mOrderChecked;
    protected ListenerRegistration listenerRegistration;

    public OrderAdapter(Query query) {
        listenerRegistration = query.addSnapshotListener(eventListener);
    }

    private EventListener eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot snapshots,
                            @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                return;
            }
            String orderKey;
            int orderIndex;
            Order order;

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        order = dc.getDocument().toObject(Order.class);
                        // Update RecyclerView
                        mOrderIds.add(0, dc.getDocument().getId());
                        mOrderList.add(0, order);
                        notifyItemInserted(0);
                        Log.d("receive Time", (order.getOrder_time()).toString());
                        Log.d("Id", mOrderIds.get(0));
                        Log.d("size", Integer.toString(mOrderIds.size()));
                        Log.d("standby", Boolean.toString(order.isStandby()));
                        Log.d("called", Boolean.toString(order.isCalled()));
                        break;

                    case MODIFIED:
                        order = dc.getDocument().toObject(Order.class);
                        orderKey = dc.getDocument().getId();
                        orderIndex = mOrderIds.indexOf(orderKey);
                        if (orderIndex > -1) {
                            // Replace with the new data
                            mOrderList.set(orderIndex, order);

                            // Update the RecyclerView
                            notifyItemChanged(orderIndex);
                        } else {
                            Log.w("OrderListFragment", "Order Changed" + orderKey);
                        }
                        break;
                    case REMOVED:

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        orderKey = dc.getDocument().getId();
                        orderIndex = mOrderIds.indexOf(orderKey);
                        if (orderIndex > -1) {
                            // Remove data from the list
                            mOrderIds.remove(orderIndex);
                            mOrderList.remove(orderIndex);

                            // Update the RecyclerView
                            notifyItemRemoved(orderIndex);
                        } else {
                            Log.w("OrderListFragment", "onChildRemoved:unknown_child:" + orderKey);
                        }
                        break;
                }
            }
            mOrderChecked = new boolean[mOrderIds.size()];
        }
    };


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_basic, parent, false));
    }

    @Override
    @NonNull
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int pos) {
        Order data = mOrderList.get(pos);

        // 대 기 / 완 료
            boolean isWaiting = data.isStandby();
           if(!isWaiting) holder.itemView.setBackgroundColor(Color.argb(100, 190, 190, 187));
        else {
               // 호 출 여 부
               boolean calledOrNot = data.isCalled();
               if (calledOrNot) holder.itemView.setBackgroundColor(Color.argb(100, 21, 158, 26));
               else holder.itemView.setBackgroundColor(Color.argb(100, 255, 193, 7));
        /*if (calledOrNot) holder.mCalledImageView.setVisibility(View.VISIBLE);
        else holder.mCalledImageView.setVisibility(View.INVISIBLE);
        */
           }

        // 주 문 번 호
        holder.mNumberTextView.setText(Integer.toString(data.getOrder_num()));

         // 체 크 박 스
        boolean checked = mOrderChecked[pos];
        if(!checked) holder.mCheckBox.setVisibility(View.INVISIBLE);
        else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(mOrderChecked[pos]);
            holder.mCheckBox.setClickable(false);
            holder.mCheckBox.setFocusable(false);
        }


        // 주 문 내 용
        String orderList_String = "";
        //int total_Price = 0;

        Log.d("data", Integer.toString(data.getOrder_list().size()));
        List<HashMap<String, Object>> orderList = data.getOrder_list();
        for (Map<String, Object> _map : orderList) {
            orderList_String += (_map.get("name") + " " + _map.get("num") + "\n");
            //    total_Price += (long) (_map.get("price")) * (long) (_map.get("num"));
        }
        holder.mListTextView.setText(orderList_String);



        // 가 격
        //holder.mPriceTextView.setText(Integer.toString(total_Price) + "원");

        // 주 문 시 간
        //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        // holder.mTimeTextView.setText(simpleDateFormat.format(data.getOrder_time()));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public void cleanupListener() {
        listenerRegistration.remove();
    }

    public void resetAll(Query newQuery) {
        cleanupListener();
        mOrderList.clear();
        mOrderIds.clear();
        listenerRegistration = newQuery.addSnapshotListener(eventListener);

    }


    public void setChecked(int position) {
        mOrderChecked[position] = !mOrderChecked[position];
    }

    public void setAllChecked(boolean isChecked) {
        int size = mOrderChecked.length;
        for (int i = 0; i < size; ++i)
            mOrderChecked[i] = isChecked;
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
            private CheckBox mCheckBox;
        private TextView mNumberTextView;
        private TextView mListTextView;
        //    private TextView mStateTextView;
        //    private ImageView mCalledImageView;
        //    private TextView mPriceTextView;
        //    private TextView mTimeTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.order_checkbox_basic);
            mNumberTextView = itemView.findViewById(R.id.order_item_num_basic);
            mListTextView = itemView.findViewById(R.id.order_item_list_basic);
            //      mStateTextView = itemView.findViewById(R.id.order_item_state);
            //      mCalledImageView = itemView.findViewById(R.id.order_item_called);
            //      mPriceTextView = itemView.findViewById(R.id.order_item_total_price);
            //      mTimeTextView = itemView.findViewById(R.id.order_item_time);
        }
    }

}
