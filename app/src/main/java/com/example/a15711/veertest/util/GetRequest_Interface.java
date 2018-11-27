package com.example.a15711.veertest.util;

import com.example.a15711.veertest.data.Data;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetRequest_Interface {
    @GET("test")
    Call<ResponseBody> getCall();
}
