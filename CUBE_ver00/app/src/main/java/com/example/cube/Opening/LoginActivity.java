package com.example.cube.Opening;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.DefaultActivity;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private Button findPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private CheckBox checkAutoLogin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null && !firebaseAuth.getCurrentUser().isEmailVerified()) {
            firebaseAuth.getCurrentUser().delete();
        }
        progressDialog = new ProgressDialog(this);

        checkAutoLogin = (CheckBox) findViewById(R.id.checkAutoLogin);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignIn = (Button) findViewById(R.id.signIn);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        findPassword = (Button) findViewById(R.id.findpassword);

        setting = getSharedPreferences("setting",0);
        editor = setting.edit();

        if(setting.getBoolean("autoLogin",false)) {
            editTextEmail.setText(setting.getString("email",""));
            editTextPassword.setText(setting.getString("password",""));
            checkAutoLogin.setChecked(true);
        }

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        findPassword.setOnClickListener(this);
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this,"Please enter email", Toast.LENGTH_LONG).show();
            return ;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this,"Please enter password", Toast.LENGTH_LONG).show();
            return ;
        }
        progressDialog.setMessage("로그인 중입니다...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            if(checkAutoLogin.isChecked()) {
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
                            Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                            editor.remove("email");
                            editor.remove("password");
                            editor.remove("autoLogin");
                            editor.clear();
                            editor.commit();
                            checkAutoLogin.setChecked(false);
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(v == buttonSignIn) {
            userLogin();
        }
        if(v == textViewSignUp) {
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        }
        if(v == findPassword) {
            finish();
            startActivity(new Intent(this, PasswordActivity.class));
        }
    }
}
