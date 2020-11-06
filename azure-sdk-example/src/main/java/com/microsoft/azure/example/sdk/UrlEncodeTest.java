package com.microsoft.azure.example.sdk;

import okhttp3.HttpUrl;

import java.net.URLEncoder;

public class UrlEncodeTest {

    public static void main(String[] args) {
        String s = "http://s/a b";
        System.out.println(URLEncoder.encode(s));
        System.out.println(HttpUrl.get(s).toString());
    }
}
