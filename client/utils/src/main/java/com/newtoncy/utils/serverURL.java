package com.newtoncy.utils;

import java.net.URL;

import okhttp3.MediaType;

public class serverURL {
    static final String protocol = "http";
    static final String host = "10.1.1.111";
    static final int port = 5000;
    static final String uploadPath = "/upload";
    static final String signIn = "/sign_in";
    static final String signUp = "/sign_up";
    static URL getURL(String path){
        URL url = null;
        try {
            url = new URL(protocol,host,port,path);
        }catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

}
