package com.newtoncy.utils;



import okhttp3.Response;

public interface RequestFail {
    void onFail(Response response, int reason, Object e);
}
