package com.example.cube.MoonChang;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cube.DBHelper;
import com.example.cube.R;

import static android.app.Activity.RESULT_OK;

public class SetCountPopUp extends Activity {
    EditText count;
    Button countDown;
    Button countUp;
    String foodName;
    String foodPhoto;
    Button addToBagLayout;

    int foodNum;
    int foodPrice;
    private DBHelper helper;
    private static SQLiteDatabase db;


    int returnValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_count_pop_up);
        Intent intent = getIntent();
        foodName = intent.getStringExtra("name");
        foodPrice = intent.getIntExtra("price", 0);
        foodPhoto = intent.getStringExtra("photo");
        foodNum = 0;

        helper = new DBHelper(this, "BASKET.db", null, 1);
        db = helper.getWritableDatabase();

        //UI 객체생성
        addToBagLayout = findViewById(R.id.add_to_bag);
        addToBagLayout.setClickable(false);

        count = (EditText) findViewById(R.id.textview_setcount);
        count.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) addToBagLayout.setClickable(false);
                else if (s.equals("")) addToBagLayout.setClickable(false);
                else addToBagLayout.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });


        countDown = (Button) findViewById(R.id.button_countdown);
        countUp = (Button) findViewById(R.id.button_countup);

        countDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodNum = Integer.parseInt(count.getText().toString());
                if (foodNum != 0)
                    --foodNum;

                count.setText(String.valueOf(foodNum));
                returnValue = foodNum;
            }
        });
        countUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodNum = Integer.parseInt(count.getText().toString());
                ++foodNum;
                count.setText(String.valueOf(foodNum));
                returnValue = foodNum;
            }
        });
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        Log.d("BUTTON", "Clicked");
        try {
            returnValue = Integer.parseInt(count.getText().toString());
            Cursor checking = db.rawQuery("SELECT name FROM BASKET WHERE name='" + foodName + "';", null);
            Toast.makeText(v.getContext(), foodName + " 장바구니에 " + returnValue + "개 추가됨.", Toast.LENGTH_SHORT).show();
            if (checking.getCount() == 0) {   // 있으면 무조건 getCount 는 1 일 것
                db.execSQL("INSERT INTO BASKET VALUES('" + foodName + "', '" + returnValue + "', '" + foodPrice + "', '" + foodPhoto + "');");
                finish();
            } else {
                db.execSQL("UPDATE BASKET SET num = num+ " + returnValue);
                finish();
            }
        }
        catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(), "숫자를 입력해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public void onBackPressed() {
//        //안드로이드 백버튼 막기
//        return;
//    }
}

