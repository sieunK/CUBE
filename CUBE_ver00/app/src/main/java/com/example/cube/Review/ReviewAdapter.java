package com.example.cube.Review;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cube.MoonChang.WriteCommentPopUp;
import com.example.cube.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    ArrayList<ReviewParent> ReviewParents; //부모 리스트를 담을 배열
    private Context mContext;

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

        private ImageView arrowIcon;
        private ImageView rotatedArrow;

        private TextView userID;
        private ImageView userImage;
        private TextView time;
        private ImageView reviewImage;
        private TextView review;

        private TextView comment;
        private TextView commentDate;
        private RatingBar ratingBar;


        ItemViewHolder(View v) {
            super(v);

            arrowIcon = (ImageView) v.findViewById(R.id.review_write_comment);
            rotatedArrow = (ImageView) v.findViewById(R.id.image_rotated_arrow);

            userID = (TextView) v.findViewById(R.id.review_userID);
            userImage = (ImageView) v.findViewById(R.id.review_user_image);
            time = (TextView) v.findViewById(R.id.review_time);
            reviewImage = (ImageView)v.findViewById(R.id.review_image);
            review = (TextView) v.findViewById(R.id.review_main);


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
            if (reviewImageStr == null) {
                reviewImage.setVisibility(View.GONE);
            } else {
                byte[] photo = Base64.decode(reviewImageStr, Base64.NO_WRAP);
                reviewImage.setVisibility(View.VISIBLE);
                reviewImage.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
            }

            if(data.getComment()==null) {
                comment.setVisibility(View.GONE);
                commentDate.setVisibility(View.GONE);
                rotatedArrow.setVisibility(View.GONE);
                //Toast.makeText(v.getContext(),"null임",Toast.LENGTH_SHORT).show();
            } else {
                comment.setText(data.getComment());
                commentDate.setVisibility(View.VISIBLE);
                comment.setVisibility(View.VISIBLE);
                rotatedArrow.setVisibility(View.VISIBLE);
                //Toast.makeText(v.getContext(),"null아님",Toast.LENGTH_SHORT).show();
            }

            arrowIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
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
                                    //Toast.makeText(v.getContext(),result.getString("review"),Toast.LENGTH_SHORT).show();
                                    //Log.i("[DEBUG]",result.getString("comment"));
                                    data.setComment(result.getString("comment"));
                                    notifyDataSetChanged();
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