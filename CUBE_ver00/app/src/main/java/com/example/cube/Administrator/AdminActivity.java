package com.example.cube.Administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.cube.BNUDialog;
import com.example.cube.BackPressCloseHandler;
import com.example.cube.CurrentApplication;
import com.example.cube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class AdminActivity extends AppCompatActivity {
    final String TAG = "AdminActivity";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BackPressCloseHandler backPressCloseHandler;

    private FirebaseFirestore Fdb;
    private CurrentApplication currentUserInfo;
    private BNUDialog dialog;

    private String getUserEmail;
    private String getUserNickName;
    private String getUserDocID;
    private Object getUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        backPressCloseHandler = new BackPressCloseHandler(this);

        tabLayout = (TabLayout)findViewById(R.id.main_tabs);
        viewPager = (ViewPager)findViewById(R.id.main_pager);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(
                getSupportFragmentManager(),tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        getUserEmail = mAuth.getCurrentUser().getEmail();
        currentUserInfo = (CurrentApplication) getApplication();

        dialog = BNUDialog.newInstance("사용자 확인...");
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), BNUDialog.TAG);

        Fdb = FirebaseFirestore.getInstance();

        Fdb.collection("users").whereEqualTo("email", getUserEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "이메일은 찾음", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot dc : task.getResult()) {
                                getUserNickName = dc.getData().get("username").toString();
                                getUserProfile = dc.getData().get("profile");
                                getUserDocID = dc.getId();
                                if (getUserNickName == null) {
                                    Log.d("nullName", "null");
                                    Toast.makeText(getApplicationContext(), "사용자를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "사용자 확인", Toast.LENGTH_SHORT).show();
                                    currentUserInfo.setEmail(getUserEmail);
                                    currentUserInfo.setNickname(getUserNickName);
                                }
                                if(getUserProfile!=null) {
                                    currentUserInfo.setProfileImage(getUserProfile.toString());
                                }

                                Object isAdmin = dc.getData().get("isAdmin");
                                if(isAdmin!=null && (boolean)isAdmin==true) {
                                    currentUserInfo.setAdmin(true);
                                    Toast.makeText(getApplicationContext(), "관리자 확인", Toast.LENGTH_SHORT).show();

                                }
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "사용자를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
