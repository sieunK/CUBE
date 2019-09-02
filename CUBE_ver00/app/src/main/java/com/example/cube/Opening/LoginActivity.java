package com.example.cube.Opening;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.Administrator.AdminActivity;
import com.example.cube.BNUDialog;
import com.example.cube.BackPressCloseHandler;
import com.example.cube.DefaultActivity;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private int adminCheck;

    private ImageView BNULogo;
    private Button buttonSignIn;
    private Button buttonSignInAdmin;
    private Button findPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private TextView nonUserLogin;
    private CheckBox checkAutoLogin;

    private FirebaseAuth firebaseAuth;
    private BNUDialog dialog;

    private BackPressCloseHandler backPressCloseHandler;


    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminCheck = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null && !firebaseAuth.getCurrentUser().isEmailVerified()) {
            firebaseAuth.getCurrentUser().delete();
        }
        backPressCloseHandler = new BackPressCloseHandler(this);

        BNULogo = (ImageView) findViewById(R.id.imageView);
        checkAutoLogin = (CheckBox) findViewById(R.id.checkAutoLogin);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignInAdmin = (Button) findViewById(R.id.signInAdmin);
        buttonSignIn = (Button) findViewById(R.id.signIn);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        nonUserLogin = (TextView) findViewById(R.id.nonuserlogin);
        findPassword = (Button) findViewById(R.id.findpassword);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        if (setting.getBoolean("autoLogin", false)) {
            editTextEmail.setText(setting.getString("email", ""));
            editTextPassword.setText(setting.getString("password", ""));
            checkAutoLogin.setChecked(true);
        }

        BNULogo.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
        buttonSignInAdmin.setOnClickListener(this);
        buttonSignInAdmin.setVisibility(View.GONE);
        textViewSignUp.setOnClickListener(this);
        nonUserLogin.setOnClickListener(this);
        findPassword.setOnClickListener(this);
    }

    private void nonUserLogin() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("[DEBUG]", "signInAnonymously:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            finish();
                            startActivity(new Intent(getApplicationContext(), DefaultActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("[DEBUG]", "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        dialog= BNUDialog.newInstance("로그인 중입니다...");
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), BNUDialog.TAG);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (checkAutoLogin.isChecked()) {
                                String email = editTextEmail.getText().toString();
                                String password = editTextPassword.getText().toString();

                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putBoolean("autoLogin", true);
                                editor.commit();
                            }
                            finish();
                            startActivity(new Intent(getApplicationContext(), DefaultActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            editor.remove("email");
                            editor.remove("password");
                            editor.remove("autoLogin");
                            editor.clear();
                            editor.commit();
                            checkAutoLogin.setChecked(false);
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void adminLogin() {      // 로 그 인

        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        dialog = BNUDialog.newInstance("관리자페이지로 로그인 중입니다...");
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), BNUDialog.TAG);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                            mStore.collection("users").whereEqualTo("email", email)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Toast.makeText(getApplicationContext(), "로그인 실패\n 사용자를 찾을 수 없습니다!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                                Object isAdmin = ds.get("isAdmin");
                                                if (isAdmin == null) {
                                                    Toast.makeText(getApplicationContext(), "관리자가 아닙니다", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();;
                                                }
                                                else{
                                                    if (checkAutoLogin.isChecked()) {
                                                        String email = editTextEmail.getText().toString();
                                                        String password = editTextPassword.getText().toString();

                                                        editor.putString("email", email);
                                                        editor.putString("password", password);
                                                        editor.putBoolean("autoLogin", true);
                                                        editor.commit();
                                                    }
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            editor.remove("email");
                            editor.remove("password");
                            editor.remove("autoLogin");
                            editor.clear();
                            editor.commit();
                            checkAutoLogin.setChecked(false);
                        }
                        dialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v == BNULogo) {
            adminCheck = (adminCheck + 1) % 5;
            if (adminCheck == 4)
                buttonSignInAdmin.setVisibility(View.VISIBLE);
            else
                buttonSignInAdmin.setVisibility(View.GONE);
        }
        if (v == buttonSignIn) {
            userLogin();
        }
        if (v == buttonSignInAdmin) {
            adminLogin();
        }
        if (v == textViewSignUp) {
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        }
        if (v == findPassword) {
            finish();
            startActivity(new Intent(this, PasswordActivity.class));
        }
        if (v == nonUserLogin) {
            nonUserLogin();
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
