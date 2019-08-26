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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class OrderListFragment extends Fragment implements View.OnClickListener {
    @Nullable
    private Bundle savedInstanceState;
    private FirebaseFirestore mStore;

    private DatePickerDialog dateDialog;
    private String TAG = "OrderListFragment";
    private Query setQuery;
    private CollectionReference collectionRef;
    private TextView datePickerTextView;
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
        View view = inflater.inflate(R .layout.order_list_fragment, null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.order_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        mStore = FirebaseFirestore.getInstance();
        collectionRef = mStore.collection("foodcourt/moonchang/order");
        setQuery = collectionRef
                .orderBy("order_time", Query.Direction.ASCENDING);

       /* dateDialog = (DatePicker)view.findViewById(R.id.order_date_picker);
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int Y, int M, int D) {
                        sendQuery(Y, M, D);
                    }
                });*/

        datePickerTextView = (TextView) view.findViewById(R.id.order_date_picker_text);
        datePickerTextView.setOnClickListener(this);

        selectAll = (CheckBox) view.findViewById(R.id.order_all_checkbox);

        orderRecylcerView = (RecyclerView) view.findViewById(R.id.order_recycler_view);
        orderRecylcerView.setHasFixedSize(true);
        orderRecylcerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("loading...");
        progressDialog.show();

        datePickerTextView.setText("날짜를 선택하세요");

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
                                callOrder(orderChosen, orderId, view);
                            }
                        }));
        progressDialog.dismiss();

        selectAll.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             mAdapter.setAllChecked(selectAll.isChecked());
                                             mAdapter.notifyDataSetChanged();
                                         }
                                     }
        );

        return view;
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }

//

    private void callOrder(Order order, String orderId, final View view) {
        String orderList_String = "";

        List<Map<String, Object>> orderList = order.getOrder_list();
        for (Map<String, Object> _map : orderList) {
            orderList_String += (_map.get("name") + " " + _map.get("num") + "\n");
        }

        final String nickName = order.getUser();
        final int orderNum = order.getOrder_num();
        final String orderID = orderId;
        final boolean isCalled = order.isCalled();
        CallOrderDialogFragment codf =
                CallOrderDialogFragment.newInstance(new CallOrderDialogFragment.CallOrderListener() {
                    @Override
                    public void callUser(final String nickName) {
                        if (isCalled) {
                            Toast.makeText(getActivity(), "이미 호출하였습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            mStore.collection("users").whereEqualTo("username", nickName)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot qs) {
                                    String user_email = qs.getDocuments().get(0)
                                            .getString("email");
                                    //--------------------------------------------------------------//
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                                        return;
                                                    }

                                                    // Get new Instance ID token
                                                    String token = task.getResult().getToken();

                                                    // Log and toast
                                                    Log.d(TAG, token);
                                                    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    //--------------------------------------------------------------//
                                  //  Toast.makeText(getActivity(), nickName + " 조회 성공!\r "
                                  //          + user_email + "알림 설정은 나중에!", Toast.LENGTH_SHORT).show();

                                    // orderRecylcerView.getChildViewHolder(view).itemView.setBackgroundColor(Color.LTGRAY);
                                    collectionRef.document(orderID)
                                            .update("called", true);
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
                }, nickName, orderNum, orderList_String);

        codf.setCancelable(true);
        codf.show(getFragmentManager(), CallOrderDialogFragment.TAG);
    }


    @Override
    public void onClick(View v) {
        if (v == datePickerTextView) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
            dateDialog = new DatePickerDialog(getContext(), dateSetListener,
                    cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DAY_OF_MONTH));
            dateDialog.show();
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int Y, int M, int D) {
            sendQuery(Y, M, D);
        }
    };


    private void sendQuery(int Y, int M, int D) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
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
        Log.d("send Time", beginTime.toString() + " " + endTime.toString());
        Query query = collectionRef
                .whereGreaterThanOrEqualTo("order_time", getDayStart)
                .whereLessThan("order_time", getDayLast)
                .orderBy("order_time", Query.Direction.ASCENDING);
        mAdapter.resetAll(query);
        mAdapter.notifyDataSetChanged();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 (E)");
        datePickerTextView.setText(simpleDateFormat.format(beginTime));
    }


    //  주 문 리 스 트 (RECYCLERVIEW) - 아 이 템 관 리    //

    /* VIEWHOLDER */

    private class OrderViewHolder extends RecyclerView.ViewHolder {
        private CheckBox mCheckBox;
        private TextView mNumberTextView;
        private TextView mListTextView;
        private TextView mStateTextView;
        private ImageView mCalledImageView;
        private TextView mPriceTextView;
        private TextView mTimeTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.order_checkbox);
            mNumberTextView = itemView.findViewById(R.id.order_item_num);
            mListTextView = itemView.findViewById(R.id.order_item_list);
            mStateTextView = itemView.findViewById(R.id.order_item_state);
            mCalledImageView = itemView.findViewById(R.id.order_item_called);
            mPriceTextView = itemView.findViewById(R.id.order_item_total_price);
            mTimeTextView = itemView.findViewById(R.id.order_item_time);
        }
    }


    /* VIEWHOLDER */


    /* ADAPTER - QUERY 사용 */
    private class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
        private List<String> mOrderIds = new ArrayList<>();
        private List<Order> mOrderList = new ArrayList<>();
        private boolean[] mOrderChecked;

        private ListenerRegistration listenerRegistration;

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
                            // A new comment has been added, add it to the displayed list
                            order = dc.getDocument().toObject(Order.class);
                            // Update RecyclerView
                            mOrderIds.add(0, dc.getDocument().getId());
                            mOrderList.add(0, order);
                            notifyItemInserted(0);
                            Log.d("receive Time", (order.getOrder_time()).toString());
                            Log.d("Id", mOrderIds.get(0));
                            Log.d("size", Integer.toString(mOrderIds.size()));
                            break;

                        case MODIFIED:
                            // A comment has changed, use the key to determine if we are displaying this
                            // comment and if so displayed the changed comment.
                            order = dc.getDocument().toObject(Order.class);
                            orderKey = dc.getDocument().getId();
                            orderIndex = mOrderIds.indexOf(orderKey);
                            if (orderIndex > -1) {
                                // Replace with the new data
                                mOrderList.set(orderIndex, order);

                                // Update the RecyclerView
                                notifyItemChanged(orderIndex);
                            } else {
                                Log.w(getActivity().toString(), "Order Changed" + orderKey);
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
                                Log.w(getActivity().toString(), "onChildRemoved:unknown_child:" + orderKey);
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
            return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
        }

        @Override
        @NonNull
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int pos) {
            Order data = mOrderList.get(pos);

            // 주 문 번 호
            holder.mNumberTextView.setText(Integer.toString(data.getOrder_num()));

            // 체 크 박 스
            holder.mCheckBox.setChecked(mOrderChecked[pos]);
            holder.mCheckBox.setClickable(false);
            holder.mCheckBox.setFocusable(false);

            // 주 문 내 용
            String orderList_String = "";
            int total_Price = 0;
            Log.d("data", Integer.toString(data.getOrder_list().size()));
            List<Map<String, Object>> orderList = data.getOrder_list();
            for (Map<String, Object> _map : orderList) {
                orderList_String += (_map.get("name") + " " + _map.get("num") + "\n");
                total_Price += (long) (_map.get("price")) * (long) (_map.get("num"));
            }
            holder.mListTextView.setText(orderList_String);

            // 대 기 / 완 료
            boolean waitOrNot = data.isStandby();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if (waitOrNot) holder.mStateTextView.setText("대기");
            else {
                holder.mStateTextView.setText("완료");
                holder.mStateTextView.setTextColor(Color.RED);
            }

            // 호 출 여 부
            boolean calledOrNot = data.isCalled();
            if (calledOrNot) holder.mCalledImageView.setVisibility(View.VISIBLE);
            else holder.mCalledImageView.setVisibility(View.INVISIBLE);

            // 가 격
            holder.mPriceTextView.setText(Integer.toString(total_Price) + "원");

            // 주 문 시 간
            holder.mTimeTextView.setText(simpleDateFormat.format(data.getOrder_time()));
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
    }
    /* ADAPTER */


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
        if (id == R.id.sales_status) {  // 임시 데이터 입력 ->  판매현황으로 바꿀 것
              Intent intent = new Intent(getContext(), ShowSalesStatusActivity.class);
              startActivity(intent);


        } else if (id == R.id.order_delete) {
            final int deleteSize = mAdapter.mOrderChecked.length;
            int checkedNum = 0;
            for (int i = 0; i < deleteSize; ++i) {
                if (mAdapter.mOrderChecked[i]) checkedNum++;
            }
            if (checkedNum == 0) {
                Toast.makeText( getActivity(),
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
                                        final int finalI = i;
                                        collectionRef.document(mAdapter.mOrderIds.get(i)).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (finalI == deleteSize - 1) {
                                                            Toast.makeText((AdminActivity) getActivity(),
                                                                    "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText((AdminActivity) getActivity(),
                                                        "삭제 도중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }

                            } else
                                Log.d("orderList", "IsDeleted==0");
                        }
                    });


            ddf.show(getFragmentManager(), DeleteDialogFragment.TAG);

        }

        return super.

                onOptionsItemSelected(item);
    }
}
