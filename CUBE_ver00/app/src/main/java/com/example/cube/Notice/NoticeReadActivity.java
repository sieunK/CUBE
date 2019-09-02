package com.example.cube.Notice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.cube.BNUDialog;
import com.example.cube.Components.Comments;
import com.example.cube.Components.NoticeData;
import com.example.cube.DeleteDialogFragment;
import com.example.cube.R;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class NoticeReadActivity extends AppCompatActivity {
    private static final String TAG = "NoticeReadActivity";
    Context context;
    // FIREBASE 연동
    private FirebaseFirestore mStore;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private String collectionPath;
    private String DocumentID;
    private DocumentReference DocumentRef;

    // 글 구성
    private TextView ReadPostTitle;
    private TextView ReadPostContent;
    private TextView ReadPostDate;
    private TextView ReadPostNumClicks;

    // 댓글 구성
    private CommentAdapter mAdapter;
    private EditText mCommentField;
    private Button mCommentButton;
    private long ReadPostNumComments;
    private RecyclerView mCommentsRecycler;

    // 이미지 구성
    private int photoNum;
    private LinearLayout mPictures;
    private LayoutInflater mInflater;

    // 입력 시 키보드 컨트롤
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_notice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.read_notice_toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        // FIREBASE
        mStore = FirebaseFirestore.getInstance();
        collectionPath = "foodcourt/moonchang/board";

        Intent preIntent = getIntent();
        DocumentID = preIntent.getStringExtra("id");
        DocumentRef = mStore.collection(collectionPath).document(DocumentID);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://bobnu-47135.appspot.com").
                child(DocumentID + "_images");
        ;
        mAuth = FirebaseAuth.getInstance();


        // 글 구성
        ReadPostTitle = findViewById(R.id.read_title);
        ReadPostContent = findViewById(R.id.read_content);
        ReadPostDate = findViewById(R.id.read_date);
        ReadPostNumClicks = findViewById(R.id.read_numclicks);

        // 이미지 구성
        photoNum = 0;
        mPictures = (LinearLayout) findViewById(R.id.read_pictures);
        mInflater = LayoutInflater.from(this);

        // 댓글 구성
        mCommentButton = (Button) findViewById(R.id.comment_button);
        mCommentField = findViewById(R.id.comment_field);
        mCommentsRecycler = findViewById(R.id.comment_list);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 로딩 중..
        final BNUDialog dialog = BNUDialog.newInstance("로딩 중입니다...");
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), BNUDialog.TAG);

        // 게시글 가져오기 + FIREBASE STORAGE 에서 현재 게시글에 해당하는 폴더의 이미지 가져오기
        DocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot ds = task.getResult();
                NoticeData post = ds.toObject(NoticeData.class);

                if (post == null) {
                    Toast.makeText(getApplicationContext(), "존재하지 않는 게시물입니다", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    //게시글 구성
                    ReadPostTitle.setText(post.getTitle());
                    ReadPostContent.setText(post.getContent());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm (E)");
                    ReadPostDate.setText(simpleDateFormat.format(post.getDate().getTime()));
                    ReadPostNumClicks.setText(Integer.toString(post.getNumClicks()));
                    ReadPostNumComments = (long) post.getNumComments();

                    photoNum = (int) (long) ds.get("filenum");
                    Log.d("number of photo is", Integer.toString(photoNum));

                    // 이미지 가져오기
                    if (photoNum == 0) dialog.dismiss();
                    for (int i = 0; i < photoNum; i++) {
                        final long unit = 1024 * 1024 * 4;
                        final String filename = i + ".png";
                        mStorageRef.child("/" + filename).getBytes(unit).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Log.e("image search success", "조회 성공 " + filename);

                                addView(bytes);
                                if (mPictures.getChildCount() == photoNum) dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("image search fail", "조회 실패" + filename);
                                Toast.makeText(getApplicationContext(), "이미지 불러오기 실패!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            }
        });


        // 댓글 구성
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
                                DeleteComment(userId, commentId);
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

    public void hideKeyBoard() {
        imm.hideSoftInputFromWindow(mCommentField.getWindowToken(), 0);
    }

    public RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {


        @Override
        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

            return false;
        }
    };


    private void addView(byte[] bytes) {

        View view = mInflater.inflate(R.layout.picture_item,
                mPictures, false);
        ImageView img = view.findViewById(R.id.index_picture_item);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_logo);

        if(this.isFinishing())
            return;     // 중간에 액티비티 껐을 시

        Glide.with(this).load(bytes)
                .apply(requestOptions).listener(requestListener).into(img);
        mPictures.addView(view);
    }

    private void postComment() {
        hideKeyBoard();

        final String uEmail = mAuth.getCurrentUser().getEmail();
        final String uid = mAuth.getCurrentUser().getUid();

        Query userQuery = mStore.collection("users").whereEqualTo("email", uEmail);
        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot foundUser = queryDocumentSnapshots.getDocumentChanges().get(0)
                        .getDocument();

                String adminName = foundUser.get("username").toString();

                Comments comment = new Comments(uid, adminName, mCommentField.getText().toString(), new Date());
                DocumentRef.collection("comments").document().set(comment);
                DocumentRef.update("numComments", ++ReadPostNumComments);
                mCommentField.setText(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "AppUser " + uEmail + " is unexpectedly null");
                Toast.makeText(getApplicationContext(),
                        "사용자를 정의할 수 없습니다",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void DeleteComment(String userId, String commentId) {
        final String cid = commentId;
        final String uid = mAuth.getCurrentUser().getUid();
        if (userId.equals(uid)) {
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            DeleteDialogFragment ddf =
                    DeleteDialogFragment.newInstance(new DeleteDialogFragment.DeleteCommentListener() {
                        @Override
                        public void DeleteOrNot(int IsDeleted) {
                            if (IsDeleted == 1) {
                                Log.d(TAG, "comment delete");
                                DocumentRef.collection("comments").document(cid).delete();
                                Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            } else Log.d(TAG, "IsDeleted==0");

                        }
                    });
            ddf.show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
        } else Toast.makeText(getApplicationContext(), "글쓴이가 아닙니다", Toast.LENGTH_SHORT).show();
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
                    if (e != null) {
                        return;
                    }
                    String commentKey;
                    int commentIndex;
                    Comments comment;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                // A new comment has been added, add it to the displayed list
                                comment = dc.getDocument().toObject(Comments.class);
                                // Update RecyclerView
                                mCommentIds.add(0, dc.getDocument().getId());
                                mComments.add(0, comment);
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


    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 게시글 수정은 추후에 만들기로


            // 게시글 삭제
            case R.id.delete_post: {
                DeleteDialogFragment ddf =
                        DeleteDialogFragment.newInstance(new DeleteDialogFragment.DeletePostListener() {
                            @Override
                            public void DeleteOrNot(int IsDeleted) {
                                if (IsDeleted == 1) {
                                    final ProgressDialog progressDialog = new ProgressDialog(NoticeReadActivity.this);
                                    progressDialog.setTitle("삭제중입니다");
                                    progressDialog.show();

                                    // STORAGE에서 해당폴더삭제
                                    if (photoNum > 0) {
                                        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://bobnu-47135.appspot.com").
                                                child(DocumentID + "_images");
                                        for (int i = 0; i < photoNum; i++) {
                                            final String filename = i + ".png";
                                            storageRef.child("/" + filename).delete().addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, filename + "을 찾지 못함");
                                                }
                                            });
                                        }
                                    }

                                    //댓글 컬렉션 삭제 ( 많을 시 메모리부담이 있거나 느릴 수 있음)
                                    DocumentRef.collection("comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot ds : task.getResult()) {
                                                    DocumentRef.collection("comments").document(ds.getId()).delete();
                                                }
                                            } else return;
                                        }
                                    });
                                    DocumentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    finish();
                                                                }
                                                            }
                                    );  // FIRESTORE 에서 게시글 문서 삭제
                                }
                            }
                        });
                ddf.show(getSupportFragmentManager(), DeleteDialogFragment.TAG);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (mStorageRef != null) {
            outState.putString("reference", mStorageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = mStorageRef.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot state) {
                    // Success!
                    // ...
                }
            });
        }
    }

}
