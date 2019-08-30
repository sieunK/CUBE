package com.example.cube.Administrator.Order;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Administrator.AdminActivity;
import com.example.cube.Components.Order;
import com.example.cube.DeleteDialogFragment;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderListFragment extends Fragment {
    @Nullable
    private Bundle savedInstanceState;
    private String TAG = "OrderListFragment";

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY
            ="AAAAvvzO9hU:APA91bFQcNh5jlRTJkH6Yk3Vy8YG0pEB_2Al1s3qv8hqI0e3GTwa032nJhpmS0Y45TFxgQYN_uWCS6shUe5oLSo6yhtonYlAvylOU3lMGdJ9l-uaYQM2HprfFdR7GoqD3oR-ji48h3LQ";
    private FirebaseFirestore mStore;
    private Query setQuery;
    private CollectionReference collectionRef;

    private Calendar cal;

    private DatePickerDialog dateDialog;
    private TextView datePickerTextView;
    private TextView goYesterday;
    private TextView goTomorrow;
    private Button showFinished;
    private Button showOrder;
    private boolean finishOrNot;

    private RecyclerView orderRecylcerView;
    private OrderAdapter mAdapter;

    private CheckBox selectAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

// FRAGMENT CYCLE

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list_fragment, null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.order_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //  setHasOptionsMenu(true);

        selectAll = (CheckBox) view.findViewById(R.id.order_all_checkbox);
        goYesterday = view.findViewById(R.id.order_date_picker_yesterday);
        goTomorrow = view.findViewById(R.id.order_date_picker_tomorrow);
        showOrder = view.findViewById(R.id.order_show);
        showFinished = view.findViewById(R.id.order_show_finish);
        finishOrNot = false;

        mStore = FirebaseFirestore.getInstance();
        collectionRef = mStore.collection("foodcourt/moonchang/order");
        cal = Calendar.getInstance(Locale.KOREA);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        datePickerTextView = (TextView) view.findViewById(R.id.order_date_picker_text);
        datePickerTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dateDialog = new DatePickerDialog(getContext(), dateSetListener,
                        cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DAY_OF_MONTH));
                dateDialog.show();
            }
        });

        orderRecylcerView = (RecyclerView) view.findViewById(R.id.order_recycler_view);
        orderRecylcerView.setHasFixedSize(true);
        orderRecylcerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecylcerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));


        selectAll.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             mAdapter.setAllChecked(selectAll.isChecked());
                                             mAdapter.notifyDataSetChanged();
                                         }
                                     }
        );

        goYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(cal.DATE, -1);
                int Y = cal.get(cal.YEAR);
                int M = cal.get(cal.MONTH);
                int yD = cal.get(cal.DAY_OF_MONTH);
                sendQuery(Y, M, yD);

            }
        });

        goTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(cal.DATE, 1);
                int Y = cal.get(cal.YEAR);
                int M = cal.get(cal.MONTH);
                int tD = cal.get(cal.DAY_OF_MONTH);
                sendQuery(Y, M, tD);
            }
        });
        showFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!finishOrNot) {
                    Date beginTime = new Date(cal.getTimeInMillis());
                    Timestamp getDayStart = new Timestamp(beginTime);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date endTime = new Date(cal.getTimeInMillis());
                    Timestamp getDayLast = new Timestamp(endTime);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    Query query = collectionRef
                            .whereEqualTo("standby", false)
                            .whereGreaterThanOrEqualTo("order_time", getDayStart)
                            .whereLessThan("order_time", getDayLast)
                            .orderBy("order_time", Query.Direction.ASCENDING);
                    mAdapter.resetAll(query);
                    mAdapter.notifyDataSetChanged();
                    finishOrNot = true;
                } else return;
            }
        });

        showOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finishOrNot) {
                    int Y = cal.get(cal.YEAR);
                    int M = cal.get(cal.MONTH);
                    int D = cal.get(cal.DAY_OF_MONTH);
                    sendQuery(Y, M, D);
                    finishOrNot = false;
                } else return;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 오늘 0시 0분 0초
        Date today = new Date(cal.getTimeInMillis());
        Timestamp todayStart = new Timestamp(today);
        cal.add(Calendar.DATE, 1);
        // 내일 0시 0분 0초
        Date tomorrow = new Date(cal.getTimeInMillis());
        Timestamp tomorrowStart = new Timestamp(tomorrow);
        cal.add(Calendar.DATE, -1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        datePickerTextView.setText(simpleDateFormat.format(today));

        setQuery = collectionRef
                .whereEqualTo("standby", true)
                .whereGreaterThanOrEqualTo("order_time", todayStart)
                .whereLessThan("order_time", tomorrowStart)
                .orderBy("order_time", Query.Direction.ASCENDING);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("loading...");
        progressDialog.show();

        mAdapter = new OrderAdapter(setQuery);
        orderRecylcerView.setAdapter(mAdapter);
        orderRecylcerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), orderRecylcerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d("onitemclick", "clicked");
                                mAdapter.setChecked(position);
                                mAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Log.d(this.getClass().getName(), "long push");

                                //   String user_name = mAdapter.mOrderList.get(position).getUser();
                                //   int order_num = mAdapter.mOrderList.get(position).getOrder_num();
                                String orderId = mAdapter.mOrderIds.get(position);
                                Order orderChosen = mAdapter.mOrderList.get(position);
                                callOrder(orderChosen, orderId, position);
                            }
                        }));
        progressDialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }

    private void callOrder(Order order, String orderId, final int pos) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        String orderList_String = "";
        int totalPrice = 0;

        List<Map<String, Object>> orderList = order.getOrder_list();
        for (Map<String, Object> _map : orderList) {
            orderList_String += (_map.get("name") + " " + _map.get("num") + "\t\t" + _map.get("price") + "원\n");
            totalPrice += (long) (_map.get("price")) * (long) (_map.get("num"));

        }

        final String ORDERLIST =orderList_String;
        final String NICKNAME = order.getUser();
        final int ORDERNUM = order.getOrder_num();
        final String ORDERID = orderId;
        final String TIME = simpleDateFormat.format(order.getOrder_time());
        final boolean STANDBY = order.isStandby();
        final boolean ISCALLED = order.isCalled();

        CallOrderDialogFragment codf =
                CallOrderDialogFragment.newInstance(new CallOrderDialogFragment.CallOrderListener() {
                    @Override
                    public void callUser(final String nickName) {
                        if (ISCALLED) {
                            Toast.makeText(getActivity(), "이미 호출하였습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            ;
                            mStore.collection("users").whereEqualTo("username", nickName)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot qs) {
                                    String userLoginToken = qs.getDocuments().get(0)
                                            .getString("token");
                                    Log.d("TOKEN get", userLoginToken);
                                    sendPostToFCM(nickName +"님, 주문하신 음식이 완료되었습니다!\n"+ORDERLIST,
                                            userLoginToken);

                                    //--------------------------------------------------------------//
//                                    FirebaseInstanceId.getInstance().getInstanceId()
//                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                                                    if (!task.isSuccessful()) {
//                                                        Log.w(TAG, "getInstanceId failed", task.getException());
//                                                        return;
//                                                    }
//
//                                                    // Get new Instance ID token
//                                                    String token = task.getResult().getToken();
//
//                                                    // Log and toast
//                                                    Log.d(TAG, token);
//                                                    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                    //--------------------------------------------------------------//
                                    //  Toast.makeText(getActivity(), nickName + " 조회 성공!\r "
                                    //          + user_email + "알림 설정은 나중에!", Toast.LENGTH_SHORT).show();

                                    // orderRecylcerView.getChildViewHolder(view).itemView.setBackgroundColor(Color.LTGRAY);

                                    collectionRef.document(ORDERID)
                                            .update("called", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("CALL", "Success " + pos);
                                                orderRecylcerView.getChildAt(pos).setBackgroundColor((Color.argb(100, 21, 158, 26)));
                                                mAdapter.notifyItemChanged(pos);
                                            } else
                                                Log.d("CALL", "Fail");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(getActivity().toString(), "주문자 조회 실패!");
                                    Toast.makeText(getActivity(), nickName + "주문자 조회 실패!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }, NICKNAME, ORDERNUM, ORDERLIST, TIME, STANDBY, ISCALLED, totalPrice);

        codf.setCancelable(true);
        codf.show(getFragmentManager(), CallOrderDialogFragment.TAG);
    }


    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int Y, int M, int D) {
            sendQuery(Y, M, D);
        }
    };

    private void sendQuery(int Y, int M, int D) {
        cal.set(Y, M, D);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date beginTime = new Date(cal.getTimeInMillis());
        Timestamp getDayStart = new Timestamp(beginTime);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endTime = new Date(cal.getTimeInMillis());
        Timestamp getDayLast = new Timestamp(endTime);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Log.d("send Time", beginTime.toString() + " " + endTime.toString());
        Query query = collectionRef
                .whereEqualTo("standby", true)
                .whereGreaterThanOrEqualTo("order_time", getDayStart)
                .whereLessThan("order_time", getDayLast)
                .orderBy("order_time", Query.Direction.ASCENDING);

        mAdapter.resetAll(query);
        mAdapter.notifyDataSetChanged();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        datePickerTextView.setText(simpleDateFormat.format(beginTime));
    }

    //  주 문 리 스 트 (RECYCLERVIEW) - 아 이 템 관 리
    //  기본 어댑터와 테이블 형태 어댑터 클래스를 따로 파일로 분리

    /* 각 ITEM CLICK 이벤트 */

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private RecyclerItemClickListener.OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, RecyclerItemClickListener.OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        Log.d("short", "press");
                        mListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                    return true;
                }


                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        Log.d("long", "press");
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                Log.d("Intercept", "press");
                //   mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    /* 각 ITEM CLICK 이벤트 */


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.order_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sales_status) {
            Intent intent = new Intent(getContext(), ShowSalesStatusActivity.class);
            startActivity(intent);


        } else if (id == R.id.order_delete) {
            final int deleteSize = mAdapter.mOrderChecked.length;
            int checkedNum = 0;
            for (int i = 0; i < deleteSize; ++i) {
                if (mAdapter.mOrderChecked[i]) checkedNum++;
            }
            if (checkedNum == 0) {
                Toast.makeText(getActivity(),
                        "삭제할 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
            Toast.makeText(getActivity(), "해당 내역을 영구 삭제하게 됩니다", Toast.LENGTH_SHORT).show();
            DeleteDialogFragment ddf =
                    DeleteDialogFragment.newInstance(new DeleteDialogFragment.DeleteOrderListener() {
                        @Override
                        public void DeleteOrNot(int IsDeleted) {
                            if (IsDeleted == 1) {
                                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                progressDialog.setTitle("deleting...");
                                progressDialog.show();

                                Log.d("orderList", "order delete");
                                int i;
                                for (i = 0; i < deleteSize; ++i) {
                                    if (mAdapter.mOrderChecked[i]) {
                                        final int INDEX = i;
                                        collectionRef.document(mAdapter.mOrderIds.get(i)).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mAdapter.mOrderIds.remove(INDEX);
                                                        mAdapter.mOrderList.remove(INDEX);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText((AdminActivity) getActivity(),
                                                        "삭제 도중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                return;
                                            }
                                        });
                                    }
                                }
                                progressDialog.dismiss();

                            } else
                                Log.d("orderList", "IsDeleted==0");
                        }
                    });
            ddf.show(getFragmentManager(), DeleteDialogFragment.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendPostToFCM(final String message, final String userLoginToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("TOKEN is ", userLoginToken);
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", message);
                    notification.put("title", getString(R.string.app_name));
                    root.put("notification", notification);
                    root.put("to", userLoginToken);

                    // FMC 메시지 생성 end

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
