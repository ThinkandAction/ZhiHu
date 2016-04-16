package com.example.wujie.zhihu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wujie.zhihu.detail.DetailActivity;
import com.example.wujie.zhihu.detail.DetailContract;
import com.example.wujie.zhihu.support.Constants;

import java.util.List;

/**
 * Created by wujie on 2016/3/15.
 */
public class MyPageAdapter extends PagerAdapter {
    List<View> viewLists;
    int[] mUrl;
    Context mContext;

    public MyPageAdapter(List<View> lists, int[] url, Context context)
    {
        viewLists = lists;
        mUrl = url;
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = viewLists.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                intent.putExtra("StoryId", mUrl[position]);//////!!!!!超出范围，不能点击
                mContext.startActivity(intent);
            }
        });
        container.addView(view, 0);
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }


    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
