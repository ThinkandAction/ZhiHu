package com.example.wujie.zhihu.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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

import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.home.HomeFragment;
import com.example.wujie.zhihu.login.LoginActivity;
import com.example.wujie.zhihu.set.SettingsActivity;
import com.example.wujie.zhihu.theme.ThemeFragment;
import com.example.wujie.zhihu.util.ActivityUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by wujie on 2016/4/10.
 */
public class MainActivity extends AppCompatActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private MainContract.Presenter mPresenter;

    private Fragment showingFragment;
    private boolean isFocused = false;

    private int menu_Type = 0;
    private Menu myMenu;
    private HomeFragment homeFragment;
    private ThemeFragment[] themeFragment = new ThemeFragment[14];
    private Toolbar toolbar;

    int[] themeId = {12, 4, 5, 11, 10, 6, 2, 7, 9, 3, 13, 8};
    String[] title = {"用户推荐日报", "设计日报", "大公司日报", "不许无聊", "互联网安全",
            "财经日报", "开始游戏", "音乐日报", "动漫日报", "电影日报", "日常心理学", "体育日报"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MainPresenter mainPresenter = new MainPresenter(this);

        homeFragment = new HomeFragment();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), homeFragment, R.id.frameLayout);
        setFragment(homeFragment);
        //mMainPresenter = new MainPresenter(, homeFragment);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        if (menu_Type == 0 & menu.size() < 3){
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_main, menu);
            Log.d("TAG", menu.size() + "");
        } else if (menu_Type == 1 & menu.size() == 3){
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_theme, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.login:
                mPresenter.openLoginActivity();
                break;
            case R.id.action_settings:
                mPresenter.openMenuSet();
                break;
            case R.id.pattern:
                break;
            case R.id.action_focus:
                if (!isFocused){
                    item.setIcon(R.drawable.ic_remove_circle_outline_white_24dp);
                    isFocused = true;
                } else {
                    item.setIcon(R.drawable.ic_add_circle_outline_white_24dp);
                    isFocused = false;
                }

        }
        return super.onOptionsItemSelected(item);
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(showingFragment);
        int order = menuItem.getOrder();
        int fragmentId;
        if (order == 1){
            if (homeFragment == null) {
                // 如果homeFragment为空，则创建一个并添加到界面上
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.frameLayout, homeFragment);
            } else {
                // 如果homeFragment不为空，则直接将它显示出来
                fragmentTransaction.show(homeFragment);
            }
            setFragment(homeFragment);
            toolbar.setTitle("首页");
            menu_Type = 0;
        } else {
            fragmentId = themeId[order - 2];
            if (themeFragment[fragmentId] == null) {
                // 如果homeFragment为空，则创建一个并添加到界面上
                themeFragment[fragmentId] = ThemeFragment.getInstance(fragmentId);
                fragmentTransaction.add(R.id.frameLayout, themeFragment[fragmentId]);
            } else {
                // 如果homeFragment不为空，则直接将它显示出来
                fragmentTransaction.show(themeFragment[fragmentId]);
            }
            setFragment(themeFragment[fragmentId]);
            toolbar.setTitle(title[order - 2]);
            menu_Type = 1;
        }
        fragmentTransaction.commit();
        onCreateOptionsMenu(myMenu);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment){
        showingFragment = fragment;
    }

    @Override
    public void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMenuSet() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
