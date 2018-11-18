package com.example.a15711.veertest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a15711.veertest.R;
import com.example.a15711.veertest.adapter.DataAdapter;
import com.example.a15711.veertest.data.Data;
import com.example.a15711.veertest.util.HttpUtil;
import com.example.a15711.veertest.util.MyApplication;
import com.example.a15711.veertest.util.UpLoad;
import com.example.a15711.veertest.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomepageActivity extends AppCompatActivity {

    //API接口
    private final String API="http://mingke.veervr.tv:1920/test";
    //缓存从接口中读得的json字符串
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //从API得到的数据列表
    List<Data> dataList=new ArrayList<Data>();
    //显示在一页中的数据列表
    List<Data> datas=new ArrayList<Data>();
    //记录刷新次数
    int refresh=0;
    //刷新状态
    boolean refreshStatus=false;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    DataAdapter dataAdapter;

    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{ progressBar.setVisibility(View.VISIBLE);refreshStatus=true;break;}
                case 2:{
                    boolean res=msg.getData().getBoolean("result");
                    if(res==true){
                        dataAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(0);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(HomepageActivity.this,"加载下一页成功",Toast.LENGTH_SHORT).show();
                    }
                    refreshStatus=false;
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout=findViewById(R.id.down_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        progressBar=findViewById(R.id.progressbar);
        recyclerView=findViewById(R.id.veer_recycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        String jsonStr=pref.getString("jsonString",null);
        if(jsonStr!=null){
            dataList.addAll(Utility.handleJson(jsonStr));
        }else{
            queryFromApi(API);
        }
        if(!setRefreshData()){
            Toast.makeText(this,"没有更多数据了",Toast.LENGTH_SHORT).show();
        }
        if(dataList.size()>0){
            Log.d("HomepageActivity","dataList不为空");
        }
        if(datas.size()>0){
            Log.d("HomepageActivity","datas不为空");
        }
        dataAdapter=new DataAdapter(datas);
        recyclerView.setAdapter(dataAdapter);
        //用户点击事件监听接口
        dataAdapter.setOnClickListener(new DataAdapter.OnClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Intent intent=new Intent(HomepageActivity.this,DetailpageActivity.class);
                Data data=datas.get(position);
                intent.putExtra("page_url",data.getPageUrl());
                intent.putExtra("title",data.getTitle());
                startActivity(intent);
            }
        });
        //下拉刷新监听接口
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        //上拉刷新监听接口
        recyclerView.addOnScrollListener(new UpLoad(new UpLoad.ScrollListener(){
            @Override
            public void loadMore() {
                if(!refreshStatus){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message1=new Message();
                            message1.what=1;
                            handler.sendMessage(message1);
                            try{
                                //本地加载很快可能看不到进度框
                                Thread.sleep(1500);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            boolean result=setRefreshData();
                            Message message2=new Message();
                            message2.what=2;
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("result",result);
                            message2.setData(bundle);
                            handler.sendMessage(message2);
                        }
                    }).start();

                }
            }
        }));
    }
    //根据刷新次数更新显示当前页的数据列表
    public boolean setRefreshData(){
        if(refresh*6>=dataList.size()){
            return false;
        }
        datas.clear();
        //  取6条数据即可
        int count=refresh*6+6;
        for(int i=refresh*6;i<dataList.size()&&i<count;i++){
            datas.add(dataList.get(i));
        }
        refresh++;
        return true;
    }

    //从API中查询数据
    public void queryFromApi(String url) {
        Log.d("queryFromApi","before");
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomepageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        Log.d("queryFromApi","onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                editor=pref.edit();
                editor.putString("jsonString",responseText);
                editor.apply();
                dataList.addAll(Utility.handleJson(responseText));
                Log.d("queryFromApi","onResponse");
            }
        });
        Log.d("queryFromApi","after");
    }
    //刷新，从API中重新获取一次数据并显示
    public void refreshData(){
        HttpUtil.sendOkHttpRequest(API, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomepageActivity.this,"刷新失败！",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                editor=pref.edit();
                editor.putString("jsonString",responseText);
                editor.apply();
                dataList.clear();
                dataList.addAll(Utility.handleJson(responseText));
                refresh=0;
                setRefreshData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomepageActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                        dataAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
