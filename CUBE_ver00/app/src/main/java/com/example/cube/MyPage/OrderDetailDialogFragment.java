package com.example.cube.MyPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cube.Components.Order;
import com.example.cube.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailDialogFragment extends DialogFragment {
    public static final String TAG = "Order-Detail";

    private TextView OrderNumText;
    private TextView OrderDetailText;
    private TextView OrderDateText;
    private TextView OrderSumText;
    private Order order;

    public OrderDetailDialogFragment(){}

    public OrderDetailDialogFragment (Order order){
        OrderDetailDialogFragment oddf = new OrderDetailDialogFragment();
        this.order = order;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_order_detail,container);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");


        OrderNumText = v.findViewById(R.id.order_num_text);
        OrderDetailText = v.findViewById(R.id.order_detail_text);
        OrderDateText = v.findViewById(R.id.order_date_text);
        OrderSumText = v.findViewById(R.id.order_sum_text);

        OrderNumText.setText(Integer.toString(order.getOrder_num()));

        List<HashMap<String, Object>> orderedList = order.getOrder_list();

        String orderDetailStr = "";
        int total_Price = 0;

        for (Map<String, Object> _map : orderedList) {
            orderDetailStr += (_map.get("name") + " X " + _map.get("num") +"\n");
            total_Price += (long) (_map.get("price")) * (long) (_map.get("num"));
        }

        OrderDetailText.setText(orderDetailStr);
        OrderDateText.setText(simpleDateFormat.format(order.getOrder_time()));
        OrderSumText.setText("총 " +Integer.toString(total_Price) + "원");

        setCancelable(true);
        return v;
    }


}
