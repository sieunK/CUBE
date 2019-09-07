package com.example.cube.Administrator.Menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cube.Components.Menu;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MenuAddActivity extends AppCompatActivity {
    private ImageView foodPhotoView;
    private EditText foodNameEdit;
    private EditText foodPriceEdit;
    private AppCompatSpinner foodTypeEdit;
    private EditText foodInfoEdit;
    private Button addBtn;
    private FirebaseFirestore mStore;
    private boolean readStoragePermission;
    private int nextMenuNum;

    private ArrayList<String> menuTypes;
    private ArrayAdapter<String> menuTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            readStoragePermission =true;
        }
        if(!readStoragePermission){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},10);
        }

        mStore = FirebaseFirestore.getInstance();

        foodPhotoView = (ImageView) findViewById(R.id.add_item_photo);
        foodNameEdit = (EditText) findViewById(R.id.add_item_name);
        foodPriceEdit = (EditText) findViewById(R.id.add_item_price);
        foodTypeEdit = findViewById(R.id.add_item_type);
        foodInfoEdit = findViewById(R.id.add_item_info);
        addBtn = (Button) findViewById(R.id.add_item_btn);

        menuTypes = new ArrayList<>();
        menuTypes.add("한식");
        menuTypes.add("일식");
        menuTypes.add("양식");
        menuTypeAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                menuTypes);
        foodTypeEdit.setAdapter(menuTypeAdapter);


        // 이번에 메뉴 추가 시 들어갈 메뉴번호 가져옴
        DocumentReference MoonChangRef = mStore.collection("foodcourt").document("moonchang");
        MoonChangRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot dc = task.getResult();
                            if (dc.exists()) {
                                nextMenuNum = (int) (long) dc.get("menu_num")+1;
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



        foodPhotoView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(readStoragePermission){ // 갤러리앱의 목록화면을 인텐트로 띄우자
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 10); // 목록화면에서 작업하고 되돌아왔을 때 사후처리해야하므로 forResult씀.
                } else {
                    Toast.makeText( getApplicationContext(), "허가되지않음", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmapPhoto = ((BitmapDrawable) (foodPhotoView.getDrawable())).getBitmap();
                int id = nextMenuNum;
                String photo = bitmapToString(bitmapPhoto);
                String name = foodNameEdit.getText().toString();
                String type = foodTypeEdit.getSelectedItem().toString();
                String info = foodInfoEdit.getText().toString();
                int price = Integer.parseInt(foodPriceEdit.getText().toString());
                Menu menu = new Menu(id, photo, name, price, false, type, info);
                mStore.collection("foodcourt/moonchang/menu").document().set(menu);
                Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MenuConfigActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // requestCode:intent정보, resultcode: 결과를 되돌릴때상태코드값 정도
        if(requestCode==10 && resultCode==RESULT_OK){
            try{
                String[] projection = {MediaStore.Images.Media.DATA}; //선택한 사진의 식별자 만 넘김, 갤러리앱의 content provider 필요
                Cursor cursor = getContentResolver().query(data.getData(), projection, null , null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                cursor.moveToFirst();
                BitmapFactory.Options options = new BitmapFactory.Options();    //data 뽑아내기
                options.inSampleSize=1;
                Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(0), options ); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.
                float bmpWidth = bitmap.getWidth();
                float bmpHeight = bitmap.getHeight();
                if (bmpWidth > 200) {
                    float mWidth = bmpWidth / 100;
                    float scale = 200/ mWidth;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                } else if (bmpHeight > 120) {
                    float mHeight = bmpHeight / 100;
                    float scale = 120/ mHeight;
                    bmpWidth *= (scale / 100);
                    bmpHeight *= (scale / 100);
                }
                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
                if(bitmap!=null){
                    foodPhotoView.setImageBitmap(resizedBmp);

                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10 && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                readStoragePermission=true;
            }
        }
    }
    public static String bitmapToString(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = stream.toByteArray();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byteArray= stream.toByteArray();
        return Base64.encodeToString(byteArray,Base64.NO_WRAP);
    }

}
