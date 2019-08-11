package com.example.cube.Opening;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cube.Components.UserInformation;
import com.example.cube.Opening.LoginActivity;
import com.example.cube.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;

    private DatabaseReference databaseReference;
    private EditText editTextName, editTextAddress;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextAddress = (EditText) findViewById(R.id.editAddress);
        editTextName = (EditText) findViewById(R.id.editName);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome "+user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
    }

    private void saveUserInformation() {
        String name = editTextName.getText().toString().trim();
        String addr = editTextAddress.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, addr);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this,"Information Saved...",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
        if(v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        if(v == buttonSave) {
            saveUserInformation();
        }

    }
}
