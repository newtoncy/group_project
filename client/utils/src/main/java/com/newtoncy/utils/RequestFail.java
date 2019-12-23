package com.newtoncy.utils;



import org.json.JSONObject;

import okhttp3.Response;

/**
 * reason 为ERR_CODE中定义的一个常数
 * e 用来描述详细的原因，通常为一个Exception对象，偶尔是一个字符串
 */
public interface RequestFail {
    void onFail(Response response, int reason, Object e);
    void onFail(JSONObject response, int reason, Object e);
    interface Callback{
        void fail();
    }
    void setCallback(Callback callback);
}
