package com.example.cube;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingActivity extends Fragment {

    public SettingActivity() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settingpage,container,false);
        initState(view);

        final CheckBox checkBox_auto = (CheckBox)view.findViewById(R.id.check_auto_login) ;
        final CheckBox checkBox_sound = (CheckBox)view.findViewById(R.id.check_sound_alarm) ;
        final CheckBox checkBox_vibe = (CheckBox)view.findViewById(R.id.check_vibe_alarm) ;
        final CheckBox checkBox_write = (CheckBox)view.findViewById(R.id.check_write_review_alarm) ;
        final CheckBox checkBox_comment = (CheckBox)view.findViewById(R.id.check_comment_alarm) ;
        Button menuAlarm = (Button)view.findViewById(R.id.button_set_menu_alarm);
        Button saveState = (Button)view.findViewById(R.id.button_save_setting_state);
        saveState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sf = getActivity().getSharedPreferences("setting",MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.clear();

                editor.putBoolean("auto_login",checkBox_auto.isChecked());
                editor.putBoolean("sound",checkBox_sound.isChecked());
                editor.putBoolean("vibe",checkBox_vibe.isChecked());
                editor.putBoolean("comment",checkBox_comment.isChecked());
                editor.putBoolean("write",checkBox_write.isChecked());

                editor.commit();
            }
        });

        checkBox_auto.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(),"자동로그인 설정됨.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"자동로그인 설정해제됨.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox_sound.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(),"소리알림 설정됨.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"소리알림 설정해제됨.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox_vibe.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(),"진동알림 설정됨.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"진동알림 설정해제됨.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox_write.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(),"리뷰쓰기알림 설정됨.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"리뷰쓰기알림 설정해제됨.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox_comment.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(),"리뷰댓글알림 설정됨.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"리뷰댓글알림 설정해제됨.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        menuAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),MenuAlarmSettingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void initState(View view) {
        SharedPreferences sf = getActivity().getSharedPreferences("setting",MODE_PRIVATE);
        CheckBox checkBox_auto = (CheckBox)view.findViewById(R.id.check_auto_login) ;
        CheckBox checkBox_sound = (CheckBox)view.findViewById(R.id.check_sound_alarm) ;
        CheckBox checkBox_vibe = (CheckBox)view.findViewById(R.id.check_vibe_alarm) ;
        CheckBox checkBox_write = (CheckBox)view.findViewById(R.id.check_write_review_alarm) ;
        CheckBox checkBox_comment = (CheckBox)view.findViewById(R.id.check_comment_alarm) ;

        checkBox_auto.setChecked(sf.getBoolean("auto_login",false));
        checkBox_sound.setChecked(sf.getBoolean("sound",false));
        checkBox_vibe.setChecked(sf.getBoolean("vibe",false));
        checkBox_write.setChecked(sf.getBoolean("write",false));
        checkBox_comment.setChecked(sf.getBoolean("comment",false));
    }

}
