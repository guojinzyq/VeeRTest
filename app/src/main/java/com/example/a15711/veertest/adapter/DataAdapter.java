package com.example.a15711.veertest.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a15711.veertest.R;
import com.example.a15711.veertest.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 15711 on 2018/11/16.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{

    private Context mContext;
    //apapter中的item点击事件监听对象
    public OnClickListener onClickListener;
    private List<Data> dataList=new ArrayList<Data>();

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //定义一个点击事件回调接口
    public interface OnClickListener{
        void onClick(View itemView,int position);
    }
    public DataAdapter(List<Data> dataList){
        this.dataList=dataList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View dataView;
        ImageView thumbImageView;
        TextView titleTextView;
        TextView categoryTextView;
        ViewHolder(View view){
            super(view);
            dataView=view;
            thumbImageView=(ImageView)view.findViewById(R.id.thumb);
            titleTextView=(TextView)view.findViewById(R.id.title);
            categoryTextView=(TextView)view.findViewById(R.id.category);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.data_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(dataList.size()>0){
            Data data=(Data)dataList.get(position);
            Glide.with(mContext).load(data.getThumbUrl()).into(holder.thumbImageView);
            holder.titleTextView.setText(data.getTitle());
            holder.categoryTextView.setText(data.getCategory());
            holder.dataView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickListener!=null){
                        onClickListener.onClick(holder.dataView,holder.getLayoutPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size()>0?dataList.size():1;
    }
}
