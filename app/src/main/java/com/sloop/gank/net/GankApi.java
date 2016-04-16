package com.sloop.gank.net;

import com.sloop.gank.callback.ICallBack;
import com.sloop.gank.bean.CommonDate;
import com.sloop.gank.log.L;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
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
public class GankApi {

    public static Call<CommonDate> getCommonData(final String type, final int count, final int pageIndex, final ICallBack<CommonDate> callBack) {
        Call<CommonDate> commonDate = BuildService.getGankService().getCommonDate(type, count, pageIndex);
        final String key = type+count+pageIndex;

        commonDate.enqueue(new Callback<CommonDate>() {
            @Override
            public void onResponse(Response<CommonDate> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    CommonDate commonDate = response.body();
                    L.i("getCommonData---onResponse：" + commonDate.toString());
                    if (!commonDate.isError()) {
                        //数据正确，把数据返回
                        callBack.onSuccess(type, key, commonDate);
                    } else {
                        //数据错误
                        callBack.onFailure(type, key, "数据错误");
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                L.i("getCommonData-----onFailure：" + t.toString());
                //数据错误
                callBack.onFailure(type, key, "请求失败");
            }
        });
        return commonDate;
    }
}