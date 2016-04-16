package com.example.wujie.zhihu.theme;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.JsonLatestNews;
import com.example.wujie.zhihu.Info.NoBoringInfo;
import com.example.wujie.zhihu.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wujie on 2016/4/13.
 */
public class ThemePresenter implements ThemeContract.Presenter {

    private final ThemeContract.View mThemeView;

    private int themeId;

    private RequestQueue mQueue;

    public ThemePresenter(ThemeContract.View view){
        mThemeView = view;

        mThemeView.setPresenter(this);
    }

    private void getRequestQueue(){
        mQueue = Volley.newRequestQueue(mThemeView.getViewContent());
    }

    public void loadNewsFromInternet(String url, Response.ErrorListener errorListener) {
        if (mQueue == null){
            getRequestQueue();
        }
        GsonRequest gsonRequest = new GsonRequest<NoBoringInfo>(url, NoBoringInfo.class,
                mResponseListener, errorListener);
        mQueue.add(gsonRequest);
    }

    Response.Listener<NoBoringInfo> mResponseListener = new Response.Listener<NoBoringInfo>() {
        @Override
        public void onResponse(NoBoringInfo response) {
            ArrayList<HashMap<String, Object>> transList = dealWithResponse(response);
            mThemeView.showNews(transList);
            mThemeView.setVisiableNewsId(getLastStoryId(response));
            if (response.getBackground() != null){
                mThemeView.setRefreshIndicator(false);
                Log.d("TAG", "-----------setfalse");
            } else {
                mThemeView.setLoad(false);
            }
        }
    };

    private int getLastStoryId(NoBoringInfo response){
        List ms = response.getStories();
        int id = ((NoBoringInfo.StoriesBean) ms.get(ms.size() - 1)).getId();
        return id;
    }

    private ArrayList<HashMap<String, Object>> dealWithResponse(NoBoringInfo response){
        ArrayList<HashMap<String, Object>> deList = new ArrayList<HashMap<String, Object>>();
        if (response.getBackground() != null){
            String background = response.getBackground();
            String description = response.getDescription();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Description", description);
            map.put("Background", background);
            deList.add(map);
        }

        //有问题,不断刷新,不断增加,如何判断是否刷新内容
        Iterator iterator = response.getStories().iterator();
        for (int i = 0; iterator.hasNext(); i++){
            NoBoringInfo.StoriesBean m = (NoBoringInfo.StoriesBean) iterator.next();
            HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put("Stories_Title", m.getTitle());
            if (m.getImages() != null){
                map1.put("Stories_Url", m.getImages().iterator().next());
            } else {
                map1.put("Stories_Url",  "");
            }
            map1.put("Stories_Id", m.getId());//注意！！！
            deList.add(map1);
        }
        return deList;
    }

    @Override
    public void refreshNews() {
        loadNewsFromInternet(Constants.Url.THEME_NEWS + themeId, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mThemeView.showAlertNoInternet();
                mThemeView.setRefreshIndicator(false);
                mThemeView.showNoNews();
            }
        });
    }

    @Override
    public void loadNews(int id) {
        loadNewsFromInternet(Constants.Url.THEME_NEWS +themeId + "/before/" + id, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mThemeView.showAlertNoInternet();
                mThemeView.setRefreshIndicator(false);
                mThemeView.showNoNews();
            }
        });
    }

    @Override
    public void setThemeId(int id) {
        themeId = id;
    }

    @Override
    public void start() {
        mThemeView.setRefreshIndicator(true);
        refreshNews();
    }
}
