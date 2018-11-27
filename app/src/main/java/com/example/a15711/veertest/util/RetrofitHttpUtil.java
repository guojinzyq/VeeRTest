package com.example.a15711.veertest.util;

import android.widget.Toast;

import okhttp3.Callback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitHttpUtil {
    public static void requestFormUrl(retrofit2.Callback<ResponseBody> callback){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://mingke.veervr.tv:1920/")
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<ResponseBody> call=request.getCall();
        call.enqueue(callback);
//        call.enqueue(new retrofit2.Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try{
//                    str=response.body().string();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(MyApplication.getContext(),"连接失败",Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
