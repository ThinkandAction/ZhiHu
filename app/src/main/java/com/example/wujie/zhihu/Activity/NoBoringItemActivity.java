package com.example.wujie.zhihu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.BitmapCache;
import com.example.wujie.zhihu.ContentInfo;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.R;

/**
 * Created by wujie on 2016/3/22.
 */
public class NoBoringItemActivity extends AppCompatActivity {

    private ListView listView;
    private WebView webView;
    private String mUrl;
    private ContentInfo mResponse;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
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



        /*listView = (ListView)findViewById(R.id.listView);
        String[] theme = new String[]{"首页", "日常心理学", "用户推荐日报", "电影日报",
                "不许无聊", "设计日报", "大公司日报", "财经日报", "互联网安全", "开始游戏", "音乐日报", "动漫日报", "体育日报",
        "dawd", "dwdawd", "dwda","dwad"};
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < theme.length; i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", theme[i]);
            map.put("image", R.drawable.icon_plus_touming);
            arrayList.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.item_menu_list, new String[]{"name", "image"},
                new int[]{R.id.menu_item, R.id.image_button});
        listView.setAdapter(simpleAdapter);
        */
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
                        if (response.getImage() !=null){
                            RequestQueue mQueue = Volley.newRequestQueue(NoBoringItemActivity.this);
                            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
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
