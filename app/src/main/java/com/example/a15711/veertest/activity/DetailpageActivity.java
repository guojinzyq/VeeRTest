package com.example.a15711.veertest.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a15711.veertest.R;

import static android.view.View.LAYER_TYPE_NONE;

public class DetailpageActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpage);
        String page_url=getIntent().getStringExtra("page_url");
        String mTitle=getIntent().getStringExtra("title");
        webView=findViewById(R.id.webview);
        title=findViewById(R.id.title);
        progressBar=findViewById(R.id.progressbar);
        title.setText(mTitle);
        //******
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webView.setLayerType(LAYER_TYPE_NONE, null);
        }
        //管理WebView的状态配置
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setSavePassword(false);//关闭密码保存提醒功能
        //解决 File域同源策略绕过漏洞
        webSettings.setAllowFileAccess(false);
        //当Android系统为4.1及以上，API版本16以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //设置WebView运行中的一个文件方案被不允许访问其他文件方案中的内容
            webSettings.setAllowFileAccessFromFileURLs(false);
            //设置WebView运行中的脚本可以是否访问任何原始起点内容
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }
        //当Android系统为5.0及以上，API版本21以上
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            //设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.removeJavascriptInterface("searchBoxjavaBridge_");//解决 CVE-2014-1939 漏洞
            webView.removeJavascriptInterface("accessibility");//解决  CVE-2014-7224  漏洞
            webView.removeJavascriptInterface("accessibilityTraversal");//解决  CVE-2014-7224  漏洞
        }
        //*********
        webView.loadUrl(page_url);
        //setWebChromeClient辅助WebView处理JavaScript的对话框，网站图标，网站title，加载进度
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    // 加载中
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }

            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
