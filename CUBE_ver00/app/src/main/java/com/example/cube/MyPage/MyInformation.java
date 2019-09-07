package com.example.cube.MyPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cube.Components.Order;
import com.example.cube.CurrentApplication;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MyInformation extends Fragment {
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;


    Button changePicture;
    Button transferPoint;
    Button chargePoint;
    ImageView profilePicture;
    private Uri mImageCaptureUri;
    private String absolutePath;
    private String profileImage;

    RecyclerView myCurrentOrderView;
    ArrayList<Order> myCurrentOrderList;
    CurrentOrderAdapter adapter;
    FirebaseFirestore mStore;

    public MyInformation() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        changePicture = view.findViewById(R.id.button_changepic);
        transferPoint = view.findViewById(R.id.button_transferpoint);
        chargePoint = view.findViewById(R.id.button_chargepoint);
        profilePicture = view.findViewById(R.id.image_profile);

        /* 저장된 이미지가 있으면 로드하고 아니면 기본이미지 */
        CurrentApplication ca = (CurrentApplication) (getActivity().getApplicationContext());
        profileImage = ca.getProfileImage();
        if( profileImage != null && profileImage != "null") {
            byte[] decodedByteArray = Base64.decode(profileImage, Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            profilePicture.setImageBitmap(decodedBitmap);
        }

        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                DialogInterface.OnClickListener defaultListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_tab_select_info);
                        profilePicture.setImageDrawable(drawable);

//                        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(),R.drawable.ic_tab_select_info);
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
//                        String profile = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                        final Map<String, Object> post = new HashMap<>();
                        post.put("profile", "null");

                        CurrentApplication ca = (CurrentApplication) (getActivity().getApplicationContext());
                        ca.setProfileImage("null");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .whereEqualTo("username", ca.getNickname()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                            DocumentReference dr = ds.getReference();
                                            dr.set(post, SetOptions.merge());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        dialog.dismiss();

                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("업로드할 이미지 선택")
                        .setPositiveButton("앨범선택", albumListener)
                        .setNegativeButton("기본사진으로 설정", defaultListener)
                        .setNeutralButton("취소",cancelListener)
                        .show();
            }
        });

        CurrentApplication currentUserInfo = (CurrentApplication) (getActivity().getApplication());
        String UserNickName = currentUserInfo.getNickname();
        myCurrentOrderView = view.findViewById(R.id.recycler_my_order_current);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        myCurrentOrderView.setLayoutManager(linearLayoutManager);
        myCurrentOrderView.setHasFixedSize(true);
        myCurrentOrderView.addItemDecoration(new DividerItemDecoration(myCurrentOrderView.getContext(), 1));


        myCurrentOrderList = new ArrayList<>();
        FragmentManager fm = getFragmentManager();

        mStore = FirebaseFirestore.getInstance();
        mStore.collection("foodcourt/moonchang/order")
                .whereEqualTo("user", UserNickName).whereEqualTo("standby", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot ds : task.getResult()) {
                        Order order = ds.toObject(Order.class);
                        myCurrentOrderList.add(order);
                    }
                    FragmentManager fm = getFragmentManager();
                    adapter = new CurrentOrderAdapter(myCurrentOrderList, fm);
                    myCurrentOrderView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "불러오기 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE: {
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/BNU/" + System.currentTimeMillis() + ".jpg";

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    profilePicture.setImageBitmap(photo);

                    storeCropImage(photo, filePath);
                    absolutePath = filePath;
                    break;
                }

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }

    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BNU";
        File directory_BNU = new File(dirPath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String profile = Base64.encodeToString(imageBytes, Base64.NO_WRAP);


        final Map<String, Object> post = new HashMap<>();
        post.put("profile", profile);

        CurrentApplication ca = (CurrentApplication) (getActivity().getApplicationContext());
        ca.setProfileImage(profile);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("username", ca.getNickname()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                            DocumentReference dr = ds.getReference();
                            dr.set(post, SetOptions.merge());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /* 로컬저장소 염 */
//        SharedPreferences sf = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
//        SharedPreferences.Editor et = sf.edit();
//        et.clear(); // 저장전에 한번 클리어함

        /* Bitmap을 String으로 바꿔서 저장 */
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] imageBytes = byteArrayOutputStream.toByteArray();
//        et.putString("Image",Base64.encodeToString(imageBytes, Base64.NO_WRAP));
//        et.commit();

        if (!directory_BNU.exists())
            directory_BNU.mkdir();
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile((copyFile))));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ItemViewHolder> {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY년 MM월 dd일 HH:mm");
        private FragmentManager fm;

        // adapter에 들어갈 list 입니다.
        private ArrayList<Order> listData;

        public CurrentOrderAdapter(ArrayList<Order> orderList, FragmentManager fragmentManager) {
            listData = orderList;
            fm = fragmentManager;
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order, parent, false);
            return new ItemViewHolder(view);
        }

        // RecyclerView의 핵심인 ViewHolder 입니다.
        // 여기서 subView를 setting 해줍니다.
        class ItemViewHolder extends RecyclerView.ViewHolder {

            private TextView curOrderInfo;
            private TextView curOrderList;
            private TextView curOrderState;
            private LinearLayout Item;
            private int pos;

            ItemViewHolder(View itemView) {
                super(itemView);

                curOrderInfo = itemView.findViewById(R.id.cur_order_info);
                curOrderList = itemView.findViewById(R.id.cur_order_food);
                curOrderState = itemView.findViewById(R.id.cur_order_state);
                Item = itemView.findViewById(R.id.cur_order_itemView);

            }

            void onBind(Order data, int position) {

                pos = position;

                // order collection은 식당별로 있기도하고 일단 문창만 주문을 했을 때 리뷰를 쓰는 것으로 하였음.
                String orderInfoStr = "";
                orderInfoStr += (data.getOrder_num() + " / 문창 / ");
                orderInfoStr += simpleDateFormat.format(data.getOrder_time());

                List<Map<String, Object>> orderedList = data.getOrder_list();
                String orderDetailStr = "";
                Map<String, Object> _map = orderedList.get(0);
                orderDetailStr += (_map.get("name") + "X" + _map.get("num") + "외" + (orderedList.size() - 1) + "개");

                curOrderInfo.setText(orderInfoStr);
                curOrderList.setText(orderDetailStr);
                if (data.isCalled()) {
                    curOrderState.setTextColor((Color.argb(100, 255, 193, 7)));
                    curOrderState.setText("호출됨");
                }

                Item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "상세보기", Toast.LENGTH_SHORT).show();
                        Order order = listData.get(pos);
                        OrderDetailDialogFragment oddf
                                = new OrderDetailDialogFragment(order);
                        oddf.show(fm, OrderDetailDialogFragment.TAG);
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.

            holder.onBind(listData.get(position), position);
        }

        @Override
        public int getItemCount() {
            // RecyclerView의 총 개수 입니다.
            return listData.size();
        }

        void addItem(Order data) {
            // 외부에서 item을 추가시킬 함수입니다.
            listData.add(data);
        }


    }
}