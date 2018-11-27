package com.example.a15711.veertest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.a15711.veertest.R;
import com.example.a15711.veertest.adapter.DataAdapter;
import com.example.a15711.veertest.data.Data;
import com.example.a15711.veertest.util.HttpUtil;
import com.example.a15711.veertest.util.RetrofitHttpUtil;
import com.example.a15711.veertest.util.UpLoad;
import com.example.a15711.veertest.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    //记录刷新次数，以便显示各页的内容
    int refresh=0;
    //每次上拉刷新加载的数据条数
    int loadCount=0;
    //刷新状态，本次刷新未结束时不响应新的刷新
    boolean refreshStatus=false;
    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    //下拉刷新进度条
    ProgressBar progressBar;
    DataAdapter dataAdapter;
    //接收上拉刷新消息并处理
    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{ progressBar.setVisibility(View.VISIBLE);refreshStatus=true;break;}
                case 2:{
                    boolean res=msg.getData().getBoolean("result");
                    if(res){
                        dataAdapter.notifyItemRangeInserted(datas.size()-1-loadCount,loadCount);
                        recyclerView.scrollToPosition(datas.size()-1-loadCount);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(HomepageActivity.this,"加载下一页成功",Toast.LENGTH_SHORT).show();
                    }else if(!res){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(HomepageActivity.this,"没有更多数据了",Toast.LENGTH_SHORT).show();
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
        //初始化各个控件
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout=findViewById(R.id.down_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        progressBar=findViewById(R.id.progressbar);
        recyclerView=findViewById(R.id.veer_recycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataAdapter=new DataAdapter(datas);
        recyclerView.setAdapter(dataAdapter);
        //判断本地是否有从API中读来的json串的缓存，有的话直接用，没有的话重新从API中读取
        String jsonStr=pref.getString("jsonString",null);
        if(jsonStr!=null){
            dataList.addAll(Utility.handleJson(jsonStr));
            setRefreshData() ;
            dataAdapter.notifyItemRangeInserted(0,loadCount);
            recyclerView.scrollToPosition(0);
        }else{
            refreshData(1);
        }
//        if(!setRefreshData()){
//            Toast.makeText(this,"没有更多数据了",Toast.LENGTH_SHORT).show();
//        }
//        if(dataList.size()>0){
//            Log.d("HomepageActivity","dataList不为空");
//        }
//        if(datas.size()>0){
//            Log.d("HomepageActivity","datas不为空");
//        }
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
                refreshData(2);
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
        loadCount=0;
        if(refresh*6>=dataList.size()){
            return false;
        }
        //  取6条数据即可
        int count=refresh*6+6;
        for(int i=refresh*6;i<dataList.size()&&i<count;i++){
            datas.add(dataList.get(i));
            loadCount++;
        }
        refresh++;
        return true;
    }

    //刷新数据，从API中获取数据并显示，参数为1表示启动时程序自动获取；为2表示通过用户下拉刷新获取
    public void refreshData(int i){
        final int signal=i;
        RetrofitHttpUtil.requestFormUrl(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseText="";
                try{
                    responseText=response.body().string();
                }catch (Exception e){
                    e.printStackTrace();
                }
                editor=pref.edit();
                editor.putString("jsonString",responseText);
                editor.apply();
                dataList.clear();
                datas.clear();
                dataList.addAll(Utility.handleJson(responseText));
                refresh=0;
                setRefreshData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(signal==1){
                            dataAdapter.notifyItemRangeInserted(0,loadCount);
                        }
                        else if(signal==2){
                            dataAdapter.notifyDataSetChanged();
                            Toast.makeText(HomepageActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        RetrofitHttpUtil.requestFormUrl(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(retrofit2.Call call, Throwable t) {

            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //singal==2表示是由下拉刷新触发
                        if(signal==2) {
                            Toast.makeText(HomepageActivity.this, "刷新失败！", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        //signal==1表示是由应用第一次启动触发
                        else if(signal==1){
                            Toast.makeText(HomepageActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//        HttpUtil.sendOkHttpRequest(API, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //singal==2表示是由下拉刷新触发
//                        if(signal==2) {
//                            Toast.makeText(HomepageActivity.this, "刷新失败！", Toast.LENGTH_SHORT).show();
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
//                        //signal==1表示是由应用第一次启动触发
//                        else if(signal==1){
//                            Toast.makeText(HomepageActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseText = response.body().string();
//                editor=pref.edit();
//                editor.putString("jsonString",responseText);
//                editor.apply();
//                dataList.clear();
//                datas.clear();
//                dataList.addAll(Utility.handleJson(responseText));
//                refresh=0;
//                setRefreshData();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(signal==1){
//                            dataAdapter.notifyItemRangeInserted(0,loadCount);
//                        }
//                        else if(signal==2){
//                            dataAdapter.notifyDataSetChanged();
//                            Toast.makeText(HomepageActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
//                    }
//                });
//            }
//        });
    }
}
