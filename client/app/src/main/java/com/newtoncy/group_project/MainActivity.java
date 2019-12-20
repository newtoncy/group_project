package com.newtoncy.group_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.renkai.login_test.Login_Activity;
import com.newtoncy.idcardcamera.camera.CameraActivity;

/**
 * Author   wildma
 * Github   https://github.com/wildma
 * Date     2018/6/24
 * Desc     ${身份证相机使用例子}
 */
public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv_image);
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
                m_bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(m_bitmap);
                Button tp = findViewById(R.id.bt_take_photo);
                Button show = findViewById(R.id.bt_picture_deal);
                tp.setText("重新拍摄");
                show.setVisibility(View.VISIBLE);
            }
        }
    }

    public void login(View view) {
        Login_Activity.toActivity(this);
    }
}
