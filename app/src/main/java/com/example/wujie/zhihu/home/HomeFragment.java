package com.example.wujie.zhihu.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.wujie.zhihu.Adapter.RecyclerViewAdapter;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.support.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wujie on 2016/4/11.
 */
public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter mPresenter;

    private Context context;
    private RecyclerView mRecyclerView;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    private ViewGroup noNewsLayout;
    private View root;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int visibleNewsDate;
    private boolean isLoad = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        HomePresenter homePresenter = new HomePresenter(this);

        context = getContext();
        root = inflater.inflate(R.layout.content_fragment, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout = (ScrollChildSwipeRefreshLayout)root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryLight);
        noNewsLayout = (ViewGroup)root.findViewById(R.id.noNews);

        mRecyclerViewAdapter = new RecyclerViewAdapter(context, new ArrayList<HashMap<String, Object>>(),
                mSwipeRefreshLayout);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //控件已经设定在加载时无法再加载了
                //loadNews(mUrl);
                mPresenter.refreshNews();
            }
        });

        mRecyclerView.addOnScrollListener(mScrollListener);
        mPresenter.start();

        return root;
    }

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
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
                mPresenter.loadNews(visibleNewsDate - 1);
            }
        }
    };

    @Override
    public void setVisiableNewsDate(int date) {
        visibleNewsDate = date;
    }

    @Override
    public Context getViewContent() {
        return getContext();
    }

    @Override
    public void setLoad(boolean load) {
        isLoad = load;
    }

    @Override
    public void setRefreshIndicator(boolean refresh) {
        mSwipeRefreshLayout.setRefreshing(refresh);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showNews(ArrayList<HashMap<String, Object>> list) {
        mRecyclerViewAdapter.updateNewsList(list);

        mRecyclerView.setVisibility(View.VISIBLE);
        noNewsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoNews() {
        noNewsLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showAlertNoInternet() {
        Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
    }
}
