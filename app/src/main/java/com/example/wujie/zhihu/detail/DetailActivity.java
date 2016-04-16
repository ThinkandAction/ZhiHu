package com.example.wujie.zhihu.detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.login.LoginActivity;
import com.example.wujie.zhihu.support.Constants;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {

    private WebView webView;
    private int mStoryId;

    private ImageView background;
    private TextView commentText;
    private TextView supportText;

    private DetailContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DetailPresenter detailPresenter = new DetailPresenter(this);

        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);//启用transition的API（被调用的Activity中设置）
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        background = (ImageView) findViewById(R.id.imageView);
        ImageButton share = (ImageButton)findViewById(R.id.button_share);
        ImageButton star = (ImageButton)findViewById(R.id.button_star);
        ImageButton comment = (ImageButton)findViewById(R.id.button_comment);
        ImageButton support = (ImageButton)findViewById(R.id.button_support);
        commentText = (TextView)findViewById(R.id.comment_text);
        supportText = (TextView)findViewById(R.id.support_text);

        Intent intent = getIntent();
        mStoryId = intent.getIntExtra("StoryId", 0);

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
        mPresenter.loadDetail(mStoryId);
        mPresenter.loadExtra(mStoryId);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    public Context getViewContent() {
        return getApplicationContext();
    }

    @Override
    public ImageView getBackgroundView() {
        return background;
    }

    @Override
    public void setCommentCount(int count) {
        commentText.setText(String.valueOf(count));
    }

    @Override
    public void setSupportCount(int count) {
        supportText.setText(String.valueOf(count));
    }

    @Override
    public void showCommentActivity() {

    }
//异步加载
    @Override
    public void showDetail(String body) {
        //webView.loadData(mResponse.getBody(), "text/html; charset=UTF-8", null);//该方法不能放在oncreat中，因为volley是异步方法，调用此方法时数据可能还没加载完.
        // 上面的方法不能加载图片
        webView.loadDataWithBaseURL(Constants.Url.STORY_DETAIL + mStoryId, body, "text/html", "utf-8", null);
    }

    @Override
    public void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void showShareDialog() {

    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
