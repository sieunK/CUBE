package com.example.cube.Review;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemViewHolder> {
    final String TAG = "ReviewAdapter";
    ArrayList<ReviewParent> ReviewParents; //부모 리스트를 담을 배열
    private ListenerRegistration listenerRegistration;

    // adapter에 들어갈 list 입니다.
    private Context mContext;
    private DocumentReference DocumentRef;
    private CurrentApplication ca;

    public ReviewAdapter(Context context, Query query) {
        super();
        this.mContext = context;
        ca = (CurrentApplication)(mContext.getApplicationContext());
        ReviewParents = new ArrayList<>();
        EventListener childEventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String reviewParentKey;
                int reviewParentIndex;
                ReviewParent reviewParent;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            // A new comment has been added, add it to the displayed list
                            reviewParent = dc.getDocument().toObject(ReviewParent.class);
                            // Update RecyclerView
                            ReviewParents.add(0, reviewParent);
                            notifyItemInserted(0);
                            Log.w(TAG, "onChildChanged:unknown_child:ADDED");

                            break;
                        case MODIFIED:
                            // A comment has changed, use the key to determine if we are displaying this
                            // comment and if so displayed the changed comment.
                            reviewParent = dc.getDocument().toObject(ReviewParent.class);
                            reviewParentKey = dc.getDocument().getId();
                            reviewParentIndex = ReviewParents.indexOf(reviewParentKey);
                            if (reviewParentIndex > -1) {
                                // Replace with the new data
                                ReviewParents.set(reviewParentIndex, reviewParent);
                                Log.w(TAG, "onChildChanged:unknown_child:MODIFIED"  );

                                // Update the RecyclerView
                                notifyItemChanged(reviewParentIndex);
                            } else {
                                Log.w(TAG, "onChildChanged:unknown_child:" + reviewParentKey);
                            }
                            break;
                        case REMOVED:
                            // A comment has changed, use the key to determine if we are displaying this
                            // comment and if so remove it.
                            reviewParentKey = dc.getDocument().getId();

                            reviewParentIndex = ReviewParents.indexOf(reviewParentKey);
                            if (reviewParentIndex > -1) {
                                // Remove data from the list
                                ReviewParents.remove(reviewParentIndex);

                                // Update the RecyclerView
                                notifyItemRemoved(reviewParentIndex);
                            } else {
                                Log.w(TAG, "onChildRemoved:unknown_child:" + reviewParentIndex);
                            }
                            break;
                    }
                }

            }
        };
        listenerRegistration =query.addSnapshotListener(childEventListener);
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
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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


        ItemViewHolder(final View v) {
            super(v);

            writeComment = (ImageView) v.findViewById(R.id.review_write_comment);
            writeComment.setOnClickListener(this);
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

        @Override
        public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.review_write_comment:
                        // 데이터를 다이얼로그로 보내는 코드
                        Bundle args = new Bundle();
                        //---------------------------------------.//
                        WriteCommentPopUp dialog = new WriteCommentPopUp();
                        dialog.setArguments(args);
                        dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "comment");
                        dialog.setDialogResult(new WriteCommentPopUp.OnMyDialogResult() {
                            @Override
                            public void finish(Bundle result) {
                                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                                progressDialog.setTitle("loading...");
                                progressDialog.show();
                                final String putComment = result.getString("comment");
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                ReviewParent data = ReviewParents.get(getAdapterPosition());
                                Log.e("IDDDDDDD", data.getId());
                                DocumentRef = db.collection("foodcourt/moonchang/review").document(data.getId());
                                DocumentRef.update("comment", result.getString("comment"))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentRef.update("commentDate", new Date())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("UPDATE", "COMPLETE");
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "댓글 입력 실패", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        return;
                                    }
                                });
                            }
                        });
                        break;
                }
            }
        }



    public void cleanupListener() {
        listenerRegistration.remove();
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        ReviewParent data = ReviewParents.get(position);

        holder.review.setText(data.getReview());
        holder.userID.setText(data.getUser());
        holder.ratingBar.setRating(data.getRating());
        String postTime = timeGapCheck(data.getDate());
        holder.time.setText(postTime);
        String commentTime = timeGapCheck(data.getCommentDate());
        holder.commentDate.setText(commentTime);
        String reviewImageStr = data.getPhoto();
        /* 관리자가 아니면 코멘트쓰기 버튼 숨김 */
        if(!ca.isAdmin()) {
            //Toast.makeText(mContext,"관리자아님",Toast.LENGTH_SHORT).show();
            holder.writeComment.setVisibility(View.INVISIBLE);

        /* 리뷰사진이 없을때와 있을때 구분 */
        if (reviewImageStr.equals("null")) {
            holder. reviewImage.setVisibility(View.GONE);
        } else {
            byte[] photo = Base64.decode(reviewImageStr, Base64.NO_WRAP);
            holder. reviewImage.setVisibility(View.VISIBLE);
            holder.reviewImage.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }

        /* 코멘트가 있을때와 없을때 구분 */
        if(data.getComment().equals("null")) {
            holder. comment_layout.setVisibility(View.GONE);
            holder. commentDate.setVisibility(View.GONE);
            Toast.makeText(mContext,"null임",Toast.LENGTH_SHORT).show();
        } else {
            holder. comment.setText(data.getComment());
            holder. comment_layout.setVisibility(View.VISIBLE);
            holder. commentDate.setVisibility(View.VISIBLE);

            //Toast.makeText(v.getContext(),"null아님",Toast.LENGTH_SHORT).show();
        }

        /* 프로필사진 저장된거 있으면 불러옴 */
        if(!data.getProfile().equals("null")) {
            byte[] decodedByteArray = Base64.decode(data.getProfile(), Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            holder. userImage.setImageBitmap(decodedBitmap);
        }


    }
}


    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return ReviewParents.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
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