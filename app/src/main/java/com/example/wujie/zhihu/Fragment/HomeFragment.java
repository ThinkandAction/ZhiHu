package com.example.wujie.zhihu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Activity.ItemActivity;
import com.example.wujie.zhihu.Adapter.RecyclerViewAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.JsonLatestNews;
import com.example.wujie.zhihu.Interface.OnRecyclerItemClickListener;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.ZhiHuDailyApplication;
import com.example.wujie.zhihu.db.DBHelper;
import com.example.wujie.zhihu.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by wujie on 2016/3/21.
 */
public class HomeFragment extends Fragment implements Response.Listener<JsonLatestNews>, Response.ErrorListener{
    private static final String EXTRA_MESSAGE = "url";

    private Context context;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;
    private String mUrl;
    private ArrayList<HashMap<String, Object>> itemList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int visibleNewsDate;
    private boolean isLoad = false;

    public static HomeFragment newInstance(String mUrl){
        HomeFragment f = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, mUrl);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        new RecoverNewsListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//并发处理任务
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        mUrl = getArguments().getString(EXTRA_MESSAGE);
        view = inflater.inflate(R.layout.content_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryLight);
        itemList = new ArrayList<HashMap<String, Object>>();
        /*mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });*/
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        mRecyclerViewAdapter = new RecyclerViewAdapter(context, list, new int[]{R.layout.view_pager,
                R.layout.item_main_list, R.layout.background}, new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(context, ItemActivity.class);
                int id = 0;
                if (itemList.get(position).get("Stories_Id") instanceof Double){
                    double m = (double)itemList.get(position).get("Stories_Id");////为什么取出的数据变成了double
                    id = (int)Math.floor(m);
                } else {
                    id = (int)itemList.get(position).get("Stories_Id");
                }
                intent.putExtra("url", Constants.Url.STORY_DETAIL + id);//////!!!!!超出范围，不能点击
                startActivity(intent);
                //getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //控件已经设定在加载时无法再加载了
                loadNews(mUrl);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mRecyclerViewAdapter.getItemCount()
                        && !isLoad) {
                    Log.d("TAG", "-------load");
                    setLoad(true);
                    ArrayList<HashMap<String, Object>> list = ZhiHuDailyApplication.getDataBase()
                            .newsOfTheDay(visibleNewsDate - 1, DBHelper.TABLE_NAME);
                    if (list != null){
                        updateList(list);
                        mRecyclerViewAdapter.updateNewsList(list);
                        visibleNewsDate = visibleNewsDate - 1;
                        setLoad(false);
                    } else {
                        loadNews(Constants.Url.STORY_BEFORE + visibleNewsDate);
                    }
                }
            }
        });
        return view;
    }

    private void setLoad(boolean load){
        isLoad = load;
    }

    public void loadNews(String url) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest<JsonLatestNews>(url, JsonLatestNews.class, this, this);
        mQueue.add(gsonRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("error", "error!!!!!!!");
        mSwipeRefreshLayout.setRefreshing(false);
        setLoad(false);
    }

    @Override
    public void onResponse(JsonLatestNews response) {

        ArrayList<HashMap<String, Object>> transList = dealWithResponse(response);
        updateList(transList);
        mRecyclerViewAdapter.updateNewsList(transList);
        visibleNewsDate = Integer.parseInt(response.getDate());
        if (response.getTop_stories() != null){
            mSwipeRefreshLayout.setRefreshing(false);
        } else if (isLoad) {
            setLoad(false);
        }

        ZhiHuDailyApplication.getDataBase().insertOrUpdateNewsList(visibleNewsDate, DBHelper.TABLE_NAME, transList);
    }

    private ArrayList<HashMap<String, Object>> dealWithResponse(JsonLatestNews response){
        ArrayList<HashMap<String, Object>> deList = new ArrayList<HashMap<String, Object>>();
        if (response.getTop_stories()!=null){
            String[] top_Stories_Title = new String[response.getTop_stories().size()];
            String[] top_Stories_Url = new String[response.getTop_stories().size()];
            Iterator iterator_TopStories = response.getTop_stories().iterator();
            for (int i = 0;iterator_TopStories.hasNext();i++){
                JsonLatestNews.Top_stories m = (JsonLatestNews.Top_stories) iterator_TopStories.next();
                top_Stories_Url[i] = m.getImage();
                top_Stories_Title[i] = m.getTitle();
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Top_Stories_Title", top_Stories_Title);
            map.put("Top_Stories_Url", top_Stories_Url);
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

    public void updateList(ArrayList<HashMap<String, Object>> list){
        if (list.get(0).containsKey("Top_Stories_Title")){
            itemList = list;
        } else {
            itemList.addAll(list);
        }
    }

    private class RecoverNewsListTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Void... params) {
            int date = ZhiHuDailyApplication.getDataBase().tableLastNewsId(DBHelper.TABLE_NAME);
            visibleNewsDate = date;
            ArrayList<HashMap<String, Object>> list = ZhiHuDailyApplication.getDataBase()
                    .newsOfTheDay(visibleNewsDate, DBHelper.TABLE_NAME);
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> newsListRecovered) {
            if (newsListRecovered != null) {
                mRecyclerViewAdapter.updateNewsList(newsListRecovered);
                updateList(newsListRecovered);
            }
        }
    }

}
