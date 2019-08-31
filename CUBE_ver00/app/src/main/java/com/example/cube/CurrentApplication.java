package com.example.cube;

import android.app.Application;

public class CurrentApplication extends Application {
    private String Nickname;
    private String Email;
    private String profileImage;
    private boolean isAdmin;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        super.onCreate();
        Nickname = null;
        Email = null;
        isAdmin = false;
        profileImage = null;
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
                ", isAdmin='" + isAdmin + '\'' +
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getProfileImage() { return profileImage; }
}
