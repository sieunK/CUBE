package com.example.cube;

import android.app.Application;

public class CurrentApplication extends Application {
    private String Nickname;
    private String Email;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        super.onCreate();
        Nickname = null;
        Email = null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public String toString() {
        return "CurrentUserInfo{" +
                "Nickname='" + Nickname + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
