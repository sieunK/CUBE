package com.example.cube.Administrator.Order;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cube.BNUDialog;
import com.example.cube.Components.Order;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowSalesStatusActivity extends AppCompatActivity implements View.OnClickListener {
    // 날짜 선택
    private TextView datePickerTextView;

    //월 매출
    private TextView salesMonth;
    private long sellMonth;

    //일 매출
    private TextView salesDay;
    private long sellDay;

    //파이어스토어
    private FirebaseFirestore mStore;

    //주문 컬렉션
    private CollectionReference collectionRef;

    //db에서 데이터 받아오는 리스트
    private ArrayList<Map<String, Object>> foodList;

    //데이터 띄워주는 RECYCLERVIEW 와 ADAPTER
    private RecyclerView salesListView;
    private salesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sales_status);

        mStore = FirebaseFirestore.getInstance();
        collectionRef = mStore.collection("foodcourt/moonchang/order");

        foodList = new ArrayList<>();

        salesListView = (RecyclerView) findViewById(R.id.sales_list);
        salesListView.setLayoutManager(new LinearLayoutManager(ShowSalesStatusActivity.this));
        salesListView.setHasFixedSize(true);

        salesDay = findViewById(R.id.sales_day);
        salesMonth = findViewById(R.id.sales_month);

        datePickerTextView = (TextView) findViewById(R.id.sales_date_picker_text);
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        datePickerTextView.setText(simpleDateFormat.format(today));
        datePickerTextView.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        sellDay = 0;    // 일 매출

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 오늘 0시 0분 0초
        Date today = new Date(cal.getTimeInMillis());
        Timestamp todayStart = new Timestamp(today);

        cal.add(Calendar.DATE, 1);
        // 내일 0시 0분 0초
        Date tomorrow = new Date(cal.getTimeInMillis());
        Timestamp tomorrowStart = new Timestamp(tomorrow);

        // 로딩 중...
        final BNUDialog dialog = BNUDialog.newInstance("로딩 중입니다...");
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), BNUDialog.TAG);


        // 오늘 날짜에 해당하는 데이터 쿼리
        collectionRef
                .whereGreaterThanOrEqualTo("order_time", todayStart)
                .whereLessThan("order_time", tomorrowStart)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                //데이터 불러오기
                                Order order = dc.toObject(Order.class);

                                List<Map<String, Object>> OrderList = order.getOrder_list();

                                for (Map<String, Object> _map : OrderList) {
                                    sellDay += (long) (_map.get("num")) * (long) (_map.get("price"));
                                    int i;

                                    for (i = 0; i < foodList.size(); ++i) {
                                        if (foodList.get(i).containsKey(_map.get("name").toString())) {
                                            long soldNum = (long) (foodList.get(i).get("num"));
                                            foodList.get(i).put("num", soldNum + 1);
                                            break;
                                        }
                                    }
                                    if (i == foodList.size()) {
                                        foodList.add(_map);
                                        Log.d("added", foodList.get(0).get("name").toString());
                                    }
                                }
                            }
                            mAdapter = new salesAdapter(foodList);

                            //일 매출 표시
                            salesListView.setAdapter(mAdapter);
                            salesDay.setText(Long.toString(sellDay));

                            dialog.dismiss();

                        } else {
                            Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                            Log.d("ShowSalesStatusActivity", "Error getting documents: ", task.getException());
                            dialog.dismiss();

                        }
                    }

                });
    }


    // 날짜 선택 시
    @Override
    public void onClick(View view) {
        if (view == datePickerTextView) {
            Calendar cal = Calendar.getInstance(Locale.KOREA);
            DatePickerDialog dateDialog = new DatePickerDialog(ShowSalesStatusActivity.this, dateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dateDialog.show();

        }
    }


    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int Y, int M, int D) {
            sendQuery(Y, M, D);
        }
    };


    // 달력에서 날짜 선택 시
    private void sendQuery(int Y, int M, int D) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Y, M, D);
        Date beginTime = new Date(cal.getTimeInMillis());
        Timestamp getDayStart = new Timestamp(beginTime);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endTime = new Date(cal.getTimeInMillis());
        Timestamp getDayLast = new Timestamp(endTime);
        Log.d("send Time", getDayStart.toString() + " " + getDayLast.toString());

        // 받은 날짜에 맞는 새 쿼리 전달
        Query query = collectionRef
                .whereGreaterThanOrEqualTo("order_time", getDayStart)
                .whereLessThan("order_time", getDayLast)
                .orderBy("order_time", Query.Direction.ASCENDING);
        mAdapter.resetAll(query);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        datePickerTextView.setText(simpleDateFormat.format(beginTime));
    }


    public class salesAdapter extends RecyclerView.Adapter<salesViewHolder> {
        private List<Map<String, Object>> list;

        private salesAdapter(ArrayList<Map<String, Object>> mlist) {
            this.list = mlist;
            Log.d("Size of List : ", Integer.toString(list.size()));

        }

        @NonNull
        @Override
        public salesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new salesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull salesViewHolder holder, int pos) {
            Map<String, Object> data = list.get(pos);
            holder.salesNameView.setText(data.get("name").toString());
            holder.salesPriceView.setText(data.get("price").toString());
            holder.salesNumView.setText(Long.toString((long) data.get("num")));
            holder.salesSumView.setText(Long.toString((long) (data.get("num")) * (long) (data.get("price"))));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private void resetAll(Query newQuery) {
            final ProgressDialog progressDialog = new ProgressDialog(ShowSalesStatusActivity.this);
            progressDialog.setTitle("loading...");
            progressDialog.show();
            list.clear();
            sellDay = 0;
            newQuery
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot dc : task.getResult()) {
                                    Order order = dc.toObject(Order.class);
                                    List<Map<String, Object>> orderList = order.getOrder_list();
                                    for (Map<String, Object> _map : orderList) {
                                        sellDay += (long) (_map.get("num")) * (long) (_map.get("price"));
                                        int i;
                                        for (i = 0; i < list.size(); ++i) {
                                            //   Log.d("name", _map.get("name").toString());
                                            if (list.get(i).containsValue(_map.get("name").toString())) {
                                                long soldNum = (long) (list.get(i).get("num"));
                                                list.get(i).put("num", soldNum + (long) _map.get("num"));
                                                break;
                                            }
                                        }
                                        if (i == list.size()) {
                                            list.add(_map);
                                            //  Log.d("added", list.get(list.size() - 1).get("name").toString());
                                        }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                salesDay.setText(Long.toString(sellDay));
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_SHORT).show();
                                Log.d("ShowSalesStatusActivity", "Error getting documents: ", task.getException());
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private class salesViewHolder extends RecyclerView.ViewHolder {
        TextView salesNameView;
        TextView salesPriceView;
        TextView salesNumView;
        TextView salesSumView;

        private salesViewHolder(@NonNull View itemView) {
            super(itemView);
            salesNameView = (TextView) itemView.findViewById(R.id.sales_field_name);
            salesPriceView = (TextView) itemView.findViewById(R.id.sales_field_price);
            salesNumView = (TextView) itemView.findViewById(R.id.sales_field_num);
            salesSumView = (TextView) itemView.findViewById(R.id.sales_field_sum);

        }
    }
}