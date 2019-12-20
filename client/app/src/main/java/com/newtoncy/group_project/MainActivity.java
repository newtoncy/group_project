package com.newtoncy.group_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.renkai.login_test.Login_Activity;
import com.example.renkai.login_test.UserInfo2Activity;
import com.newtoncy.idcardcamera.camera.CameraActivity;
import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFail;
import com.newtoncy.utils.UserProfile;

import okhttp3.Response;

/**
 * Author   wildma
 * Github   https://github.com/wildma
 * Date     2018/6/24
 * Desc     ${身份证相机使用例子}
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text_cover).setVisibility(View.VISIBLE);
        findViewById(R.id.img_cover).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Login.isLogin()){
            SharedPreferences sharedPreferences = getSharedPreferences("pwd",MODE_PRIVATE);
            String password = sharedPreferences.getString("password",null);
            String uid = sharedPreferences.getString("uid",null);
            if(uid != null && password != null){
                Login login  = new Login(new RequestFail() {
                    @Override
                    public void onFail(Response response, int reason, Object e) {
                        Login_Activity.toActivity(MainActivity.this);
                    }
                });
                login.login(uid, password, new Login.Callback() {
                    @Override
                    public void success(UserProfile userProfile) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.text_cover).setVisibility(View.GONE);
                                findViewById(R.id.img_cover).setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }

        }
    }

    /**
     * 拍照
     */
    public void frontIdCard(View view) {
        CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_FRONT);
    }

    /**
     * 图像处理
     */
    public void backIdCard(View view) {
        if(!TextUtils.isEmpty(path))
            PicDealActivity.toActivity(this,path);
    }
    Bitmap m_bitmap;
    String path;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            //获取图片路径，显示图片
            path = CameraActivity.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
               TagActivity.toActivity(this,path);
            }
        }
    }

    public void userInfo(View view) {
        UserInfo2Activity.toActivity(this);
    }
}
