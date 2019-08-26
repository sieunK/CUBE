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
    private TextView callText;
    private Button callBtn;
    private static String nick_name;
    private static int order_Num;
    private static String order_list;
    public CallOrderDialogFragment(){}

    public static CallOrderDialogFragment newInstance(CallOrderListener listener, String user_name, int order_num, String orderList){
        CallOrderDialogFragment codf = new CallOrderDialogFragment();
        codf.listener = listener;
        nick_name= user_name;
        order_Num = order_num;
        order_list = orderList;
        return codf;
    }
    public interface CallOrderListener{
        void callUser(String UserName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_order_dialog_fragment,container);
        callText = (TextView) v.findViewById(R.id.call_txt);
        String msg = "주문번호 : " + Integer.toString(order_Num) +  "\n\n이 름 : " + nick_name
                +"\n\n주문내역 :\n" + order_list;
        callText.setText(msg);
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

