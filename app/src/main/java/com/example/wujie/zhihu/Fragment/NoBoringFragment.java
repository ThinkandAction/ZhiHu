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
import com.example.wujie.zhihu.Adapter.MainListAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.NoBoringInfo;
import com.example.wujie.zhihu.R;
import java.util.Iterator;

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
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        context = getActivity();
        updateLatestNews();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLatestNews();
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
                    intent.putExtra("url", STORY + mResponse.getStories().get(position-1).getId());
                    startActivity(intent);
                }
            }
        });

        return view;
    }



    public void updateLatestNews(){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest<NoBoringInfo>(mUrl,
                NoBoringInfo.class,
                new Response.Listener<NoBoringInfo>() {
                    @Override
                    public void onResponse(NoBoringInfo response) {
                        mResponse = response;
                        String[] url = new String[response.getStories().size()];

                        Iterator iterator = response.getStories().iterator();

                        String[] title = new String[response.getStories().size()];
                        for (int i = 0;iterator.hasNext();i++){
                            NoBoringInfo.StoriesBean m = (NoBoringInfo.StoriesBean) iterator.next();
                            title[i] = m.getTitle();
                            if (m.getImages()!=null){
                                url[i] = m.getImages().iterator().next();
                            } else {
                                url[i] = "";
                            }
                        }
                        String background = response.getBackground();
                        String description = response.getDescription();
                        mainListView.setAdapter(new MainListAdapter(context, title, url, background, description, R.layout.item_main_list,
                                R.layout.title_image, R.id.main_list_textview, R.id.main_list_image, 0));

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Log.e("error", "error!!!!!!!");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mQueue.add(gsonRequest);
    }
}
