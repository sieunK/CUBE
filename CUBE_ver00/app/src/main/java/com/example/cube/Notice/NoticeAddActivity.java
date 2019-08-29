package com.example.cube.Notice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeAddActivity extends AppCompatActivity {

    private String collectionPath;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private boolean readStoragePermission;

    private ProgressDialog progressDialog;

    private EditText mWriteTitle;
    private EditText mWriteContent;
    private FloatingActionButton mWriteButton;

    private RecyclerView pictureRecycler;
    private pictureAdapter padapter;

    private ArrayList<Bitmap> mImgIds;
    // private ArrayList<Uri> mImgUris;

    private int uploadedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.write_notice_toolbar);
        setSupportActionBar(toolbar);

        readStoragePermission = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readStoragePermission = true;
        }
        readStoragePermission = true;
        if (readStoragePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }

        progressDialog = new ProgressDialog(NoticeAddActivity.this);
        progressDialog.setMessage("uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);

        collectionPath = "foodcourt/moonchang/board";
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mImgIds = new ArrayList();
        //   mImgUris = new ArrayList();

        pictureRecycler = (RecyclerView) findViewById(R.id.add_post_picture_scroll);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pictureRecycler.setLayoutManager(linearLayoutManager);

        mWriteTitle = (EditText) findViewById(R.id.add_post_title);
        mWriteContent = (EditText) findViewById(R.id.add_post_content);
        mWriteButton = (FloatingActionButton) findViewById(R.id.upload_button);
        mWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) return;

                final String DocId = mStore.collection(collectionPath).document().getId();
                Date DocDate = new Date();

                final String picFolderName = DocId + "_images/";
                Log.d("The Number Of Image Is", Integer.toString(mImgIds.size()));

                final int numberOfImages = mImgIds.size();
                uploadedNum = 0;

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
                                if (numberOfImages == 0)
                                    finish();
                                else {
                                    progressDialog.setMax(numberOfImages);
                                    progressDialog.show();

                                    for (int i = 0; i < numberOfImages; i++) {
                                        final int imageNumber = i;
                                        final String fileName = imageNumber + ".png";

                                        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://bobnu-47135.appspot.com").
                                                child(picFolderName + fileName);

                                        Bitmap bitmap = mImgIds.get(imageNumber);
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        byte[] data = baos.toByteArray();

                                        final UploadTask uploadTask = storageRef.putBytes(data);
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                //   Log.d("image number", Integer.toString(imageNumber));
                                                //   Log.d("About Progress", uploadedNum + " / " + mImgIds.size());
                                                progressDialog.setProgress(++uploadedNum);
                                                if (uploadedNum == numberOfImages) {
                                                    progressDialog.dismiss();
                                                    finish();
                                                }
                                            }
                                        }).addOnCanceledListener(new OnCanceledListener() {
                                                                     @Override
                                                                     public void onCanceled() {
                                                                         Toast.makeText(getApplicationContext(), "글 업로드가 취소되었습니다", Toast.LENGTH_SHORT).show();
                                                                         mStore.collection(collectionPath).document(DocId).delete();
                                                                         finish();
                                                                     }
                                                                 }
                                        );
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "글 업로드 실패!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        padapter = new pictureAdapter(mImgIds);
        pictureRecycler.setAdapter(padapter);
        pictureRecycler.addOnItemTouchListener(new pictureClickListener(getApplicationContext(), pictureRecycler,
                new pictureClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("position", Integer.toString(position));
                        mImgIds.remove(position);
                        //         mImgUris.remove(position);
                        padapter.notifyItemRemoved(position);
                        padapter.notifyItemRangeChanged(position, mImgIds.size());
                    }
                })
        );
    }
/*
    private class UploadFileTask extends AsyncTask<String, String, Long>{
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UploadFileTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            progressDialog.show();
        }



        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) {
            int count;

            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile= new File(path, "Alight.avi"); //파일명까지 포함함 경로의 File 객체 생성

                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float)downloadedSize/FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;
        }


        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();

            if ( size > 0) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" + size.toString(), Toast.LENGTH_LONG).show();

                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);

                playVideo(outputFile.getPath());

            }
            else
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
        }

    }
*/

    private class pictureAdapter extends RecyclerView.Adapter<pictureAdapter.pictureViewHolder> {
        private List<Bitmap> bitmapList;

        public pictureAdapter(List<Bitmap> bitmapList) {
            this.bitmapList = bitmapList;
        }

        @NonNull
        @Override
        public pictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new pictureViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull pictureViewHolder holder, int pos) {
            Bitmap data = bitmapList.get(pos);
            if (data!= null) {
                Glide.with(getApplicationContext()).load(data)
                        .into(holder.picture);
            }
        }

        @Override
        public int getItemCount() {
            return bitmapList.size();
        }

        class pictureViewHolder extends RecyclerView.ViewHolder {
            ImageView picture;

            public pictureViewHolder(@NonNull View itemView) {
                super(itemView);
                picture = itemView.findViewById(R.id.index_picture_item);
            }
        }
    }

    private static class pictureClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public pictureClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    //      View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    //    if (child != null && mListener != null) {
                    //      Log.d("long", "press");
                    //    mListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                    //  }

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
        getMenuInflater().inflate(R.menu.write_notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_pic_button: {
                Toast.makeText(this, "사진 가져오기", Toast.LENGTH_SHORT).show();
                // 클릭해서 갤러리에서 사진 불러오기
                if (readStoragePermission) { // 갤러리앱의 목록화면을 인텐트로 띄우자
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
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

        Log.d("requestCode", Integer.toString(requestCode));
        Log.d("resultCode", Integer.toString(resultCode));

        if (requestCode == 10 && resultCode == RESULT_OK) {
            try {
                String[] projection = {MediaStore.Images.Media.DATA}; //선택한 사진의 식별자 만 넘김, 갤러리앱의 content provider 필요
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                cursor.moveToFirst();
                String imagePath = cursor.getString(0);

                BitmapFactory.Options options = new BitmapFactory.Options();    //data 뽑아내기
                //   options.inJustDecodeBounds =true;
                //    options.inSampleSize = calculateInSampleSize(options,100,100);
                options.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.
                int degree = getExifOrientation(imagePath);
                bitmap = getRotatedBitmap(bitmap, degree);
                if (bitmap != null) {
                    //         mImgUris.add(data.getData());
                    mImgIds.add(bitmap);
                    padapter.notifyItemChanged(mImgIds.size() - 1);

                    Log.d("size", Integer.toString(mImgIds.size()));
                    Log.d("The Bitmap", bitmap.toString());


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
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
    }*/


    private int getExifOrientation(String filePath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
        }

        return 0;
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
