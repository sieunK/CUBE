package com.example.cube;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PayItemAdapter extends RecyclerView.Adapter<PayItemAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<PayMenuData> listData = new ArrayList<>();
    private Context mContext;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_menu, parent, false);
        return new ItemViewHolder(view);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView foodName;
        private TextView foodPrice;
        private ImageView foodImage;
        private TextView foodCount;
        private Button countUp;
        private Button countDown;

        public LinearLayout Item;

        ItemViewHolder(View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.pay_food_name);
            foodPrice = itemView.findViewById(R.id.pay_food_price);
            foodImage = itemView.findViewById(R.id.pay_food_image);
            foodCount = itemView.findViewById(R.id.pay_food_count);
            countUp = itemView.findViewById(R.id.pay_count_up);
            countDown = itemView.findViewById(R.id.pay_count_down);
            Item = itemView.findViewById(R.id.pay_itemView);
        }

        void onBind(PayMenuData data) {
            foodName.setText(data.getName());
            foodPrice.setText(data.getPrice());
            //foodImage.setImage(data.getImage());
            foodCount.setText("0");
            Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"상세보기 클릭됨.", Toast.LENGTH_SHORT).show();
                }
            });
            countUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int myNum=0;
                    try {
                        myNum = Integer.parseInt(foodCount.getText().toString());
                    } catch(NumberFormatException nfe) {
                        Toast.makeText(v.getContext(),"Could not parse " + nfe,Toast.LENGTH_SHORT).show();
                    }
                    foodCount.setText(String.valueOf(myNum+=1));
                    Toast.makeText(v.getContext(),String.valueOf(myNum),Toast.LENGTH_SHORT).show();
                }
            });
            countDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int myNum=0;
                    try {
                        myNum = Integer.parseInt(foodCount.getText().toString());
                    } catch(NumberFormatException nfe) {
                        Toast.makeText(v.getContext(),"Could not parse " + nfe,Toast.LENGTH_SHORT).show();
                    }
                    if(myNum<=0) {
                        // 아무것도 안함.
                    } else {
                        myNum-=1;
                    }
                    foodCount.setText(String.valueOf(myNum));
                    Toast.makeText(v.getContext(),String.valueOf(myNum),Toast.LENGTH_SHORT).show();
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

    void addItem(PayMenuData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


}
