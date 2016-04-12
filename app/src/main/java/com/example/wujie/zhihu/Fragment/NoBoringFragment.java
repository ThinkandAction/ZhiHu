package com.example.wujie.zhihu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.Activity.ItemActivity;
import com.example.wujie.zhihu.Adapter.FragmentListAdapter;
import com.example.wujie.zhihu.Adapter.RecyclerViewAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.JsonLatestNews;
import com.example.wujie.zhihu.Info.NoBoringInfo;
import com.example.wujie.zhihu.Interface.OnRecyclerItemClickListener;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.ZhiHuDailyApplication;
import com.example.wujie.zhihu.cache.DiskLruCache;
import com.example.wujie.zhihu.db.DBHelper;
import com.example.wujie.zhihu.db.NoBoringDataBase;
import com.example.wujie.zhihu.support.Constants;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wujie on 2016/3/21.
 */
public class NoBoringFragment extends Fragment implements Response.Listener<NoBoringInfo>, Response.ErrorListener{
    private static final String EXTRA_MESSAGE = "url";

    private Context context;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View view;
    private String mUrl;
    private ArrayList<HashMap<String, Object>> itemList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int visibleNewsId;
    private boolean isLoad = false;

    public static NoBoringFragment newInstance(String mUrl){
        NoBoringFragment f = new NoBoringFragment();
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
        mUrl = getArguments().getString(EXTRA_MESSAGE);
        view = inflater.inflate(R.layout.content_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryLight);
        context = getActivity();
        itemList = new ArrayList<HashMap<String, Object>>();
        /*mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });*/
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        mRecyclerViewAdapter = new RecyclerViewAdapter(context, list);
        //updateLatestNews(mUrl);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //控件已经设定在加载时无法再加载了
                Log.d("TAG", "------------set-----------");
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
                    //updateLatestNews("http://news-at.zhihu.com/api/4/theme/11/before/" + id);  //!!!mResponse的变化
                   int m = ZhiHuDailyApplication.getDataBase().idBeforeId(visibleNewsId, DBHelper.TABLE_NAME_1);
                    if (m != 0){
                        Log.d("TAG", "-------load---m---");
                        ArrayList<HashMap<String, Object>> list = ZhiHuDailyApplication.getDataBase()
                            .newsOfTheDay(m, DBHelper.TABLE_NAME_1);

                        mRecyclerViewAdapter.updateNewsList(list);
                        updateList(list);
                        visibleNewsId = m;
                        setLoad(false);
                    } else {
                        loadNews(Constants.Url.THEME_BEFORE + visibleNewsId);
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
        GsonRequest gsonRequest = new GsonRequest<NoBoringInfo>(url, NoBoringInfo.class, this, this);
        mQueue.add(gsonRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("error", "error!!!!!!!");
        mSwipeRefreshLayout.setRefreshing(false);
        setLoad(false);
    }

    @Override
    public void onResponse(NoBoringInfo response) {

        ArrayList<HashMap<String, Object>> transList = dealWithResponse(response);
        updateList(transList);
        mRecyclerViewAdapter.updateNewsList(transList);
        int id = getLastStoryId(response);
        visibleNewsId = id;
        if (response.getBackground() != null){
            mSwipeRefreshLayout.setRefreshing(false);
        } else if (isLoad) {
            setLoad(false);
        }
        ZhiHuDailyApplication.getDataBase().insertOrUpdateNewsList(id, DBHelper.TABLE_NAME_1,
                transList);
    }

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
/*
    private void setNewsList(ArrayList<HashMap<String, Object>> list) {
        mList = list;
    }

    private void addNewsList(ArrayList<HashMap<String, Object>> list) {
        mList.addAll(list);
    }

    public void updateNewsList(ArrayList<HashMap<String, Object>> list) {
        if (list.get(0).containsKey("Background")){
            setNewsList(list);
            Log.d("TAG", "-----------setlist.size----"+list.size());
        } else {
            addNewsList(list);
            Log.d("TAG", "-----------addlist.size----" + mList.size());
        }
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }
*/

    public void updateList(ArrayList<HashMap<String, Object>> list){
        if (list.get(0).containsKey("Background") || list.get(0).containsKey("Top_Stories_Title")){
            itemList = list;
        } else {
            itemList.addAll(list);
        }
    }

    //获取缓存地址
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
//获取应用程序的版本号
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void mdisklurche(){
        DiskLruCache mDiskLruCache = null;
        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RecoverNewsListTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Void... params) {
            int id = ZhiHuDailyApplication.getDataBase().tableLastNewsId(DBHelper.TABLE_NAME_1);
            visibleNewsId = id;
            Log.d("TAG", "-------load--doin----"+id);
            ArrayList<HashMap<String, Object>> list = ZhiHuDailyApplication.getDataBase().newsOfTheDay(id, DBHelper.TABLE_NAME_1);
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
