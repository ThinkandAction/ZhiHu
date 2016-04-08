package com.example.wujie.zhihu;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.wujie.zhihu.Fragment.HomeFragment;
import com.example.wujie.zhihu.Fragment.NoBoringFragment;
import com.example.wujie.zhihu.Fragment.NoBoringFragment_NoDB;
import com.example.wujie.zhihu.support.Constants;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int menu_Type = 0;
    private Menu myMenu;
    private HomeFragment homeFragment;
    private NoBoringFragment_NoDB noBoringFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);//启用transition的API（主动调用的activity设置）
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        toolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = HomeFragment.newInstance(Constants.Url.LATEST_NEWS);
        fragmentTransaction.replace(R.id.frameLayout, homeFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /*
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//透明导航栏
            */
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            toolbar.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            navigationView.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        myMenu = menu;
        if (menu_Type == 0 & menu.size() < 2){
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_main, menu);
            Log.d("TAG", menu.size() + "");
        } else if (menu_Type == 1 & menu.size() == 3){
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_item, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (homeFragment != null){
            fragmentTransaction.hide(homeFragment);
        }
        if (noBoringFragment != null){
            fragmentTransaction.hide(noBoringFragment);
        }
        switch ((String) menuItem.getTitle()) {
            case "首页":
                if (homeFragment == null) {
                    // 如果homeFragment为空，则创建一个并添加到界面上
                    homeFragment = HomeFragment.newInstance(Constants.Url.LATEST_NEWS);
                    fragmentTransaction.add(R.id.frameLayout, homeFragment);
                } else {
                    // 如果homeFragment不为空，则直接将它显示出来
                    fragmentTransaction.show(homeFragment);
                }
                toolbar.setTitle("首页");
                menu_Type = 0;
                onPrepareOptionsMenu(myMenu);
                break;

            case "不许无聊":
                if (noBoringFragment == null) {
                    // 如果homeFragment为空，则创建一个并添加到界面上
                    noBoringFragment = NoBoringFragment_NoDB.newInstance(Constants.Url.THEME_NO_BORING);
                    fragmentTransaction.add(R.id.frameLayout, noBoringFragment);
                } else {
                    // 如果homeFragment不为空，则直接将它显示出来
                    fragmentTransaction.show(noBoringFragment);
                }
                toolbar.setTitle("不许无聊");
                menu_Type = 1;
                onPrepareOptionsMenu(myMenu);
                break;
        }
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
