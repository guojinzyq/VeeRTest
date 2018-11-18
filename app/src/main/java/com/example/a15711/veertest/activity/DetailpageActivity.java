package com.example.a15711.veertest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a15711.veertest.R;

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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(page_url);
        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setAllowFileAccess(true);
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
}
