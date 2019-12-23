package com.newtoncy.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.Response;

public class RequestFailImp implements RequestFail {
    private Activity activity;
    public RequestFailImp(Activity activity){
        this.activity = activity;
    }
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    @Override
    public void onFail(final Response response, final int reason, final Object e) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (reason){
                    case ERR_CODE.NET_ERR:
                        Toast.makeText(activity,"网络异常:"+e.toString(),Toast.LENGTH_LONG).show();
                        break;
                    case ERR_CODE.FORBIDDEN_ERR:
                        Toast.makeText(activity,"服务器拒绝:"+e.toString(),Toast.LENGTH_LONG).show();
                        break;
                    case ERR_CODE.Exception:
                        Toast.makeText(activity,"异常:"+e.toString(),Toast.LENGTH_LONG).show();
                        ((Exception)e).printStackTrace();
                        break;
                    case ERR_CODE.FORMAT_ERR:
                        Toast.makeText(activity,"服务器返回格式错误:"+e.toString(),Toast.LENGTH_LONG).show();
                        break;
                    case ERR_CODE.RESPONSE_ERR:
                        Toast.makeText(activity,"服务器返回错误号:"+response.code(),Toast.LENGTH_LONG).show();
                        break;
                    default:
                    case ERR_CODE.OTHER_ERR:
                        ((Exception)e).printStackTrace();
                        break;
                }
                if(callback!=null)
                    callback.fail();
            }
        });

    }

    @Override
    public void onFail(JSONObject response, int reason, Object e) {
        onFail((Response) null,reason,e);
    }
}
