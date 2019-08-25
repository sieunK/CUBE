package com.example.cube.MoonChang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.CurrentApplication;
import com.example.cube.DBHelper;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoonChangBagActivity extends AppCompatActivity {

    private TextView totalPrice;

    private DBHelper helper;
    private static SQLiteDatabase db;

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;

    private CurrentApplication currentUserInfo;
    private String userNickname;

    private long currentOrderNum;

    private ArrayList<HashMap<String, Object>> selectedFoodList;
    long pricesum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moon_chang_bag);

        //SQLITE
        helper = new DBHelper(this, "BASKET.db", null, 1);
        db = helper.getWritableDatabase();

        //FIREBASE INSTANCE
        mStore = FirebaseFirestore.getInstance();  // DATABASE
        mAuth = FirebaseAuth.getInstance();        // USER

        //장바구니의 음식 리스트
        selectedFoodList = new ArrayList<>();

        // 로그인 정보 (닉네임 가져오기)
        currentUserInfo = (CurrentApplication)getApplication();
        userNickname = currentUserInfo.getNickname();

        // '문창' 문서 레퍼런스
        DocumentReference MoonChangRef = mStore.collection("foodcourt").document("moonchang");

        // 이번에 주문 넣을 시 들어갈 주문번호 가져옴
        MoonChangRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot dc = task.getResult();
                            if (dc.exists()) {
                                currentOrderNum = (long) dc.get("order_num");
                            } else {
                                Toast.makeText(getApplicationContext(), "실행 오류 - 문서를 찾지못함", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "실행 오류", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        // 장바구니(BASKET)에서 음식들을 꺼내옴 + 가격 총 합 계산
        Cursor c = db.rawQuery("SELECT name, num, price, photo FROM BASKET", null);
        while (c.moveToNext()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("name", c.getString(0));
            item.put("num", c.getInt(1));
            item.put("price", c.getInt(2));
            item.put("photo", c.getString(3));

            pricesum += (c.getInt(2) * (c.getInt(1)));
            selectedFoodList.add(item);
        }
        c.close();

        // 가격 총 합을 텍스트뷰로 띄워준다
        Toast.makeText(getApplicationContext(), "총 " + pricesum + "원 입니다.", Toast.LENGTH_SHORT).show();
        totalPrice = (TextView) findViewById(R.id.priceSumView);
        totalPrice.setText(String.valueOf(pricesum));


        // 리스트뷰 형성 + 아이템 클릭 시 삭제 기능
        final BagAdapter bagAdapter = new BagAdapter(selectedFoodList);
        ListView list = (ListView) findViewById(R.id.bagMenuListView);
        list.setAdapter(bagAdapter);

        /* 롱클릭시 아이템 전체 삭제 */
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                Map<String, Object> Selected = selectedFoodList.get(pos);
                int selectedPrice = (int) Selected.get("price");
                String selectedName = Selected.get("name").toString();
                int selectedNum = (int) Selected.get("num");
                Log.d("selectednum", Integer.toString(selectedNum));

                pricesum -= selectedPrice*selectedNum;
                totalPrice.setText(String.valueOf(pricesum));

                db.execSQL("DELETE FROM BASKET WHERE name ='" + selectedName + "';");
                selectedFoodList.remove(pos);

                bagAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /* 짧은 클릭시 아이템 수 1씩 감소 */
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Map<String, Object> Selected = selectedFoodList.get(pos);
                int selectedPrice = (int) Selected.get("price");
                String selectedName = Selected.get("name").toString();
                int selectedNum = (int) Selected.get("num");
                Log.d("selectednum", Integer.toString(selectedNum));

                pricesum -= selectedPrice;
                totalPrice.setText(String.valueOf(pricesum));

                if(selectedNum> 1){
                    db.execSQL("UPDATE BASKET SET num = num-1 ");
                    Selected.put("num", selectedNum-1);
                }
                else{
                    db.execSQL("DELETE FROM BASKET WHERE name ='" + selectedName + "';");
                    selectedFoodList.remove(pos);
                }
                bagAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //결제하기버튼
        Button PayButton = (Button) findViewById(R.id.gotopaybutton);
        PayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedFoodList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "장바구니가 비었습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 주문 문서로 넣을 Map
                Map<String, Object> order = new HashMap<>();

                order.put("called", false);                  // 호출 여부
                order.put("order_list", selectedFoodList);      // 주문 내역
                order.put("order_num", currentOrderNum + 1); // 주문 번호
                mStore.collection("foodcourt").document("moonchang")
                        .update("order_num", currentOrderNum + 1);

                order.put("order_time", new Date());    // 주문 시간
                order.put("standby", true);          // 대기 여부

                order.put("user", userNickname);        // 사용자

                //주문넣기
                mStore.collection("foodcourt/moonchang/order").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "주문하였습니다", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM BASKET");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "주문에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }


        });     //setOnClickListener
    }

    // 장바구니 리스트뷰 어댑터
    public class BagAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> bagList;

        public BagAdapter(ArrayList<HashMap<String, Object>> bagList) {
            this.bagList = bagList;
        }

        @Override
        public int getCount() {
            return bagList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_bag_menu, parent, false);
            }

            ImageView itemImage = (ImageView) convertView.findViewById(R.id.bag_food_image) ;
            TextView itemName = (TextView) convertView.findViewById(R.id.bag_food_name);
            TextView itemNum = (TextView) convertView.findViewById(R.id.bag_food_num);
            TextView itemPrice = (TextView) convertView.findViewById(R.id.bag_food_price);

            HashMap<String, Object> bagItem = bagList.get(position);

            String imageStr = bagItem.get("photo").toString();
            itemName.setText(bagItem.get("name").toString());
            itemNum.setText((bagItem.get("num").toString()));
            itemPrice.setText(bagItem.get("price").toString());
            if (imageStr == null) {
                itemImage.setImageResource(R.drawable.ic_logo);
            } else {
                byte[] photo = Base64.decode(imageStr, Base64.NO_WRAP);
                itemImage.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return bagList.get(position);
        }
    }
}
