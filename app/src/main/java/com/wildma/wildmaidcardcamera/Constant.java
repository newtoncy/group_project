package com.wildma.wildmaidcardcamera;

import java.net.URL;
import java.net.URLEncoder;

import okhttp3.MediaType;

public class Constant {
    static final String protocol = "http";
    static final String host = "10.1.1.111";
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
