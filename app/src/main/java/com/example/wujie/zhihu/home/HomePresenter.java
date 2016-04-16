package com.example.wujie.zhihu.home;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.JsonLatestNews;
import com.example.wujie.zhihu.ZhiHuDailyApplication;
import com.example.wujie.zhihu.db.DBHelper;
import com.example.wujie.zhihu.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wujie on 2016/4/10.
 */
public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View mHomeView;

    private RequestQueue mQueue;


    public HomePresenter(HomeContract.View view) {
        mHomeView = view;

        mHomeView.setPresenter(this);
    }

    private void getRequestQueue(){
        mQueue = Volley.newRequestQueue(mHomeView.getViewContent());
    }

    public void loadNewsFromInternet(String url, Response.ErrorListener errorListener) {
        if (mQueue == null){
            getRequestQueue();
        }
        GsonRequest gsonRequest = new GsonRequest<JsonLatestNews>(url, JsonLatestNews.class,
                mResponseListener, errorListener);
        mQueue.add(gsonRequest);
    }

    Response.Listener<JsonLatestNews> mResponseListener = new Response.Listener<JsonLatestNews>() {
        @Override
        public void onResponse(JsonLatestNews response) {
            ArrayList<HashMap<String, Object>> transList = dealWithResponse(response);
            mHomeView.showNews(transList);
            mHomeView.setVisiableNewsDate(Integer.parseInt(response.getDate()));
            if (response.getTop_stories() != null){
                mHomeView.setRefreshIndicator(false);
            } else {
                mHomeView.setLoad(false);
            }
            ZhiHuDailyApplication.getDataBase().insertOrUpdateNewsList(Integer.parseInt(response.getDate()),
                    DBHelper.TABLE_NAME, transList);
        }
    };

    private ArrayList<HashMap<String, Object>> dealWithResponse(JsonLatestNews response){
        ArrayList<HashMap<String, Object>> deList = new ArrayList<HashMap<String, Object>>();
        if (response.getTop_stories()!=null){
            String[] top_Stories_Title = new String[response.getTop_stories().size()];
            String[] top_Stories_Url = new String[response.getTop_stories().size()];
            int[] top_Stories_Id = new int[response.getTop_stories().size()];
            Iterator iterator_TopStories = response.getTop_stories().iterator();
            for (int i = 0;iterator_TopStories.hasNext();i++){
                JsonLatestNews.Top_stories m = (JsonLatestNews.Top_stories) iterator_TopStories.next();
                top_Stories_Url[i] = m.getImage();
                top_Stories_Title[i] = m.getTitle();
                top_Stories_Id[i] = m.getId();
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Top_Stories_Title", top_Stories_Title);
            map.put("Top_Stories_Url", top_Stories_Url);
            map.put("Top_Stories_Id", top_Stories_Id);
            deList.add(map);
        }

        //有问题,不断刷新,不断增加,如何判断是否刷新内容
        Iterator iterator = response.getStories().iterator();
        for (int i = 0; iterator.hasNext(); i++){
            JsonLatestNews.Stories m = (JsonLatestNews.Stories) iterator.next();
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

    private void loadNewsFromDB(int date, DBErrorListener listener){
        ArrayList<HashMap<String, Object>> list = ZhiHuDailyApplication.getDataBase()
                .newsOfTheDay(date, DBHelper.TABLE_NAME);
        if (list != null){
            mHomeView.showNews(list);
            mHomeView.setVisiableNewsDate(date - 1);
            mHomeView.setRefreshIndicator(false);
            mHomeView.setLoad(false);
        } else {
            listener.errorResponse();
        }
    }

    @Override
    public void refreshNews() {
        loadNewsFromInternet(Constants.Url.LATEST_NEWS, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mHomeView.setRefreshIndicator(false);
                mHomeView.showAlertNoInternet();
                loadNewsFromDB(ZhiHuDailyApplication.getDataBase().tableLastNewsId(DBHelper.TABLE_NAME),
                        new DBErrorListener() {
                            @Override
                            public void errorResponse() {
                                mHomeView.showNoNews();
                            }
                        });
            }
        });
    }

    @Override
    public void loadNews(final int date) {
        loadNewsFromDB(date, new DBErrorListener() {
            @Override
            public void errorResponse() {
                loadNewsFromInternet(Constants.Url.STORY_BEFORE + date, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mHomeView.showAlertNoInternet();
                        mHomeView.setLoad(false);
                    }
                });
            }
        });
    }

    @Override
    public void start() {
        mHomeView.setRefreshIndicator(true);
        refreshNews();
    }

    interface DBErrorListener{
        void errorResponse();
    }
}
