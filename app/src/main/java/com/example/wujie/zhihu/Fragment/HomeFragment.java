package com.example.wujie.zhihu.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Activity.ItemActivity;
import com.example.wujie.zhihu.Adapter.FragmentListAdapter;
import com.example.wujie.zhihu.Adapter.MainListAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.JsonLatestNews;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.View.MySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wujie on 2016/3/14.
 */
public class HomeFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "url";
    private static final String STORY = "http://news-at.zhihu.com/api/4/story/";

    private Context context;
    private ListView mainListView;
    private View view;
    private String[] top_Stories_Title;
    private String[] top_Stories_Url;
    private JsonLatestNews mResponse;
    private String mUrl;
    private MySwipeRefreshLayout mMySwipeRefreshLayout;
    private List<HashMap<String, Object>> list;
    private LinkedList<JsonLatestNews.Stories> mStories;
    private FragmentListAdapter fragmentListAdapter;

    public static final HomeFragment newInstance(String mUrl){
        HomeFragment f = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, mUrl);
        f.setArguments(bundle);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUrl = getArguments().getString(EXTRA_MESSAGE);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mainListView = (ListView)view.findViewById(R.id.main_list);
        mMySwipeRefreshLayout = (MySwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        mStories = new LinkedList<JsonLatestNews.Stories>();
        context = getActivity();
        list = new ArrayList<HashMap<String, Object>>();
        mMySwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mMySwipeRefreshLayout.setRefreshing(true);
            }
        });

        //mainListView.addFooterView();   //上拉刷新的footerview可能无法显示，手动设置
        updateLatestNews(mUrl);

        fragmentListAdapter = new FragmentListAdapter(context, list, new int[]{R.layout.view_pager,
                R.layout.item_main_list, R.layout.title_image});

        mMySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLatestNews(mUrl);
            }
        });

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Log.d("position", position + "");
                intent.setClass(context, ItemActivity.class);
                intent.putExtra("url", STORY + mStories.get(position - 1).getId());//////!!!!!超出范围，不能点击
                startActivity(intent);
            }
        };

        mMySwipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                Log.d("onLoad", "--------------");
                updateLatestNews("http://news.at.zhihu.com/api/4/news/before/" + mResponse.getDate());//!!!mResponse的变化
            }
        });

        //position 从0开始
        mainListView.setOnItemClickListener(listener);
        return view;
    }

    public void updateLatestNews(String url){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest<JsonLatestNews>(url,
                JsonLatestNews.class,
                new Response.Listener<JsonLatestNews>() {
                    @Override
                    public void onResponse(JsonLatestNews response) {
                        mResponse = response;
                        LinkedList<JsonLatestNews.Stories> listStories = response.getStories(); //判断url不同再执行
                        mStories.addAll(listStories);

                        if (response.getTop_stories()!=null){
                            top_Stories_Title = new String[response.getTop_stories().size()];
                            top_Stories_Url = new String[response.getTop_stories().size()];
                            Iterator iterator_TopStories = response.getTop_stories().iterator();
                            for (int i = 0;iterator_TopStories.hasNext();i++){
                                JsonLatestNews.Top_stories m = (JsonLatestNews.Top_stories) iterator_TopStories.next();
                                top_Stories_Url[i] = m.getImage();
                                top_Stories_Title[i] = m.getTitle();
                            }

                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("Top_Stories_Title", top_Stories_Title);
                            map.put("Top_Stories_Url", top_Stories_Url);
                            if (list.size() == 0){
                                list.add(map);
                            } else {
                                list.remove(0);
                                list.add(0, map);
                            }
                        }

                        Iterator iterator = response.getStories().iterator();
                        for (int i = 0; iterator.hasNext(); i++){
                            JsonLatestNews.Stories m = (JsonLatestNews.Stories) iterator.next();
                            HashMap<String, Object> map1 = new HashMap<String, Object>();
                            map1.put("Stories_Title", m.getTitle());
                            map1.put("Stories_Url", m.getImages().iterator().next());
                            list.add(map1);
                        }

                        if (response.getTop_stories()!=null){
                            mainListView.setAdapter(fragmentListAdapter);
                            Log.d("setAdapter", "--------------");
                        } else {
                            fragmentListAdapter.notifyDataSetChanged();
                        }


                        if (response.getTop_stories() == null){
                            mMySwipeRefreshLayout.setLoading(false);//---可能会出错
                        } else {
                            mMySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Log.e("error", "error!!!!!!!");
                mMySwipeRefreshLayout.setRefreshing(false);
                mMySwipeRefreshLayout.setLoading(false);
            }
        });
        mQueue.add(gsonRequest);
    }

}
