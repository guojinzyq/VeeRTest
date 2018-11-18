package com.example.a15711.veertest.util;

import android.text.TextUtils;

import com.example.a15711.veertest.data.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15711 on 2018/11/16.
 */

public class Utility {

    /**
     * 解析并处理json对象
     */
    public static List<Data> handleJson(String jsonString){
        List<Data> dataList=new ArrayList<>();
        String url="http://cn.bing.com/az/hprichbg/rb/SpiritBearSleeps_ZH-CN7690026884_1920x1080.jpg";
        if(!TextUtils.isEmpty(jsonString)){
            try{
                JSONObject jsonObject=new JSONObject(jsonString);
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object=jsonArray.getJSONObject(i);
                    Data data=new Data();
                    data.setId(object.getInt("id"));
                    data.setThumbUrl(url);
                    data.setTitle(object.getString("title"));
                    data.setCategory(object.getString("category"));
                    data.setPageUrl(object.getString("page_url"));
                    dataList.add(data);
                }
                return dataList;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return dataList;
    }
}
