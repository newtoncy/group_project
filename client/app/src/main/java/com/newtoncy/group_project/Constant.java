package com.newtoncy.group_project;

import java.net.URL;

import okhttp3.MediaType;

public class Constant {
    static final String protocol = "http";
    static final String host = "172.20.10.4";
    static final int port = 5000;
    static final String uploadPath = "/upload";
    static URL getUploadURL(){
        URL url = null;
        try {
            url = new URL(protocol,host,port,uploadPath);
        }catch (Exception e){
            e.printStackTrace();
        }

        return url;
    }
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

}
