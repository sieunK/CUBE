package com.example.cube.Opening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.BackPressCloseHandler;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private Button buttonCheckDuplicate;
    private Button  buttonVerification;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordCheck;
    private EditText nickName;
    private TextView textViewSignIn;

    private BackPressCloseHandler backPressCloseHandler;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        buttonCheckDuplicate = (Button)findViewById(R.id.buttonCheckDuplicate);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonVerification = (Button) findViewById(R.id.buttonVerification);
        editTextEmail = (EditText) findViewById(R.id.email);
        nickName = (EditText) findViewById(R.id.nickName);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPasswordCheck = (EditText) findViewById(R.id.passwordCheck);

        textViewSignIn = (TextView) findViewById(R.id.alredyRegister);

        buttonCheckDuplicate.setOnClickListener(this);
        buttonVerification.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVerification.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVerification.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVerification.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String checkPassword = editTextPasswordCheck.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this,"Please enter email",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this,"Please enter password",Toast.LENGTH_SHORT).show();
                    return ;
                }
                buttonCheckDuplicate.setEnabled(password.equals(checkPassword));
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendVerificationEmail() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Successfully Send a Email!", Toast.LENGTH_SHORT).show();
                                                buttonRegister.setEnabled(task.isSuccessful());
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Fail to Send a Email!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                   if(task.isSuccessful()) {
                                       firebaseUser = firebaseAuth.getCurrentUser();
                                       if(firebaseUser != null) {
                                           Toast.makeText(SignUpActivity.this, "check your email", Toast.LENGTH_LONG).show();
                                           buttonRegister.setEnabled(task.isSuccessful());
                                       }
                                   }
                                }
                            });

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == buttonVerification) {
            sendVerificationEmail();
        }
        if(v == textViewSignIn) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v == buttonRegister) {
            firebaseUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (firebaseUser.isEmailVerified()) {
                        final String userName = nickName.getText().toString().trim();
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", firebaseUser.getEmail());
                        user.put("point", 0);
                        user.put("username", userName);
                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Fail to register", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(SignUpActivity.this,"Please Check Your Email",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if(v == buttonCheckDuplicate) {
            final String userName = nickName.getText().toString().trim();
            final Query nickNameQuery = db.collection("users").whereEqualTo("username",userName);

            nickNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            if(userName.isEmpty())
                                Toast.makeText(SignUpActivity.this,"Please fill the blank",Toast.LENGTH_LONG).show();
                            else {
                                buttonVerification.setEnabled(true);
                                Toast.makeText(SignUpActivity.this, "You can use this nickname", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Find same nick name",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
