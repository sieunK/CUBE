package com.example.cube.Administrator.Menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.Menu;
import com.example.cube.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MenuConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore;
    private RecyclerView menuRecyclerView;
    private Query MenuQuery;
    private MenuAdapter mAdapter;
    private Button foodAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_config);

        mStore = FirebaseFirestore.getInstance();
        menuRecyclerView = (RecyclerView) findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setHasFixedSize(true);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foodAddBtn = (Button) findViewById(R.id.food_add_btn);
        foodAddBtn.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        MenuQuery = mStore.collection("foodcourt/moonchang/menu").orderBy("id", Query.Direction.DESCENDING);
        mAdapter = new MenuAdapter(MenuQuery);
        menuRecyclerView.setAdapter(mAdapter);
        //    menuRecyclerView.addItemDecoration(new DividerItemDecoration(menuRecyclerView.getContext(), 1)); // 목록 간 구분선
        //    menuRecyclerView.addItemDecoration(new DividerItemDecoration(menuRecyclerView.getContext(), 0)); // 목록 간 구분선
        menuRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), menuRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //     Log.d(this.getClass().getName(),"push");
                                Toast.makeText(getApplicationContext(), "메뉴 정보", Toast.LENGTH_SHORT).show();
                                String DocId = mAdapter.mMenuIds.get(position);

                                Log.d("position and id", position + "  and  " + DocId);
                                MenuInfoDialogFragment fidf =
                                        MenuInfoDialogFragment.newInstance(new MenuInfoDialogFragment.FoodInfoListener() {
                                            @Override
                                            public void saveFoodInfo(Menu food, String DocId) {
                                                DocumentReference foodRef = mStore.collection("foodcourt/moonchang/menu").document(DocId);
                                                foodRef.update("photo", food.getPhoto());
                                                foodRef.update("name", food.getName());
                                                foodRef.update("price", food.getPrice());
                                                foodRef.update("info", food.getInfo());
                                                foodRef.update("is_soldout", food.getIs_soldout());
                                            }

                                            @Override
                                            public void deleteFoodInfo(String DocId) {
                                                mStore.collection("foodcourt/moonchang/menu").document(DocId).delete();
                                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }, DocId);
                                fidf.show(getSupportFragmentManager(), MenuInfoDialogFragment.TAG);

                            }

                        })
        );
        progressDialog.dismiss();
    }


    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }


    @Override
    public void onClick(View v) {
        if (v == foodAddBtn) {
            Toast.makeText(getApplicationContext(), "메뉴 작성 후 추가하십시오", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MenuAddActivity.class));
            finish();
        }
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoView;
        private TextView mNameTextView;
        private TextView mPriceTextView;
        private TextView mIsSoldOut;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhotoView = itemView.findViewById(R.id.item_photo);
            mNameTextView = itemView.findViewById(R.id.item_name_text);
            mPriceTextView = itemView.findViewById(R.id.item_price_text);
            mIsSoldOut = itemView.findViewById(R.id.item_sold_out_text);
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {
        private List<String> mMenuIds = new ArrayList<>();
        private List<Menu> mMenuList = new ArrayList<>();
        private ListenerRegistration listenerRegistration;

        public MenuAdapter(Query query) {
            EventListener eventListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    String menuKey;
                    int menuIndex;
                    Menu menu;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                // A new comment has been added, add it to the displayed list
                                menu = dc.getDocument().toObject(Menu.class);
                                // Update RecyclerView
                                mMenuIds.add(0, dc.getDocument().getId());
                                mMenuList.add(0, menu);
                                notifyItemInserted(0);
                                Log.d("Id", mMenuIds.get(0));
                                break;
                            case MODIFIED:
                                // A comment has changed, use the key to determine if we are displaying this
                                // comment and if so displayed the changed comment.
                                menu = dc.getDocument().toObject(Menu.class);
                                menuKey = dc.getDocument().getId();
                                menuIndex = mMenuIds.indexOf(menuKey);
                                if (menuIndex > -1) {
                                    // Replace with the new data
                                    mMenuList.set(menuIndex, menu);
                                    // Update the RecyclerView
                                    notifyItemChanged(menuIndex);
                                } else {
                                    Log.w(getApplicationContext().toString(), "Menu Changed" + menuKey);
                                }
                                break;
                            case REMOVED:
                                // A comment has changed, use the key to determine if we are displaying this
                                // comment and if so remove it.
                                menuKey = dc.getDocument().getId();

                                menuIndex = mMenuIds.indexOf(menuKey);
                                if (menuIndex > -1) {
                                    // Remove data from the list
                                    mMenuIds.remove(menuIndex);
                                    mMenuList.remove(menuIndex);

                                    // Update the RecyclerView
                                    notifyItemRemoved(menuIndex);
                                } else {
                                    Log.w(getApplicationContext().toString(), "onChildRemoved:unknown_child:" + menuKey);
                                }
                                break;
                        }
                    }
                }
            };
            listenerRegistration = query.addSnapshotListener(eventListener);
        }


        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new MenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int pos) {
            Menu data = mMenuList.get(pos);
            String photoStr = data.getPhoto();
            if (photoStr == null) {
                holder.mPhotoView.setImageResource(R.drawable.ic_local_dining_black_24dp);
            } else {
                byte[] photo = Base64.decode(photoStr, Base64.NO_WRAP);
                holder.mPhotoView.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
            }
            holder.mNameTextView.setText(data.getName());
            holder.mPriceTextView.setText(Integer.toString(data.getPrice()) + " 원");
            boolean isSoldOut = data.getIs_soldout();
            if (!isSoldOut)
                holder.mIsSoldOut.setVisibility(View.INVISIBLE);
            else holder.mIsSoldOut.setVisibility(View.VISIBLE);

        }

        @Override
        public int getItemCount() {
            return mMenuList.size();
        }

        public void cleanupListener() {
            listenerRegistration.remove();
        }

    }

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
               /*     View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        Log.d("long", "press");
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }*/
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
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

}

