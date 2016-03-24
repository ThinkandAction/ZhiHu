package com.example.wujie.zhihu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by wujie on 2016/3/13.
 */
public class LaunchActivity extends Activity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        Handler handler = new Handler();
        handler.postDelayed(new StartMainContent(), 500);

    }
    class StartMainContent implements Runnable{

        public StartMainContent() {
        }
        @Override
        public void run() {
            startActivity(new Intent(LaunchActivity.this,MainActivity.class));
            LaunchActivity.this.finish();
        }
    }
}
