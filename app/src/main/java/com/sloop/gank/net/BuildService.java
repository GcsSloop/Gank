package com.sloop.gank.net;


import com.sloop.gank.constant.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * <ul type="disc">
 * <li>Author: Sloop</li>
 * <li>Version: v1.0.0</li>
 * <li>Date: 2016/3/8</li>
 * <li>Copyright (c) 2015 GcsSloop</li>
 * <li><a href="http://weibo.com/GcsSloop" target="_blank">WeiBo</a>      </li>
 * <li><a href="https://github.com/GcsSloop" target="_blank">GitHub</a>   </li>
 * </ul>
 */
public class BuildService {

    private static Retrofit retrofit;

    public static GankService getGankService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.GANK_URL)                         //设置Base的访问路径
                    .client(defaultOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create()) //设置默认的解析库：Gson
                    .build();
        }
        return retrofit.create(GankService.class);
    }

    public static OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5 , TimeUnit.SECONDS);
        client.setReadTimeout(5    , TimeUnit.SECONDS);
        client.setWriteTimeout(10  , TimeUnit.SECONDS);
        return client;
    }
}
