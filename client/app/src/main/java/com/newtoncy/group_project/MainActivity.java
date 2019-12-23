package com.newtoncy.group_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.renkai.login_test.Login_Activity;
import com.example.renkai.login_test.UserInfo2Activity;
import com.newtoncy.group_project.adapter.EndlessScrollDataLoader;
import com.newtoncy.group_project.adapter.ImgListAdapter;
import com.newtoncy.group_project.adapter.EndlessScrollListener;
import com.newtoncy.group_project.javaclass.ImgInfo;
import com.newtoncy.idcardcamera.camera.CameraActivity;
import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFail;
import com.newtoncy.utils.RequestFailImp;
import com.newtoncy.utils.UserProfile;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Response;

/**
 * Author   newtoncy(王超逸)
 * Date     2018/6/24
 * Desc     ${主界面}
 */
public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CREATE = 0;
    private final int REQUEST_UPDATE = 1;
    private int updatePosition = -1;
    private List<ImgInfo> imgInfoList;
    private EndlessScrollListener endlessScrollListener;
    private EndlessScrollDataLoader endlessScrollDataLoader;
    private ImgListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Login.isLogin()){
            findViewById(R.id.text_cover).setVisibility(View.VISIBLE);
            findViewById(R.id.img_cover).setVisibility(View.VISIBLE);
        }
        final RecyclerView  recyclerView = findViewById(R.id.rv_img_list);
        //设置recycleView
        imgInfoList = new ArrayList<>();
        adapter = new ImgListAdapter(this, imgInfoList, new ImgListAdapter.Callback() {
            @Override
            public void onClick(View view, ImgInfo imgInfo,int position) {
                updatePosition = position;
                TagActivity.toActivity(MainActivity.this,imgInfo,REQUEST_UPDATE);
            }
        });
        recyclerView.setAdapter(adapter);
        endlessScrollDataLoader = new EndlessScrollDataLoader(this,imgInfoList,adapter);
        endlessScrollListener = new EndlessScrollListener(endlessScrollDataLoader);
        recyclerView.addOnScrollListener(endlessScrollListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查登录与自动登录
        if(!Login.isLogin()){
            SharedPreferences sharedPreferences = getSharedPreferences("pwd",MODE_PRIVATE);
            String password = sharedPreferences.getString("password",null);
            String uid = sharedPreferences.getString("uid",null);
            if(uid != null && password != null){
                Login login  = new Login(new RequestFailImp(this) {
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
                                endlessScrollDataLoader.loadMore();
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            //获取图片路径，显示图片
            path = CameraActivity.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                TagActivity.toActivity(this, path, REQUEST_CREATE);
            }
        }
        if(requestCode == REQUEST_CREATE){
            if(resultCode == TagActivity.CODE_FAIL)
                return;
            imgInfoList.add(0, TagActivity.getImgInfo(data));
            adapter.notifyItemInserted(0);
        }
        if(requestCode == REQUEST_UPDATE){
            if(resultCode == TagActivity.CODE_FAIL)
                return;
            imgInfoList.set(updatePosition,TagActivity.getImgInfo(data));
            adapter.notifyItemChanged(updatePosition);
        }
    }

    public void userInfo(View view) {
        UserInfo2Activity.toActivity(this);
    }
}
