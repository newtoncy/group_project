package com.newtoncy.utils;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class Login {
    private Callback callback = new Callback() {
        @Override
        public void success(UserProfile userProfile) {
            //do nothing
        }
    };
    private RequestFail requestFail;
    private static boolean loginFlag = false;
    private static UserProfile userProfile = null;

    public Login(Callback callback,RequestFail requestFail){
        this.callback = callback;
        this.requestFail = requestFail;
    }
    public Login(RequestFail requestFail){
        this.requestFail = requestFail;
    }

    public static UserProfile getUserProfile() {
        return userProfile;
    }


    public static boolean isLogin() {
        return loginFlag;
    }
    public interface Callback{
        void success(UserProfile userProfile);
    }
    public void login(String uid, String password){
        login(uid,password,callback);
    }
    public  void login(String uid, final String password, final Callback callback) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", uid);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONRequest.call(ServerURL.getURL(ServerURL.signIn),jsonObject, new JSONRequest.Callback() {
            @Override
            public void success(JSONObject jsonObject) {
                loginFlag = true;
                try {
                    userProfile = new UserProfile(
                            jsonObject.getString("uid"),
                            jsonObject.getString("userName"),
                            password);
                    callback.success(userProfile);

                } catch (JSONException e) {
                    requestFail.onFail(null,ERR_CODE.FORMAT_ERR,e);
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(Response response, int reason, Object e) {
                requestFail.onFail(response, reason, e);
            }
        });

    }
    public void signUp(final UserProfile userProfile){
        signUp(userProfile,callback);
    }
    public void signUp(@NotNull final UserProfile userProfile, final Callback callback) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userProfile.uid);
            jsonObject.put("password", userProfile.password);
            jsonObject.put("userName", userProfile.userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONRequest.call(ServerURL.getURL(ServerURL.signUp),jsonObject,new JSONRequest.Callback(){

            @Override
            public void success(JSONObject jsonObject) {
                Login.userProfile = userProfile;
                Login.loginFlag = true;
                callback.success(userProfile);
            }

            @Override
            public void fail(Response response, int reason, Object e) {
                requestFail.onFail(response,reason,e);
            }
        });
    }
    public static void logout(){
        loginFlag=false;
    }
}
