package com.example.wujie.zhihu.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Info.ContentInfo;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.cache.LevelTwoCache;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class ItemActivity extends AppCompatActivity {

    private ListView listView;
    private WebView webView;
    private String mUrl;
    private ContentInfo mResponse;
    private ImageLoader mImageLoader;
    private static final int MAX_DISKSIZE = 10 * 1024 *1024;
    private static final int MAX_LRUCACHESIZE = 10 * 1024 *1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);//启用transition的API（被调用的Activity中设置）
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");

        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
       //webView.getSettings().setDefaultTextEncodingName("utf-8");
        WebSettings webSettings = webView.getSettings();
        //webSettings.setBlockNetworkImage(false);

        /*webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });*/
        loadItem();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//透明导航栏

            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void loadItem(){
        RequestQueue mQueue = Volley.newRequestQueue(this);
        GsonRequest gsonRequest = new GsonRequest<ContentInfo>(mUrl,
                ContentInfo.class,
                new Response.Listener<ContentInfo>() {
                    @Override
                    public void onResponse(ContentInfo response) {
                        mResponse = response;
                        //webView.loadData(mResponse.getBody(), "text/html; charset=UTF-8", null);//该方法不能放在oncreat中，因为volley是异步方法，调用此方法时数据可能还没加载完.
                        //上面的方法不能加载图片
                        String linkCss = "<link rel=\"stylesheet\" href=\""+response.getCss().iterator().next()+ "\"type=\"text/css\">";
                        String body = "<html><header>" + linkCss + "</header>" + mResponse.getBody() + "</body></html>";
                        webView.loadDataWithBaseURL(mUrl, body, "text/html", "utf-8", null);
                        RequestQueue mQueue = Volley.newRequestQueue(ItemActivity.this);
                        if (response.getImage() != null){
                            mImageLoader = new ImageLoader(mQueue, new LevelTwoCache(ItemActivity.this, "image", MAX_DISKSIZE, MAX_LRUCACHESIZE,
                                    Bitmap.CompressFormat.JPEG, 70));
                            ImageLoader.ImageListener listener = ImageLoader.getImageListener((ImageView) findViewById(R.id.imageView), 0, 0);
                            mImageLoader.get(response.getImage(), listener);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Log.e("error", "error!!!!!!!");

            }
        });
        mQueue.add(gsonRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
}
