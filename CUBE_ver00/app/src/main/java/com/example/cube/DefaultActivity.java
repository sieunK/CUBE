package com.example.cube;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cube.MyPage.MyPageActivity;
import com.example.cube.Notice.NoticeActivity;
import com.example.cube.Opening.LoginActivity;
import com.example.cube.Setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class DefaultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore Fdb;
    private DBHelper helper;
    private static SQLiteDatabase db;
    final static HashMap<String, Pair<String, Integer>> mc_menu = new HashMap<>();

    private CurrentApplication currentUserInfo;
    private String getUserEmail;
    private String getUserNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("밥NU");
        setContentView(R.layout.activity_default);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* DB 작업 */
        helper = new DBHelper(this, "MC_MENU.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        Fdb = FirebaseFirestore.getInstance();

        Fdb.collection("foodcourt/moonchang/menu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String t_name = document.getData().get("name").toString();
                                String t_photo = null;
                                if (document.getData().get("photo") != null)
                                    t_photo = document.getData().get("photo").toString();
                                int t_price = ((Long) document.getData().get("price")).intValue();
                                mc_menu.put(t_name, new Pair<>(t_photo, t_price));
                                Log.d("For Test2", t_name);
                                //Log.d("For Test3", t_photo);
                                Log.d("For Test4", "" + mc_menu.get(t_name).second);

                                // DB에 입력한 값으로 행 추가
                                db.execSQL("INSERT INTO MC_MENU VALUES('" + t_name + "', '" + t_photo + "', " + t_price + ");");
                            }
                        } else {
                            Log.w("????", "Error getting documents.", task.getException());
                        }
                    }
                });



        /* 로그인 후 앱에서 계속 사용할 수 있도록 닉네임과 이메일을 저장 */
        currentUserInfo = (CurrentApplication) getApplication();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getUserEmail = mAuth.getCurrentUser().getEmail();
        Fdb.collection("users").whereEqualTo("email", getUserEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                getUserNickName = dc.getData().get("username").toString();
                                if (getUserNickName == null) {
                                    Log.d("nullName", "null");
                                    Toast.makeText(getApplicationContext(), "사용자를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "사용자 확인", Toast.LENGTH_SHORT).show();
                                    currentUserInfo.setEmail(getUserEmail);
                                    currentUserInfo.setNickname(getUserNickName);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "사용자를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        /* 세팅된 닉네임 가져와서 드로어 레이아웃 프로필에 적음 */
      //  Intent intent = getIntent();
      //  String nickname = intent.getStringExtra("nickname");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_id_text = (TextView) nav_header_view.findViewById(R.id.show_nickname);
        nav_header_id_text.setText(getUserNickName);

        DBHelper dbHelper = new DBHelper(getApplicationContext(), "NOTICE.db", null, 1);

        /* 초기화면을 HomePage로 설정함 */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, new HomeActivity());
        fragmentTransaction.commit();

        /* 이건 아마 옆에 드로어 레이아웃 설정 */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_home) {
            fragment = new HomeActivity();
        } else if (id == R.id.nav_notice) {
            fragment = new NoticeActivity();
        } else if (id == R.id.nav_mypage) {
            fragment = new MyPageActivity();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingActivity();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            /* 수정필요 */
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.content_layout, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
