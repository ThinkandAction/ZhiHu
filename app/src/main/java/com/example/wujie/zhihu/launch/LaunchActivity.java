package com.example.wujie.zhihu.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.main.MainActivity;

/**
 * Created by wujie on 2016/3/13.
 */
public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        Handler handler = new Handler();
        handler.postDelayed(new StartMainContent(), 2500);

    }
    class StartMainContent implements Runnable{

        public StartMainContent() {
        }
        @Override
        public void run() {
            startActivity(new Intent(LaunchActivity.this, MainActivity.class));
            LaunchActivity.this.finish();
        }
    }
}
