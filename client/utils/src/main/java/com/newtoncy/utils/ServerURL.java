package com.newtoncy.utils;

import java.net.URL;

import okhttp3.MediaType;

public class ServerURL {
    public static final String protocol = "http";
    public static final String host = "10.1.1.111";
    public static final int port = 5000;
    public static final String uploadPath = "/upload";
    public static final String signIn = "/sign_in";
    public static final String signUp = "/sign_up";
    public static URL getURL(String path){
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
