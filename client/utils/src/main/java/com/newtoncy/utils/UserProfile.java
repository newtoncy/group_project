package com.newtoncy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfile {
    String userName;
    String password;
    String uid;

    public UserProfile(String uid, String userName, String password) {
        this.uid = uid;
        this.userName = userName;
        this.password = password;
    }

    public void savePwd(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("pwd", Context.MODE_PRIVATE).edit();
        editor.putString("uid",uid);
        editor.putString("password",password);
        editor.apply();
    }
}
