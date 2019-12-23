package com.newtoncy.group_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.newtoncy.group_project.javaclass.ImgInfo;
import com.newtoncy.idcardcamera.camera.CameraActivity;
import com.newtoncy.utils.ERR_CODE;
import com.newtoncy.utils.JSONRequest;
import com.newtoncy.utils.Login;
import com.newtoncy.utils.RequestFail;
import com.newtoncy.utils.RequestFailImp;
import com.newtoncy.utils.ServerURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TagActivity extends AppCompatActivity {

    private ImageView  imageView;
    private EditText tag = null;
    private EditText comment = null;
    private ImgInfo imgInfo=null;
    protected String imgPath = null; //图片路径。。可能是个网上的路径
    protected boolean pathOnLocal = false; // 表示图片路径在本地。那么应该上传
    protected int coverID = 0;
    protected boolean coverIDFlag = false;
    public static final int CODE_FAIL = 0;
    public static final int CODE_ACCEPT = 1;
    private static final int METHOD_UPDATE = 2;
    private static final int METHOD_CREATE = 3;

    private RequestFail requestFail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        requestFail = new RequestFailImp(this);
        tag = findViewById(R.id.summary);
        comment = findViewById(R.id.detail);
        Intent intent = getIntent();
        imageView = findViewById(R.id.img_submit);
        int method = intent.getIntExtra("method",METHOD_CREATE);
        if(method == METHOD_CREATE){
            pathOnLocal = true;
            coverIDFlag = false;
            imgPath = getIntent().getStringExtra("imgPath");
            imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
//            Glide.with(this).load(imgPath).into(imageView);
        }else if(method == METHOD_UPDATE){
            imgInfo = (ImgInfo) intent.getSerializableExtra("imgInfo");
            pathOnLocal = false;
            imgPath = imgInfo.imgPath;
            Glide.with(this).load(ServerURL.getURL(imgPath)).into(imageView);
            tag.setText(imgInfo.tag);
            comment.setText(imgInfo.comment);
            coverID = imgInfo.id;
            coverIDFlag = true;
        }else{
            throw new IllegalArgumentException("参数错误！");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraActivity.toCameraActivity(TagActivity.this,CameraActivity.TYPE_IDCARD_FRONT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            imgPath = CameraActivity.getImagePath(data);
            if(imgPath!=null) {
                pathOnLocal = true;
                imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
            }
        }

    }

    public void submit(final View view) throws JSONException, IOException {

        JSONObject jsonObject = buildJSON(imgPath, tag.getText().toString(), comment.getText().toString());
        view.setEnabled(false);
        requestFail.setCallback(new RequestFail.Callback() {
            @Override
            public void fail() {
                view.setEnabled(true);
            }
        });
        JSONRequest.call(ServerURL.getURL(ServerURL.uploadPath), jsonObject, new JSONRequest.Callback() {
            @Override
            public void success(final JSONObject jsonObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ImgInfo imgInfo = new ImgInfo();
                            imgInfo.id = jsonObject.getInt("id");
                            imgInfo.imgPath = jsonObject.getString("imgPath");
                            imgInfo.tag = tag.getText().toString();
                            imgInfo.comment = comment.getText().toString();
                            Intent intent = new Intent();
                            intent.putExtra("imgInfo",imgInfo);
                            setResult(CODE_ACCEPT,intent);
                            finish();
                            view.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestFail.onFail(jsonObject, ERR_CODE.FORMAT_ERR,e);
                            view.setEnabled(true);
                        }
                        Toast.makeText(TagActivity.this,"上传成功！",Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void fail(Response response, int reason, Object e) {
                requestFail.onFail(response,reason,e);
            }
        });

    }

    protected JSONObject buildJSON(String imgPath, String tag, String comment) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        if(coverIDFlag)
            jsonObject.put("coverID",coverID);
        jsonObject.put("userID", Login.getUserProfile().uid);
        jsonObject.put("tag", tag);
        jsonObject.put("comment", comment);
        if (pathOnLocal) {
            FileInputStream fileInputStream = new FileInputStream(imgPath);
            byte[] imgByte = new byte[fileInputStream.available()];
            int foo = fileInputStream.read(imgByte);
            String imgBase64 = Base64.encodeToString(imgByte, Base64.DEFAULT);
            jsonObject.put("imgBase64", imgBase64);
        }

        return jsonObject;
    }

    public static void toActivity(Activity activity, ImgInfo imgInfo,int requestCode){
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra("imgInfo",imgInfo);
        intent.putExtra("method", METHOD_UPDATE);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void toActivity(Activity activity, String imgPath,int requestCode) {
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("method",METHOD_CREATE);
        activity.startActivityForResult(intent,requestCode);
    }

    public void cancel(View view) {
        setResult(CODE_FAIL);
        finish();
    }

    public static ImgInfo getImgInfo(Intent data){
        return (ImgInfo) data.getSerializableExtra("imgInfo");
    }
}
