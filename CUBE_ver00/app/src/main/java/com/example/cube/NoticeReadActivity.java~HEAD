package com.example.cube;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class NoticeReadActivity extends AppCompatActivity {
    private static final String TAG = "NoticeReadActivity";

    // private ActionBar actionBar;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private String collectionPath;
    private String DocumentID;
    private DocumentReference DocumentRef;

    private CommentAdapter mAdapter;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;

    private TextView ReadPostTitle;
    private TextView ReadPostContent;
    private TextView ReadPostDate;
    private TextView ReadPostNumClicks;
    private LinearLayout mPictures;
    private LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notice);
        // actionBar = getSupportActionBar();
        // actionBar.setTitle(" ");

        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        collectionPath = "foodcourt/moonchang/board";
        Intent preIntent = getIntent();
        DocumentID = preIntent.getStringExtra("id");
        DocumentRef = mStore.collection(collectionPath).document(DocumentID);

        ReadPostTitle = findViewById(R.id.read_title);
        ReadPostContent = findViewById(R.id.read_content);
        ReadPostDate = findViewById(R.id.read_date);
        ReadPostNumClicks = findViewById(R.id.read_numclicks);

        mPictures = (LinearLayout) findViewById(R.id.read_pictures);
        mInflater = LayoutInflater.from(this);

        mCommentButton = (Button) findViewById(R.id.comment_button);
        mCommentField = findViewById(R.id.comment_field);
        mCommentsRecycler = findViewById(R.id.comment_list);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();

        DocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                StorageReference storageRef = mStorage.getReferenceFromUrl("gs://bobnu-47135.appspot.com").
                        child(DocumentID + "_images");
                DocumentSnapshot ds = task.getResult();
                NoticeData post = ds.toObject(NoticeData.class);
                if (post == null) {
                    Toast.makeText(getApplicationContext(), "존재하지 않는 게시물입니다", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ReadPostTitle.setText(post.getTitle());
                    ReadPostContent.setText(post.getContent());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
                    ReadPostDate.setText(simpleDateFormat.format(post.getDate().getTime()));
                    ReadPostNumClicks.setText(Integer.toString(post.getNumClicks()));
                    final int photoNum = (int) (long) ds.get("filenum");
                    Log.d("number of photo is", Integer.toString(photoNum));

                    if(photoNum==0) progressDialog.dismiss();
                    for (int i = 0; i < photoNum; i++) {
                        final long FILE_SIZE = 1024 * 1024 * 5;
                        final int photonum = i;
                        String filename = i + ".png";
                        storageRef.child("/" + filename).getBytes(FILE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Log.d("read bytes", "success");
                                addView(bytes);
                                if (photonum == photoNum - 1)
                                    progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "조회 실패!");
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            }
        });


        // Listen for comments
        mAdapter = new CommentAdapter(DocumentRef.collection("comments").
                orderBy("date", Query.Direction.ASCENDING));
        mCommentsRecycler.setAdapter(mAdapter);
        mCommentsRecycler.addItemDecoration(new

                DividerItemDecoration(mCommentsRecycler.getContext(), 1));
        mCommentsRecycler.addOnItemTouchListener(
                new

                        RecyclerItemClickListener(getApplicationContext(), mCommentsRecycler,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onLongItemClick(View view, int position) {
                                Log.d(this.getClass().getName(), "long push");
                                String userId = mAdapter.mComments.get(position).getUid();
                                String commentId = mAdapter.mCommentIds.get(position).toString();
                                DeleteComment(userId,commentId);
                            }
                        }));

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mCommentButton) {
                    Log.d(TAG, "comment push");
                    postComment();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }

    private void addView(byte[] bytes) {
        //   for (int i = 0; i < mImgIds.size(); i++)
        //  {
        View view = mInflater.inflate(R.layout.picture_item,
                mPictures, false);
        ImageView img = (ImageView) view
                .findViewById(R.id.id_index_picture_item);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // 갤러리앱에서 넘어온 파일경로를 줘서 옵션제공, 비트맵생성.

        img.setImageBitmap(bitmap);
        mPictures.addView(view);
        // }
    }

    private void postComment() {
        final String uEmail = mAuth.getCurrentUser().getEmail();
        final String uid = mAuth.getCurrentUser().getUid();
        final String id = DocumentRef.getId();

        Query userquery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("email", uEmail);
        userquery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    AppUser AppUser = dc.getDocument().toObject(AppUser.class);
                    if (AppUser == null) {
                        Log.e(TAG, "AppUser " + uEmail + " is unexpectedly null");
                        Toast.makeText(getApplicationContext(),
                                "사용자를 정의할 수 없습니다",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String adminName = dc.getDocument().get("username").toString();
                        Comments comment = new Comments(uid, adminName, mCommentField.getText().toString(), new Date());
                        DocumentRef.collection("comments").document().set(comment);
                        DocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                int num = (int)(long)task.getResult().get("numComments");
                                Log.e("comment num", Integer.toString(num));
                                DocumentRef.update("numComments", ++num);
                            }
                        });
                        mCommentField.setText(null);
                    }
                }
            }
        });
    }


    private void DeleteComment(String userId, String commentId) {
        final String cid = commentId;
        final String uid = mAuth.getCurrentUser().getUid();
        if(userId.equals(uid)) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            DeleteDialogFragment ddf =
                    DeleteDialogFragment.newInstance(new DeleteDialogFragment.DeleteCommentListener() {
                        @Override
                        public void DeleteOrNot(int IsDeleted) {
                            if(IsDeleted==1) {
                                Log.d(TAG, "comment delete");
                                DocumentRef.collection("comments").document(cid).delete();
                                Toast.makeText(getApplicationContext(), "Comment Deleted", Toast.LENGTH_SHORT).show();                            }
                            else Log.d(TAG, "IsDeleted==0");

                        }
                    });
            ddf.show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
        }
        else Toast.makeText(getApplicationContext(), "글쓴이가 아닙니다", Toast.LENGTH_SHORT).show();
    }
    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;
        public TextView dateView;
        CommentViewHolder(View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.comment_author_text);
            bodyView = itemView.findViewById(R.id.comment_body_text);
            dateView = itemView.findViewById(R.id.comment_date_text);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        private List<String> mCommentIds = new ArrayList<>();
        private List<Comments> mComments = new ArrayList<>();

        private ListenerRegistration listenerRegistration;

        public CommentAdapter(Query query) {
            // Create child event listener
            EventListener childEventListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {return;}
                    String commentKey;
                    int commentIndex;
                    Comments comment;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                // A new comment has been added, add it to the displayed list
                                comment = dc.getDocument().toObject(Comments.class);
                                // Update RecyclerView
                                mCommentIds.add(0,dc.getDocument().getId());
                                mComments.add(0,comment);
                                notifyItemInserted(0);
                                break;
                            case MODIFIED:
                                // A comment has changed, use the key to determine if we are displaying this
                                // comment and if so displayed the changed comment.
                                comment = dc.getDocument().toObject(Comments.class);
                                commentKey = dc.getDocument().getId();
                                commentIndex = mCommentIds.indexOf(commentKey);
                                if (commentIndex > -1) {
                                    // Replace with the new data
                                    mComments.set(commentIndex, comment);

                                    // Update the RecyclerView
                                    notifyItemChanged(commentIndex);
                                } else {
                                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                                }
                                break;
                            case REMOVED:
                                // A comment has changed, use the key to determine if we are displaying this
                                // comment and if so remove it.
                                commentKey = dc.getDocument().getId();

                                commentIndex = mCommentIds.indexOf(commentKey);
                                if (commentIndex > -1) {
                                    // Remove data from the list
                                    mCommentIds.remove(commentIndex);
                                    mComments.remove(commentIndex);

                                    // Update the RecyclerView
                                    notifyItemRemoved(commentIndex);
                                } else {
                                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                                }
                                break;
                        }
                    }

                }
            };
            listenerRegistration = query.addSnapshotListener(childEventListener);
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.comment_item, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comments comment = mComments.get(position);
            holder.authorView.setText(comment.getAuthor());
            holder.bodyView.setText(comment.getBody());
            String postTime = timeGapCheck(comment.getDate());
            holder.dateView.setText(postTime);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            listenerRegistration.remove();
        }
    }


    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{
        public interface OnItemClickListener {
            void onLongItemClick(View view, int position);
        }
        private NoticeReadActivity.RecyclerItemClickListener.OnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, NoticeReadActivity.RecyclerItemClickListener.OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        Log.d("long", "press");
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onLongItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }
        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    }
    public static String timeGapCheck(Date postDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
        long timeGap = new Date().getTime() - postDate.getTime();
        long min = timeGap / 60000;  // 분
        String postStatus;
        if(min == 0 ) postStatus = "방금";
        else if(0< min && min< 60) postStatus = min + "분 전";
        else if(60<=min && min<60*24) postStatus = min/60 +"시간 전";
        else postStatus = simpleDateFormat.format(postDate);
        return postStatus;
    }
}
