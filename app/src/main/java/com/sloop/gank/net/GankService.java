package com.sloop.gank.net;


import com.sloop.gank.bean.CommonDate;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

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
public interface GankService {

    //http://gank.io/api/data/Android/10/1
    @GET("data/{type}/{count}/{pageIndex}")
    Call<CommonDate> getCommonDate(@Path("type") String type,
                                   @Path("count") int count,
                                   @Path("pageIndex") int pageIndex
    );
}
