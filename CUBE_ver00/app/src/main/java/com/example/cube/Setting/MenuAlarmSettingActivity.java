package com.example.cube.Setting;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cube.R;
import com.ssomai.android.scalablelayout.ScalableLayout;

public class MenuAlarmSettingActivity extends AppCompatActivity {
    CheckBox geumJeong;
    CheckBox moonChang;
    CheckBox saetBul;
    CheckBox hakSaeng;

    CheckBox GJ_morning;
    CheckBox MC_morning;
    CheckBox SB_morning;
    CheckBox HS_morning;

    CheckBox GJ_lunch;
    CheckBox MC_lunch;
    CheckBox SB_lunch;
    CheckBox HS_lunch;

    CheckBox GJ_dinner;
    CheckBox MC_dinner;
    CheckBox SB_dinner;
    CheckBox HS_dinner;

    ScalableLayout GJ_line;
    ScalableLayout GJ_option;
    ScalableLayout MC_line;
    ScalableLayout MC_option;
    ScalableLayout SB_line;
    ScalableLayout SB_option;
    ScalableLayout HS_line;
    ScalableLayout HS_option;

    Button saveState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menualarm_setting);

        setInitialCheckBox();
        setButtonListener();
        saveState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sf = getSharedPreferences("menuAlarm",MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.clear();

                geumJeong = (CheckBox)findViewById(R.id.checkbox_geumjeong);
                moonChang = (CheckBox)findViewById(R.id.checkbox_moonchang);
                saetBul = (CheckBox)findViewById(R.id.checkbox_saetbul);
                hakSaeng = (CheckBox)findViewById(R.id.checkbox_haksaeng);
                GJ_morning = (CheckBox)findViewById(R.id.checkbox_geumjeong_morning);
                MC_morning = (CheckBox)findViewById(R.id.checkbox_moonchang_morning);
                SB_morning = (CheckBox)findViewById(R.id.checkbox_saetbul_morning);
                HS_morning = (CheckBox)findViewById(R.id.checkbox_haksaeng_morning);
                GJ_lunch = (CheckBox)findViewById(R.id.checkbox_geumjeong_lunch);
                MC_lunch = (CheckBox)findViewById(R.id.checkbox_moonchang_lunch);
                SB_lunch = (CheckBox)findViewById(R.id.checkbox_saetbul_lunch);
                HS_lunch = (CheckBox)findViewById(R.id.checkbox_haksaeng_lunch);
                GJ_dinner = (CheckBox)findViewById(R.id.checkbox_geumjeong_dinner);
                MC_dinner = (CheckBox)findViewById(R.id.checkbox_moonchang_dinner);
                SB_dinner = (CheckBox)findViewById(R.id.checkbox_saetbul_dinner);
                HS_dinner = (CheckBox)findViewById(R.id.checkbox_haksaeng_dinner);

                editor.putBoolean("GJ",geumJeong.isChecked());
                editor.putBoolean("GJ_m",GJ_morning.isChecked());
                editor.putBoolean("GJ_l",GJ_lunch.isChecked());
                editor.putBoolean("GJ_d",GJ_dinner.isChecked());
                editor.putBoolean("MC",moonChang.isChecked());
                editor.putBoolean("MC_m",MC_morning.isChecked());
                editor.putBoolean("MC_l",MC_lunch.isChecked());
                editor.putBoolean("MC_d",MC_dinner.isChecked());
                editor.putBoolean("SB",saetBul.isChecked());
                editor.putBoolean("SB_m",SB_morning.isChecked());
                editor.putBoolean("SB_l",SB_lunch.isChecked());
                editor.putBoolean("SB_d",SB_dinner.isChecked());
                editor.putBoolean("HS",hakSaeng.isChecked());
                editor.putBoolean("HS_m",HS_morning.isChecked());
                editor.putBoolean("HS_l",HS_lunch.isChecked());
                editor.putBoolean("HS_d",HS_dinner.isChecked());

                if(moonChang.isChecked()) {
                    if(isServiceRunning()) {
                        Intent intent = new Intent(getApplicationContext(), MenuAlarmService.class);
                        stopService(intent);
                    }
                    Intent intent = new Intent(getApplicationContext(), MenuAlarmService.class);
                    startService(intent);

                } else if(!moonChang.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), MenuAlarmService.class);
                    stopService(intent);
                }

                editor.commit();
                Toast.makeText(getApplicationContext(),"현재 정보 저장됨.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (com.example.cube.Setting.MenuAlarmService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public void setInitialCheckBox() {
        SharedPreferences sf = getSharedPreferences("menuAlarm",MODE_PRIVATE);

        geumJeong = (CheckBox)findViewById(R.id.checkbox_geumjeong);
        moonChang = (CheckBox)findViewById(R.id.checkbox_moonchang);
        saetBul = (CheckBox)findViewById(R.id.checkbox_saetbul);
        hakSaeng = (CheckBox)findViewById(R.id.checkbox_haksaeng);
        GJ_morning = (CheckBox)findViewById(R.id.checkbox_geumjeong_morning);
        MC_morning = (CheckBox)findViewById(R.id.checkbox_moonchang_morning);
        SB_morning = (CheckBox)findViewById(R.id.checkbox_saetbul_morning);
        HS_morning = (CheckBox)findViewById(R.id.checkbox_haksaeng_morning);
        GJ_lunch = (CheckBox)findViewById(R.id.checkbox_geumjeong_lunch);
        MC_lunch = (CheckBox)findViewById(R.id.checkbox_moonchang_lunch);
        SB_lunch = (CheckBox)findViewById(R.id.checkbox_saetbul_lunch);
        HS_lunch = (CheckBox)findViewById(R.id.checkbox_haksaeng_lunch);
        GJ_dinner = (CheckBox)findViewById(R.id.checkbox_geumjeong_dinner);
        MC_dinner = (CheckBox)findViewById(R.id.checkbox_moonchang_dinner);
        SB_dinner = (CheckBox)findViewById(R.id.checkbox_saetbul_dinner);
        HS_dinner = (CheckBox)findViewById(R.id.checkbox_haksaeng_dinner);

        GJ_line = (ScalableLayout)findViewById(R.id.geumjeong_line);
        GJ_option = (ScalableLayout)findViewById(R.id.geumjeong_option);
        MC_line = (ScalableLayout)findViewById(R.id.moonchang_line);
        MC_option = (ScalableLayout)findViewById(R.id.moonchang_option);
        SB_line = (ScalableLayout)findViewById(R.id.saetbul_line);
        SB_option = (ScalableLayout)findViewById(R.id.saetbul_option);
        HS_line = (ScalableLayout)findViewById(R.id.haksaeng_line);
        HS_option = (ScalableLayout)findViewById(R.id.haksaeng_option);

        Boolean GJ = sf.getBoolean("GJ",false);
        Boolean GJ_m = sf.getBoolean("GJ_m",false);
        Boolean GJ_l = sf.getBoolean("GJ_l",false);
        Boolean GJ_d = sf.getBoolean("GJ_d",false);
        Boolean MC = sf.getBoolean("MC",false);
        Boolean MC_m = sf.getBoolean("MC_m",false);
        Boolean MC_l = sf.getBoolean("MC_l",false);
        Boolean MC_d = sf.getBoolean("MC_d",false);
        Boolean SB = sf.getBoolean("SB",false);
        Boolean SB_m = sf.getBoolean("SB_m",false);
        Boolean SB_l = sf.getBoolean("SB_l",false);
        Boolean SB_d = sf.getBoolean("SB_d",false);
        Boolean HS = sf.getBoolean("HS",false);
        Boolean HS_m = sf.getBoolean("HS_m",false);
        Boolean HS_l = sf.getBoolean("HS_l",false);
        Boolean HS_d = sf.getBoolean("HS_d",false);

        geumJeong.setChecked(GJ);
        moonChang.setChecked(MC);
        saetBul.setChecked(SB);
        hakSaeng.setChecked(HS);
        GJ_morning.setChecked(GJ_m);
        MC_morning.setChecked(MC_m);
        SB_morning.setChecked(SB_m);
        HS_morning.setChecked(HS_m);
        GJ_lunch.setChecked(GJ_l);
        MC_lunch.setChecked(MC_l);
        SB_lunch.setChecked(SB_l);
        HS_lunch.setChecked(HS_l);
        GJ_dinner.setChecked(GJ_d);
        MC_dinner.setChecked(MC_d);
        SB_dinner.setChecked(SB_d);
        HS_dinner.setChecked(HS_d);

        if(!geumJeong.isChecked()) {
            GJ_line.setVisibility(View.GONE);
            GJ_option.setVisibility(View.GONE);
        } else {
            GJ_line.setVisibility(View.VISIBLE);
            GJ_option.setVisibility(View.VISIBLE);
        }
        if(!moonChang.isChecked()) {
            MC_line.setVisibility(View.GONE);
            MC_option.setVisibility(View.GONE);
        } else {
            MC_line.setVisibility(View.VISIBLE);
            MC_option.setVisibility(View.VISIBLE);
        }
        if(!saetBul.isChecked()) {
            SB_line.setVisibility(View.GONE);
            SB_option.setVisibility(View.GONE);
        } else {
            SB_line.setVisibility(View.VISIBLE);
            SB_option.setVisibility(View.VISIBLE);
        }
        if(!hakSaeng.isChecked()) {
            HS_line.setVisibility(View.GONE);
            HS_option.setVisibility(View.GONE);
        } else {
            HS_line.setVisibility(View.VISIBLE);
            HS_option.setVisibility(View.VISIBLE);
        }
    }
    public void setButtonListener() {
        geumJeong = (CheckBox)findViewById(R.id.checkbox_geumjeong);
        moonChang = (CheckBox)findViewById(R.id.checkbox_moonchang);
        saetBul = (CheckBox)findViewById(R.id.checkbox_saetbul);
        hakSaeng = (CheckBox)findViewById(R.id.checkbox_haksaeng);
        GJ_morning = (CheckBox)findViewById(R.id.checkbox_geumjeong_morning);
        MC_morning = (CheckBox)findViewById(R.id.checkbox_moonchang_morning);
        SB_morning = (CheckBox)findViewById(R.id.checkbox_saetbul_morning);
        HS_morning = (CheckBox)findViewById(R.id.checkbox_haksaeng_morning);
        GJ_lunch = (CheckBox)findViewById(R.id.checkbox_geumjeong_lunch);
        MC_lunch = (CheckBox)findViewById(R.id.checkbox_moonchang_lunch);
        SB_lunch = (CheckBox)findViewById(R.id.checkbox_saetbul_lunch);
        HS_lunch = (CheckBox)findViewById(R.id.checkbox_haksaeng_lunch);
        GJ_dinner = (CheckBox)findViewById(R.id.checkbox_geumjeong_dinner);
        MC_dinner = (CheckBox)findViewById(R.id.checkbox_moonchang_dinner);
        SB_dinner = (CheckBox)findViewById(R.id.checkbox_saetbul_dinner);
        HS_dinner = (CheckBox)findViewById(R.id.checkbox_haksaeng_dinner);

        saveState = (Button)findViewById(R.id.button_save_menu_alarm_state);

        geumJeong.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    GJ_morning.setChecked(true);
                    GJ_lunch.setChecked(true);
                    GJ_dinner.setChecked(true);
                    GJ_line.setVisibility(View.VISIBLE);
                    GJ_option.setVisibility(View.VISIBLE);
                } else {
                    GJ_morning.setChecked(false);
                    GJ_lunch.setChecked(false);
                    GJ_dinner.setChecked(false);
                    GJ_line.setVisibility(View.GONE);
                    GJ_option.setVisibility(View.GONE);
                }
            }
        });
        moonChang.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    MC_morning.setChecked(true);
                    MC_lunch.setChecked(true);
                    MC_dinner.setChecked(true);
                    MC_line.setVisibility(View.VISIBLE);
                    MC_option.setVisibility(View.VISIBLE);
                } else {
                    MC_morning.setChecked(false);
                    MC_lunch.setChecked(false);
                    MC_dinner.setChecked(false);
                    MC_line.setVisibility(View.GONE);
                    MC_option.setVisibility(View.GONE);
                }
            }
        });
        saetBul.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    SB_morning.setChecked(true);
                    SB_lunch.setChecked(true);
                    SB_dinner.setChecked(true);
                    SB_line.setVisibility(View.VISIBLE);
                    SB_option.setVisibility(View.VISIBLE);
                } else {
                    SB_morning.setChecked(false);
                    SB_lunch.setChecked(false);
                    SB_dinner.setChecked(false);
                    SB_line.setVisibility(View.GONE);
                    SB_option.setVisibility(View.GONE);
                }
            }
        });
        hakSaeng.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    HS_morning.setChecked(true);
                    HS_lunch.setChecked(true);
                    HS_dinner.setChecked(true);
                    HS_line.setVisibility(View.VISIBLE);
                    HS_option.setVisibility(View.VISIBLE);
                } else {
                    HS_morning.setChecked(false);
                    HS_lunch.setChecked(false);
                    HS_dinner.setChecked(false);
                    HS_line.setVisibility(View.GONE);
                    HS_option.setVisibility(View.GONE);
                }
            }
        });
        GJ_morning.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!geumJeong.isChecked()) {
                        geumJeong.setChecked(true);
                    }
                } else {
                    if(!GJ_morning.isChecked() && !GJ_lunch.isChecked() && !GJ_dinner.isChecked()) {
                        if(geumJeong.isChecked()) {
                            geumJeong.setChecked(false);
                            GJ_line.setVisibility(View.GONE);
                            GJ_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        MC_morning.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!moonChang.isChecked()) {
                        moonChang.setChecked(true);
                    }
                } else {
                    if(!MC_morning.isChecked() && !MC_lunch.isChecked() && !MC_dinner.isChecked()) {
                        if(moonChang.isChecked()) {
                            moonChang.setChecked(false);
                            MC_line.setVisibility(View.GONE);
                            MC_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        SB_morning.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!saetBul.isChecked()) {
                        saetBul.setChecked(true);
                    }
                } else {
                    if(!SB_morning.isChecked() && !SB_lunch.isChecked() && !SB_dinner.isChecked()) {
                        if(saetBul.isChecked()) {
                            saetBul.setChecked(false);
                            SB_line.setVisibility(View.GONE);
                            SB_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        HS_morning.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!hakSaeng.isChecked()) {
                        hakSaeng.setChecked(true);
                    }
                } else {
                    if(!HS_morning.isChecked() && !HS_lunch.isChecked() && !HS_dinner.isChecked()) {
                        if(hakSaeng.isChecked()) {
                            hakSaeng.setChecked(false);
                            HS_line.setVisibility(View.GONE);
                            HS_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        GJ_lunch.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!geumJeong.isChecked()) {
                        geumJeong.setChecked(true);
                    }
                } else {
                    if(!GJ_morning.isChecked() && !GJ_lunch.isChecked() && !GJ_dinner.isChecked()) {
                        if(geumJeong.isChecked()) {
                            geumJeong.setChecked(false);
                            GJ_line.setVisibility(View.GONE);
                            GJ_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        MC_lunch.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!moonChang.isChecked()) {
                        moonChang.setChecked(true);
                    }
                } else {
                    if(!MC_morning.isChecked() && !MC_lunch.isChecked() && !MC_dinner.isChecked()) {
                        if(moonChang.isChecked()) {
                            moonChang.setChecked(false);
                            MC_line.setVisibility(View.GONE);
                            MC_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        SB_lunch.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!saetBul.isChecked()) {
                        saetBul.setChecked(true);
                    }
                } else {
                    if(!SB_morning.isChecked() && !SB_lunch.isChecked() && !SB_dinner.isChecked()) {
                        if(saetBul.isChecked()) {
                            saetBul.setChecked(false);
                            SB_line.setVisibility(View.GONE);
                            SB_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        HS_lunch.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!hakSaeng.isChecked()) {
                        hakSaeng.setChecked(true);
                    }
                } else {
                    if(!HS_morning.isChecked() && !HS_lunch.isChecked() && !HS_dinner.isChecked()) {
                        if(hakSaeng.isChecked()) {
                            hakSaeng.setChecked(false);
                            HS_line.setVisibility(View.GONE);
                            HS_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        GJ_dinner.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!geumJeong.isChecked()) {
                        geumJeong.setChecked(true);
                    }
                } else {
                    if(!GJ_morning.isChecked() && !GJ_lunch.isChecked() && !GJ_dinner.isChecked()) {
                        if(geumJeong.isChecked()) {
                            geumJeong.setChecked(false);
                            GJ_line.setVisibility(View.GONE);
                            GJ_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        MC_dinner.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!moonChang.isChecked()) {
                        moonChang.setChecked(true);
                    }
                } else {
                    if(!MC_morning.isChecked() && !MC_lunch.isChecked() && !MC_dinner.isChecked()) {
                        if(moonChang.isChecked()) {
                            moonChang.setChecked(false);
                            MC_line.setVisibility(View.GONE);
                            MC_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        SB_dinner.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!saetBul.isChecked()) {
                        saetBul.setChecked(true);
                    }
                } else {
                    if(!SB_morning.isChecked() && !SB_lunch.isChecked() && !SB_dinner.isChecked()) {
                        if(saetBul.isChecked()) {
                            saetBul.setChecked(false);
                            SB_line.setVisibility(View.GONE);
                            SB_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        HS_dinner.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    if(!hakSaeng.isChecked()) {
                        hakSaeng.setChecked(true);
                    }
                } else {
                    if(!HS_morning.isChecked() && !HS_lunch.isChecked() && !HS_dinner.isChecked()) {
                        if(hakSaeng.isChecked()) {
                            hakSaeng.setChecked(false);
                            HS_line.setVisibility(View.GONE);
                            HS_option.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }
}
