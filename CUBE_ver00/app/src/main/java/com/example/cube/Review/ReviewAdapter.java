package com.example.cube.Review;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.CurrentApplication;
import com.example.cube.MoonChang.WriteCommentPopUp;
import com.example.cube.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemViewHolder> {
    ArrayList<ReviewParent> ReviewParents; //부모 리스트를 담을 배열

    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private DocumentReference DocumentRef;

    public ReviewAdapter(Context context, ArrayList<ReviewParent> ReviewParents) {
        super();
        this.mContext = context;
        this.ReviewParents = ReviewParents;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_parent, parent, false);
        return new ItemViewHolder(view);
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView writeComment;
        private ImageView rotatedArrow;
        private ImageView userImage;

        private TextView userID;
        private TextView time;
        private ImageView reviewImage;
        private TextView review;
        private LinearLayout comment_layout;

        private TextView comment;
        private TextView commentDate;
        private RatingBar ratingBar;

        private CurrentApplication ca = (CurrentApplication)(mContext.getApplicationContext());

        ItemViewHolder(final View v) {
            super(v);

            writeComment = (ImageView) v.findViewById(R.id.review_write_comment);
            rotatedArrow = (ImageView) v.findViewById(R.id.image_rotated_arrow);
            userImage = (ImageView) v.findViewById(R.id.review_user_image);

            userID = (TextView) v.findViewById(R.id.review_userID);
            userImage = (ImageView) v.findViewById(R.id.review_user_image);
            time = (TextView) v.findViewById(R.id.review_time);
            reviewImage = (ImageView)v.findViewById(R.id.review_image);
            review = (TextView) v.findViewById(R.id.review_main);

            comment_layout = (LinearLayout)v.findViewById(R.id.layout_comment);
            comment = (TextView) v.findViewById(R.id.review_comment);
            commentDate = (TextView)v.findViewById(R.id.review_comment_date);

            ratingBar = (RatingBar) v.findViewById(R.id.review_rating);
        }

        void onBind(final ReviewParent data) {
            /* 임시 ID */

            review.setText(data.getReview());
            userID.setText(data.getUser());
            ratingBar.setRating(data.getRating());
            String postTime = timeGapCheck(data.getDate());
            time.setText(postTime);
            String commentTime = timeGapCheck(data.getCommentDate());
            commentDate.setText(commentTime);
            String reviewImageStr = data.getPhoto();

            /* 리뷰사진이 없을때와 있을때 구분 */
            if (reviewImageStr.equals("null")) {
                reviewImage.setVisibility(View.GONE);
            } else {
                byte[] photo = Base64.decode(reviewImageStr, Base64.NO_WRAP);
                reviewImage.setVisibility(View.VISIBLE);
                reviewImage.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
            }

            /* 코멘트가 있을때와 없을때 구분 */
            if(data.getComment().equals("null")) {
                comment_layout.setVisibility(View.GONE);
                commentDate.setVisibility(View.GONE);
                Toast.makeText(mContext,"null임",Toast.LENGTH_SHORT).show();
            } else {
                comment.setText(data.getComment());
                comment_layout.setVisibility(View.VISIBLE);
                commentDate.setVisibility(View.VISIBLE);

                //Toast.makeText(v.getContext(),"null아님",Toast.LENGTH_SHORT).show();
            }

            /* 프로필사진 저장된거 있으면 불러옴 */
            if(!data.getProfile().equals("null")) {
                byte[] decodedByteArray = Base64.decode(data.getProfile(), Base64.NO_WRAP);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                userImage.setImageBitmap(decodedBitmap);
            }

            /* 관리자가 아니면 코멘트쓰기 버튼 숨김 */
            if(!ca.isAdmin()) {
                Toast.makeText(mContext,"관리자아님",Toast.LENGTH_SHORT).show();
                writeComment.setVisibility(View.INVISIBLE);
            }
            writeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.review_write_comment:
                            // 데이터를 다이얼로그로 보내는 코드
                            Bundle args = new Bundle();
                            //---------------------------------------.//
                            WriteCommentPopUp dialog = new WriteCommentPopUp();
                            dialog.setArguments(args);
                            dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(),"comment");
                            dialog.setDialogResult(new WriteCommentPopUp.OnMyDialogResult() {
                                @Override
                                public void finish(Bundle result) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentRef = db.collection("foodcourt/moonchang/review").document(data.getId());
                                    DocumentRef.update("comment", result.getString("comment"));
                                    DocumentRef.update("commentDate", new Date());

//                                    data.setComment(result.getString("comment"));
//                                    data.setCommentDate(new Date());

                                    //notifyDataSetChanged();
                                }
                            });
                            break;
                    }
                }
            });
            /* 그냥 클릭 */
//            Item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//            /* 롱클릭 */
//            Item.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    Toast.makeText(v.getContext(),"상세보기 클릭됨.", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(ReviewParents.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return ReviewParents.size();
    }

    public void addItem(ReviewParent data) {
        // 외부에서 item을 추가시킬 함수입니다.
        ReviewParents.add(0,data);
    }


    public static String timeGapCheck(Date postDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
        long timeGap = new Date().getTime() - postDate.getTime();
        long min = timeGap / 60000;  // 분
        String postStatus;
        if (min == 0) postStatus = "방금";
        else if (0 < min && min < 60) postStatus = min + "분 전";
        else if (60 <= min && min < 60 * 24) postStatus = min / 60 + "시간 전";
        else postStatus = simpleDateFormat.format(postDate);
        return postStatus;
    }
}