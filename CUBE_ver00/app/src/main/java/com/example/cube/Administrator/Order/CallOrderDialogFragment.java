package com.example.cube.Administrator.Order;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cube.R;


public class CallOrderDialogFragment extends DialogFragment implements View.OnClickListener{
    public static final String TAG = "Make Username Event";

    private CallOrderListener listener;

    private TextView callNum;
    private TextView callWho;
    private TextView callText;
    private TextView callState;
    private TextView callPrice;
    private TextView callTime;
    private Button callBtn;

    private static String nick_name;
    private static int order_Num;
    private static String order_list;
    private static boolean order_standby;
    private static boolean order_called;
    private static int total_price;
    private static  String order_time;

    public CallOrderDialogFragment(){}

    public static CallOrderDialogFragment newInstance(CallOrderListener listener, String userName, int orderNum, String orderList,
                                                      String orderTime, boolean orderStandby, boolean orderCalled, int totalPrice){
        CallOrderDialogFragment codf = new CallOrderDialogFragment();
        codf.listener = listener;
        nick_name= userName;
        order_Num = orderNum;
        order_list = orderList;
        order_called = orderCalled;
        order_standby = orderStandby;
        total_price = totalPrice;
        order_time = orderTime;
        return codf;
    }
    public interface CallOrderListener{
        void callUser(String UserName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_order_dialog_fragment,container);
        callNum = v.findViewById(R.id.call_order_num);
        callText =  v.findViewById(R.id.call_order_list);
        callWho = v.findViewById(R.id.call_order_who);
        callState = v.findViewById(R.id.call_order_state);
        callPrice = v.findViewById(R.id.call_order_price);
        callTime = v.findViewById(R.id.call_order_time);
        String msg =  order_list;

        callNum.setText(Integer.toString(order_Num));
        callText.setText(msg);
        callWho.setText(nick_name);
        callText.setText(order_time);

        if(!order_standby) callState.setText("완료");
        else if(order_called) callState.setText("호출됨");
        else callState.setText("대기중");

        callPrice.setText(Integer.toString(total_price));

        callBtn =(Button)v.findViewById(R.id.call_btn);
        callBtn.setOnClickListener(this);
        // setCancelable(false);
        return v;
    }
    @Override
    public void onClick(View v) {
        if(v==callBtn) {
            listener.callUser(nick_name);
            dismiss();
        }
    }
}

