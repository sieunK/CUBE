package com.example.cube.Notice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cube.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoticeAddActivity extends AppCompatActivity {

    private String collectionPath;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private boolean readStoragePermission;

    private EditText mWriteTitle;
    private EditText mWriteContent;
    private FloatingActionButton mWriteButton;
    private LinearLayout pictureLayout;

    private int mImgNums;
    private ArrayList<Bitmap> mImgIds;
    private ArrayList<Uri> mImgUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readStoragePermission = true;
        }
        readStoragePermission = true;
        if (readStoragePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }

        collectionPath = "foodcourt/moonchang/board";
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mWriteTitle = (EditText) findViewById(R.id.add_post_title);
        mWriteContent = (EditText) findViewById(R.id.add_post_content);
        mWriteButton = (FloatingActionButton)findViewById(R.id.upload_button);
        mWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) return;

                String DocId = mStore.collection(collectionPath).document().getId();
                Date DocDate = new Date();

                final ProgressDialog progressDialog = new ProgressDialog(NoticeAddActivity.this);
                progressDialog.setTitle("uploading...");
                progressDialog.show();

                String picFolderName = DocId + "_images/";
                Log.d("The Number Of Image Is", Integer.toString(mImgIds.size()));

                for (int i = 0; i < mImgIds.size(); i++) {
                    final int numImg = i;
                    String fileName = i + ".png";
                    StorageReference storageRef = mStorage.getReferenceFromUrl("gs://bobnu-47135.appspot.com").
                            child(picFolderName + fileName);

                    storageRef.putFile(mImgUris.get(i))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("image number", Integer.toString(numImg));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                }

                Map<String, Object> post = new HashMap<>();
                post.put("id", DocId);
                post.put("date", new Timestamp(DocDate));
                post.put("title", mWriteTitle.getText().toString());
                post.put("content", mWriteContent.getText().toString());
                post.put("numClicks", 0);
                post.put("numComments", 0);
                post.put("filenum", mImgIds.size());
                mStore.collection(collectionPath).document(DocId).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                finish();
                            }
                        });
            }
        });

        mImgIds = new ArrayList<Bitmap>();
        mImgUris = new ArrayList<Uri>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        pictureLayout = (LinearLayout) findViewById(R.id.add_post_pictures);
        LayoutInflater picInflater = LayoutInflater.from(this);


        if (mImgIds.size() >0) {
            View view = picInflater.inflate(R.layout.picture_item,
                    pictureLayout, false);
            ImageView img = (ImageView) view
                    .findViewById(R.id.id_index_picture_item);
            img.setImageBitmap(mImgIds.get(mImgIds.size() - 1));
            pictureLayout.addView(view);
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mWriteTitle.getText().toString())) {
            mWriteTitle.setError("Required");
            result = false;
        } else {
            mWriteTitle.setError(null);
        }
        if (TextUtils.isEmpty(mWriteContent.getText().toString())) {
            mWriteContent.setError("Required");
            result = false;
        } else {
            mWriteContent.setError(null);
        }
        return result;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_notice, menu) ;
        return true ;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_pic_button: {
                Toast.makeText(this, "사진 가져오기", Toast.LENGTH_SHORT).show();
                // 클릭해서 갤러리에서 사진 불러오기
                if (readStoragePermission) { // 갤러리앱의 목록화면을 인텐트로 띄우자
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 10); // 목록화면에서 작업하고 되돌아왔을 때 사후처리해야하므로 forResult씀.
                } else {
                    Toast t = Toast.makeText(getApplication(), "허가되지않음", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
                //options.inJustDecodeBounds =true;
                //  options.inSampleSize=10;
                options.inSampleSize = calculateInSampleSize(options,100,100);
                Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(0), options ); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.
                //            byte[] bytestream=null;
                if(bitmap!=null){
                    mImgUris.add(data.getData());
                    mImgIds.add(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 200, stream);
                    //                  bytestream= stream.toByteArray();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
