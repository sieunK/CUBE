package com.example.cube.Administrator.Menu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.example.cube.Components.Menu;
import com.example.cube.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;


public class MenuInfoDialogFragment
        extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "Food Information Event";

    private Context context;

    private FirebaseFirestore mStore;
    private Boolean readStoragePermission;
    private Boolean isSoldOut;

    private static String DocId;
    private static int foodId;
    private FoodInfoListener listener;


    private TextView foodNum;
    private byte[] photo;
    private ImageView foodPhotoEdit;
    private EditText foodNameEdit;
    private EditText foodPriceEdit;
    private String foodType;
    private EditText foodInfoEdit;
    private Switch soldOutEdit;

    private Button deleteBtn;
    private Button saveBtn;


    public MenuInfoDialogFragment() {
    }

    public static MenuInfoDialogFragment newInstance(FoodInfoListener listener, String documentId) {
        MenuInfoDialogFragment fidf = new MenuInfoDialogFragment();
        fidf.listener = listener;
        DocId = documentId;
        return fidf;
    }

    public interface FoodInfoListener {
        void saveFoodInfo(Menu food, String DocId);

        void deleteFoodInfo(String id);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyAnimation_Window;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.foodinfo_dialog_fragment, container);

        context = getContext();
        readStoragePermission = false;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readStoragePermission = true;
        }
        if (!readStoragePermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }

        mStore = FirebaseFirestore.getInstance();

        foodNum = v.findViewById(R.id.item_num);
        foodPhotoEdit = (ImageView) v.findViewById(R.id.edit_item_photo);
        foodNameEdit = (EditText) v.findViewById(R.id.edit_item_name);
        foodPriceEdit = (EditText) v.findViewById(R.id.edit_item_price);
        foodInfoEdit = (EditText)v.findViewById(R.id.edit_item_info);
        foodInfoEdit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view == foodInfoEdit) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        soldOutEdit = (Switch) v.findViewById(R.id.edit_soldout_switch);

        saveBtn = (Button) v.findViewById(R.id.edit_save_btn);
        deleteBtn = (Button) v.findViewById(R.id.edit_delete_btn);

        foodPhotoEdit.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        // setCancelable(false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(1000, 2000);
        window.setGravity(Gravity.CENTER);
        mStore.collection("foodcourt/moonchang/menu").document(DocId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException e) {
                Menu menu = ds.toObject(Menu.class);

                foodId = menu.getId();
                foodNum.setText(Integer.toString(foodId));
                foodType = menu.getType();

                String photoStr = menu.getPhoto();
                if (photoStr == null) {
                    foodPhotoEdit.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_local_dining_black_24dp));
                } else {
                    photo = Base64.decode(photoStr, Base64.NO_WRAP);
                    foodPhotoEdit.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
                }

                foodNameEdit.setText(menu.getName());
                foodPriceEdit.setText(Integer.toString(menu.getPrice()));
                String infoStr = menu.getInfo();
                if(infoStr==null)
                    foodInfoEdit.setText("");
                else
                    foodInfoEdit.setText(infoStr);

                isSoldOut = menu.getIs_soldout();
                if (isSoldOut) {
                    soldOutEdit.setChecked(true);
                }
            }
        });

        soldOutEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSoldOut = true;
                } else isSoldOut = false;
            }
        });
    }


    @Override
    @NonNull
    public void onClick(View v) {
        if (foodNameEdit.getText().toString().length() <= 0) {//빈값이 넘어올때의 처리
            Toast.makeText((MenuConfigActivity) getActivity(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (foodPriceEdit.getText().toString().length() <= 0) {
            Toast.makeText((MenuConfigActivity) getActivity(), "가격을 입력하세요.", Toast.LENGTH_SHORT).show();

        } else {
            if (v == foodPhotoEdit) {
                if (readStoragePermission) { // 갤러리앱의 목록화면을 인텐트로 띄우자
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 10); // 목록화면에서 작업하고 되돌아왔을 때 사후처리해야하므로 forResult씀.
                } else {
                    Toast.makeText(getActivity(), "허가되지않음", Toast.LENGTH_SHORT).show();
                }
            } else if (v == saveBtn) {
                Menu food = new Menu(foodId,
                        bitmapToString(((BitmapDrawable) (foodPhotoEdit.getDrawable())).getBitmap()),
                        foodNameEdit.getText().toString(),
                        Integer.parseInt(foodPriceEdit.getText().toString()),
                        isSoldOut,
                        foodType,
                        foodInfoEdit.getText().toString()
                );
                listener.saveFoodInfo(food, DocId);
                dismiss();
            } else if (v == deleteBtn) {
                listener.deleteFoodInfo(DocId);
                dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // requestCode:intent정보, resultcode: 결과를 되돌릴때상태코드값 정도
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            try {
                String[] projection = {MediaStore.Images.Media.DATA}; //선택한 사진의 식별자 만 넘김, 갤러리앱의 content provider 필요
                Cursor cursor = getContext().getContentResolver().query(data.getData(), projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                cursor.moveToFirst();
                BitmapFactory.Options options = new BitmapFactory.Options();    //data 뽑아내기
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(0), options); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.
                float bmpWidth = bitmap.getWidth();
                float bmpHeight = bitmap.getHeight();
                if (bmpWidth > 200) {
                    float mWidth = bmpWidth / 100;
                    float scale = 200 / mWidth;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                } else if (bmpHeight > 120) {
                    float mHeight = bmpHeight / 100;
                    float scale = 120 / mHeight;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                }
                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
                if (bitmap != null) {
                    foodPhotoEdit.setImageBitmap(resizedBmp);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String bitmapToString(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = stream.toByteArray();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}


