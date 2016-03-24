package com.example.wujie.zhihu.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.example.wujie.zhihu.Adapter.MainListAdapter;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.JsonLatestNews;
import com.example.wujie.zhihu.R;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wujie on 2016/3/14.
 */
public class HomeFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "url";
    private static final String STORY = "http://news-at.zhihu.com/api/4/story/";

    private Context context;
    private ListView mainListView;
    private ViewPager viewPager;
    private ImageLoader mImageLoader;
    private ArrayList<View> viewContainer;
    private View view;
    private LayoutInflater mInflater;
    private ViewGroup linearLayoutPoints;
    private TextView mTextView;
    private String[] top_Stories_Title;
    private String[] top_Stories_Url;
    private JsonLatestNews mResponse;
    private String mUrl;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mainListView = (ListView)view.findViewById(R.id.main_list);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        mTextView = (TextView)view.findViewById(R.id.title_text);
        viewContainer = new ArrayList<View>();
        linearLayoutPoints = (ViewGroup)view.findViewById(R.id.points_group);
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
                Intent intent = new Intent();
                Log.d("position", position+"");
                intent.setClass(context, ItemActivity.class);
                intent.putExtra("url", STORY + mResponse.getStories().get(position-1).getId());
                startActivity(intent);
            }
        });

        return view;
    }


    public void updateLatestNews(){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        GsonRequest gsonRequest = new GsonRequest<JsonLatestNews>(mUrl,
                JsonLatestNews.class,
                new Response.Listener<JsonLatestNews>() {
                    @Override
                    public void onResponse(JsonLatestNews response) {
                        mResponse = response;
                        String[] url = new String[response.getStories().size()];

                        Iterator iterator = response.getStories().iterator();

                        String[] title = new String[response.getStories().size()];
                        for (int i = 0;iterator.hasNext();i++){
                            JsonLatestNews.Stories m = (JsonLatestNews.Stories) iterator.next();
                            title[i] = m.getTitle();
                            if (m.getImages()!=null){
                                url[i] = m.getImages().iterator().next();
                            } else {
                                url[i] = "";
                            }
                        }

                        top_Stories_Title = new String[response.getTop_stories().size()];
                        top_Stories_Url = new String[response.getTop_stories().size()];
                        Iterator iterator_TopStories = response.getTop_stories().iterator();
                        for (int i = 0;iterator_TopStories.hasNext();i++){
                            JsonLatestNews.Top_stories m = (JsonLatestNews.Top_stories) iterator_TopStories.next();
                            top_Stories_Url[i] = m.getImage();
                            top_Stories_Title[i] = m.getTitle();
                        }
                        mainListView.setAdapter(new MainListAdapter(context, title, url, top_Stories_Title, top_Stories_Url,
                                R.layout.item_main_list, R.layout.view_pager, R.id.main_list_textview, R.id.main_list_image, 1));


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
