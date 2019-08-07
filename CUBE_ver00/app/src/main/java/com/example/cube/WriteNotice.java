package com.example.cube;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WriteNotice extends AppCompatActivity {

    EditText writeTitle;
    EditText writeContents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        writeTitle = (EditText)findViewById(R.id.text_notice_title);
        writeContents = (EditText)findViewById(R.id.text_notice_contents);

    }
}
