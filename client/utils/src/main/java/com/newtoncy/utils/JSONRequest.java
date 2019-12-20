package com.newtoncy.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONRequest {

    interface Callback {
        void success(JSONObject jsonObject);
        void fail(Response response, int reason, Object e);
    }
    public static void call(URL url, final JSONObject jsonObject, final Callback callback){

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(ServerURL.JSON, jsonObject.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(
                new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        callback.fail(null,ERR_CODE.NET_ERR,e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        if(response.code()!=200){
                            callback.fail(response,ERR_CODE.RESPONSE_ERR,null);
                        }
                        JSONObject responseJS = null;
                        try {
                            responseJS = new JSONObject(response.body().string());
                            if ("OK".equals((String) responseJS.getString("code")))
                                callback.success(responseJS);
                            else
                                callback.fail(response, ERR_CODE.FORBIDDEN_ERR,responseJS.getString("reason"));
                        } catch (JSONException e) {
                            callback.fail(response,ERR_CODE.FORMAT_ERR,e);
                        } catch (IOException e) {
                            callback.fail(response,ERR_CODE.FORMAT_ERR,e);
                            e.printStackTrace();
                        }

                    }
                }
        );
    }
}
