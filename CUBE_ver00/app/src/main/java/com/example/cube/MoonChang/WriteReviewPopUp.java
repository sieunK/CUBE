package com.example.cube.MoonChang;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cube.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class WriteReviewPopUp extends DialogFragment {
    private boolean readStoragePermission;

    OnMyDialogResult mDialogResult;

    private LinearLayout writeReviewLayout;
    private Fragment fragment;
    private Button cancel;
    private Button dismiss;
    private ImageView reviewImage;
    private EditText reviewMain;
    private RatingBar ratingBar;
    private Boolean isThereImage=false;

    public WriteReviewPopUp() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            readStoragePermission = true;
        }
        if (!readStoragePermission) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_write_review, container, false);

        writeReviewLayout = view.findViewById(R.id.review_write_layout);
        writeReviewLayout.setPadding(20, 20, 20, 20);
        cancel = (Button) view.findViewById(R.id.review_cancel);
        dismiss = (Button) view.findViewById(R.id.review_dismiss);
        reviewMain = (EditText) view.findViewById(R.id.review_write);
        reviewImage = (ImageView) view.findViewById(R.id.review_write_image);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_write);

        Bundle args = getArguments();
        String value = args.getString("key");

        reviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readStoragePermission) { // 갤러리앱의 목록화면을 인텐트로 띄우자
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 10); // 목록화면에서 작업하고 되돌아왔을 때 사후처리해야하므로 forResult씀.
                } else {
                    Toast.makeText(getContext(), "허가되지않음", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("review");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    if (mDialogResult != null) {

                        Bundle bundle = new Bundle();
                        bundle.putString("review", reviewMain.getText().toString());
                        bundle.putFloat("rating", ratingBar.getRating());

                        if(isThereImage) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            Bitmap bitmap = ((BitmapDrawable) reviewImage.getDrawable()).getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            bundle.putByteArray("image", byteArray);
                        }
                        bundle.putBoolean("isImage",isThereImage);
                        mDialogResult.finish(bundle);
                    }

                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
                }
            }
        });

        return view;
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(Bundle result);
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
                options.inSampleSize = 2;
                String imagePath = cursor.getString(0);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.
//                float bmpWidth = bitmap.getWidth();
//                float bmpHeight = bitmap.getHeight();
//                if (bmpWidth > 200) {
//                    float mWidth = bmpWidth / 100;
//                    float scale = 200/ mWidth;
//                    bmpWidth *= (scale / 100);
//                    bmpHeight *= (scale / 100);
//                } else if (bmpHeight > 120) {
//                    float mHeight = bmpHeight / 100;
//                    float scale = 120/ mHeight;
//                    bmpWidth *= (scale / 100);
//                    bmpHeight *= (scale / 100);
//                }
//                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);

                int degree = getExifOrientation(imagePath);
                bitmap = getRotatedBitmap(bitmap, degree);
                if (bitmap != null) {
                    Glide.with(this).load(bitmap)
                            .into(reviewImage);
                    isThereImage=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
