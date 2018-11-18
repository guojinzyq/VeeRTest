package com.example.a15711.veertest.data;

import org.litepal.crud.DataSupport;

/**
 * Created by 15711 on 2018/11/16.
 */

public class Data extends DataSupport{

    private int id;//数据ID
    private String thumbUrl;//缩略图Url
    private String title;//数据内容
    private String category;//数据分类
    private String pageUrl;//详情页Url

    public Data(){};

    public Data(int id, String category, String title, String thumbUrl,String pageUrl) {
        this.category = category;
        this.title = title;
        this.id = id;
        this.thumbUrl = thumbUrl;
        this.pageUrl=pageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
}
