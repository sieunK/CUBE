package com.example.cube.Opening;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.cube.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView start = (ImageView)findViewById(R.id.imageViewStart);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(start);
        Glide.with(this).load(R.drawable.start).into(gifImage);

        Handler handler = new Handler();
        handler.postDelayed(new startHandler(), 4000);

    }

    private class startHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            MainActivity.this.finish();
        }
    }
}
