package com.example.wujie.zhihu.main;

import android.annotation.TargetApi;
import android.content.Intent;
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
import com.example.wujie.zhihu.Fragment.NoBoringFragment_NoDB;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.data.AppRepository;
import com.example.wujie.zhihu.login.LoginActivity;
import com.example.wujie.zhihu.set.SetActivity;
import com.example.wujie.zhihu.support.Constants;
import com.example.wujie.zhihu.util.ActivityUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by wujie on 2016/4/10.
 */
public class MainActivity extends AppCompatActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private MainContract.Presenter mPresenter;

    private int menu_Type = 0;
    private Menu myMenu;
    private HomeFragment homeFragment;
    private NoBoringFragment_NoDB noBoringFragment;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
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

        MainPresenter mainPresenter = new MainPresenter(AppRepository.getnstance(), this);

        homeFragment = HomeFragment.newInstance(Constants.Url.LATEST_NEWS);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), homeFragment, R.id.frameLayout);

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
            getMenuInflater().inflate(R.menu.menu_item, menu);
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
        if (homeFragment != null){
            fragmentTransaction.hide(homeFragment);
        }
        if (noBoringFragment != null){
            fragmentTransaction.hide(noBoringFragment);
        }
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_home:
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
                onCreateOptionsMenu(myMenu);
                break;

            case R.id.navigation_item_noboring:
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
                onCreateOptionsMenu(myMenu);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMenuSet() {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
