package com.example.cube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DBHelper extends SQLiteOpenHelper {
    public final static int DB_VERSION = 1;
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE IF NOT EXISTS NOTICE (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, contents TEXT, date TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS MC_MENU (name TEXT, photo BLOB, price INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS BASKET (name TEXT , num INTEGER, price INTEGER, photo TEXT);");

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == DB_VERSION) {
            db.execSQL("drop table if exists NOTICE");
            db.execSQL("drop table if exists MC_MENU");
            db.execSQL("drop table if exists BASKET");
            onCreate(db);
        }
    }

    public void deleteAll(String TABLE_NAME)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
        Log.d("ddfdsf","지워짐.");
    }
}
