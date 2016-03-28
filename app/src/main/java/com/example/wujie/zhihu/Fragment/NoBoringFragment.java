package com.example.wujie.zhihu.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Activity.NoBoringItemActivity;
import com.example.wujie.zhihu.Adapter.FragmentListAdapter;
import com.example.wujie.zhihu.Adapter.MainListAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.NoBoringInfo;
import com.example.wujie.zhihu.JsonLatestNews;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.View.MySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wujie on 2016/3/21.
 */
public class NoBoringFragment extends Fragment {
    private static final String EXTRA_MESSAGE = "url";
    private static final String STORY = "http://news-at.zhihu.com/api/4/story/";

    private Context context;
    private ListView mainListView;
    private View view;
    private NoBoringInfo mResponse;
    private String mUrl;
    private MySwipeRefreshLayout mMySwipeRefreshLayout;
    private List<HashMap<String, Object>> list;
    private List<NoBoringInfo.StoriesBean> mStories;
    private FragmentListAdapter fragmentListAdapter;

    public static final NoBoringFragment newInstance(String mUrl){
        NoBoringFragment f = new NoBoringFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, mUrl);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUrl = getArguments().getString(EXTRA_MESSAGE);
        view = inflater.inflate(R.layout.fragment_no_boring, container, false);
        mainListView = (ListView)view.findViewById(R.id.main_list);
        mMySwipeRefreshLayout = (MySwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        context = getActivity();
        mStories = new ArrayList<NoBoringInfo.StoriesBean>();
        list = new ArrayList<HashMap<String, Object>>();
        updateLatestNews(mUrl);

        fragmentListAdapter = new FragmentListAdapter(context, list, new int[]{R.layout.view_pager,
                R.layout.item_main_list, R.layout.title_image});

        mMySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLatestNews(mUrl);
            }
        });

        mMySwipeRefreshLayout.setOnLoadListener(new MySwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                Log.d("onLoad1", "--------------");
                List ms = mResponse.getStories();
                int id = ((NoBoringInfo.StoriesBean) ms.get(ms.size() - 1)).getId();
                updateLatestNews("http://news-at.zhihu.com/api/4/theme/11/before/" + id);  //!!!mResponse的变化

            }
        });


        //position 从0开始
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    return;
                } else {
                    Intent intent = new Intent();
                    Log.d("position", position+"");
                    intent.setClass(context, NoBoringItemActivity.class);
                    intent.putExtra("url", STORY + mStories.get(position - 1).getId());
                    startActivity(intent);
                }
            }
        });

        return view;
    }



    public void updateLatestNews(final String url){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest<NoBoringInfo>(url,
                NoBoringInfo.class,
                new Response.Listener<NoBoringInfo>() {
                    @Override
                    public void onResponse(NoBoringInfo response) {
                        mResponse = response;
                        List<NoBoringInfo.StoriesBean> listStories = response.getStories(); ////判断url不同再执行
                        mStories.addAll(listStories);

                        if (response.getBackground() != null){
                            String background = response.getBackground();
                            String description = response.getDescription();
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("Description", description);
                            map.put("Background", background);
                            if (list.size() == 0){
                                list.add(map);
                            } else {
                                list.remove(0);
                                list.add(0, map);
                            }
                        }

                        Iterator iterator = response.getStories().iterator();
                        for (int i = 0; iterator.hasNext(); i++){
                            NoBoringInfo.StoriesBean m = (NoBoringInfo.StoriesBean) iterator.next();
                            HashMap<String, Object> map1 = new HashMap<String, Object>();
                            map1.put("Stories_Title", m.getTitle());
                            String urlImage;
                            if (m.getImages() != null){
                                urlImage = m.getImages().iterator().next();
                            } else {
                                Log.d("setImage", "-----------------");
                                urlImage = "";
                            }
                            map1.put("Stories_Url", urlImage);
                            list.add(map1);
                        }

                        if (response.getBackground() != null){
                            mainListView.setAdapter(fragmentListAdapter);
                            Log.d("setAdapter", "-----------");
                        } else {
                            fragmentListAdapter.notifyDataSetChanged();
                            Log.d("setnotify", "-----------");
                        }

                        if (response.getBackground() != null){
                            mMySwipeRefreshLayout.setRefreshing(false);
                            Log.d("setRefresh", "-----------");
                        } else {
                            mMySwipeRefreshLayout.setLoading(false);
                            Log.d("setLoading", "-----------");
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
