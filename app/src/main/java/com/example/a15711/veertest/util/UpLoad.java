package com.example.a15711.veertest.util;

import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.net.InterfaceAddress;

/**
 * Created by 15711 on 2018/11/17.
 */

public  class UpLoad extends RecyclerView.OnScrollListener{

    //记录是否处于加载状态
    private boolean uploadStatus=false;
    public interface ScrollListener{
        public void loadMore();
    }
    private ScrollListener mScrollListener;

    public UpLoad(ScrollListener listener){
        mScrollListener=listener;
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //如果用户正在滑动View并且已经滑到底部则调用loadMore方法
        if(!recyclerView.canScrollVertically(1)&&dy>0){
            mScrollListener.loadMore();
        }
    }

}
