package com.example.cube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteDialogFragment extends DialogFragment implements View.OnClickListener{
    public static final String TAG = "Delete Post Event";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String email;
    private String passwd;
    private EditText putPasswdIn;
    private static int flag;
    private DeletePostListener plistener;
    private DeleteCommentListener clistener;
    public DeleteDialogFragment(){}

    public static DeleteDialogFragment newInstance(DeletePostListener listener){
        DeleteDialogFragment ddf = new DeleteDialogFragment();
        ddf.plistener = listener;
        flag = 0;
        return ddf;
    }
    public static DeleteDialogFragment newInstance(DeleteCommentListener listener){
        DeleteDialogFragment ddf = new DeleteDialogFragment();
        ddf.clistener = listener;
        flag = 1;
        return ddf;
    }
    public interface DeletePostListener{
        void DeleteOrNot(int IsDeleted);
    }
    public interface DeleteCommentListener{
        void DeleteOrNot(int IsDeleted);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_dialog,container);
        putPasswdIn = (EditText)v.findViewById(R.id.put_passwd);
        Button mDeleteButton =(Button)v.findViewById(R.id.delete_post_button);
        mDeleteButton.setOnClickListener(this);
       // setCancelable(false);
        return v;
    }

    @Override
    public void onClick(View v) {
        email = mAuth.getCurrentUser().getEmail();
        passwd = putPasswdIn.getText().toString();
        if(passwd.length() <= 0){//빈값이 넘어올때의 처리
            Toast.makeText(getActivity(), "비밀번호를 입력하세요." , Toast.LENGTH_SHORT).show();
        }
        else
            reauthenticateUser(email,passwd, flag);

    }

    private void reauthenticateUser(String email, String password, int flag) {
        final int deleteSelect = flag;
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        firebaseUser.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && deleteSelect==0 ) {
                            plistener.DeleteOrNot(1);
                            dismiss();
                        }
                        else if(task.isSuccessful() && deleteSelect==1) {
                            clistener.DeleteOrNot(1);
                            dismiss();
                        }
                        else
                         {
                            Toast.makeText(getActivity(), "비밀번호가 틀렸습니다." , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
