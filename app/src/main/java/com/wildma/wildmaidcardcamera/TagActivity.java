package com.wildma.wildmaidcardcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wildma.idcardcamera.camera.CameraActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        imgPath = getIntent().getStringExtra("imgPath");
        ImageView  imageView = findViewById(R.id.img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
    }


    public void submit(View view) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        EditText summary = findViewById(R.id.summary);
        EditText detail = findViewById(R.id.detail);
        jsonObject.put("userID", 1);
        jsonObject.put("tag", summary.getText());
        jsonObject.put("comment", detail.getText());
        FileInputStream fileInputStream = new FileInputStream(imgPath);
        byte[] imgByte = new byte[fileInputStream.available()];
        int foo = fileInputStream.read(imgByte);
        String imgBase64 = Base64.encodeToString(imgByte, Base64.DEFAULT);
        jsonObject.put("imgBase64", imgBase64);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder().url(Constant.getUploadURL()).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(TagActivity.this, "网络错误："+e.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200)
                    Toast.makeText(TagActivity.this, "成功！", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(TagActivity.this, "错误："+response.code(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private String imgPath = null;

    public static void toActivity(Activity activity, String imgPath) {
        Intent intent = new Intent(activity, TagActivity.class);
        intent.putExtra("imgPath", imgPath);
        activity.startActivity(intent);
    }
}
