package com.example.wujie.zhihu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Fragment.HomeFragment;
import com.example.wujie.zhihu.Fragment.NoBoringFragment;

public class MainActivity extends AppCompatActivity {

    private static final String LATEST_NEWS = "http://news-at.zhihu.com/api/4/news/latest";
    private static final String NO_BORING = "http://news-at.zhihu.com/api/4/theme/11";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private HomeFragment homeFragment;
    private NoBoringFragment noBoringFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorwGrey);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = HomeFragment.newInstance(LATEST_NEWS);
        fragmentTransaction.replace(R.id.main_layout_group,
                homeFragment).commit();



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_id);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mNavigationView = (NavigationView)findViewById(R.id.navigation_drawer);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("menuItem:", menuItem.getTitle() + "");
                mDrawerLayout.closeDrawers();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.hide(homeFragment);
                switch ((String) menuItem.getTitle()) {
                    case "首页":
                        if (homeFragment == null) {
                        // 如果homeFragment为空，则创建一个并添加到界面上
                            homeFragment = HomeFragment.newInstance(LATEST_NEWS);
                        fragmentTransaction.replace(R.id.main_layout_group, homeFragment);
                    } else {
                        // 如果homeFragment不为空，则直接将它显示出来
                            fragmentTransaction.replace(R.id.main_layout_group, homeFragment);
                    }
                        break;

                    case "不许无聊":
                        if (noBoringFragment == null) {
                        // 如果homeFragment为空，则创建一个并添加到界面上
                        noBoringFragment = NoBoringFragment.newInstance(NO_BORING);
                        fragmentTransaction.replace(R.id.main_layout_group, noBoringFragment);
                    } else {
                        // 如果homeFragment不为空，则直接将它显示出来
                        fragmentTransaction.replace(R.id.main_layout_group, noBoringFragment);
                    }
                        break;
                }
                fragmentTransaction.commit();
                return false;
            }
        });
        RequestQueue mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(NO_BORING, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("TAG", s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        mQueue.add(stringRequest);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
