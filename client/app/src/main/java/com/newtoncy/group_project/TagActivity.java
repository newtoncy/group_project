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

import com.newtoncy.idcardcamera.camera.CameraActivity;
import com.newtoncy.utils.Login;
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

    ImageView  imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        imgPath = getIntent().getStringExtra("imgPath");
        imageView = findViewById(R.id.img_submit);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
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
        if(requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE)
            imgPath = CameraActivity.getImagePath(data);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
    }

    public void submit(View view) throws JSONException, IOException {
        EditText summary = findViewById(R.id.summary);
        EditText detail = findViewById(R.id.detail);
        JSONObject jsonObject = buildJSON(imgPath,summary.getText().toString(),detail.getText().toString());

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonObject.toString(),ServerURL.JSON);
        Request request = new Request.Builder().url(ServerURL.getURL(ServerURL.uploadPath)).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(TagActivity.this, "网络错误："+e.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.code() == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TagActivity.this, "成功！", Toast.LENGTH_SHORT).show();
                            TagActivity.this.finish();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TagActivity.this, "错误：" + response.code(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });


    }

    protected JSONObject buildJSON(String imgPath, String tag, String comment) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("userID", Login.getUserProfile().uid);
        jsonObject.put("tag", tag);
        jsonObject.put("comment", comment);
        FileInputStream fileInputStream = new FileInputStream(imgPath);
        byte[] imgByte = new byte[fileInputStream.available()];
        int foo = fileInputStream.read(imgByte);
        String imgBase64 = Base64.encodeToString(imgByte, Base64.DEFAULT);
        jsonObject.put("imgBase64", imgBase64);
        return jsonObject;
    }

    protected  Intent createReturnIntent(){
        return null;
    }

    protected String imgPath = null;
    protected int coverID = 0;
    protected boolean coverIDFlag = false;

    public static void toActivity(Activity activity, String imgPath, int coverID){
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("coverID", coverID);

    }

    public static void toActivity(Activity activity, String imgPath) {
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra("imgPath", imgPath);
        activity.startActivity(intent);
    }

    public void cancel(View view) {
        finish();
    }
}
