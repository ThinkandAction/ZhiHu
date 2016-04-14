package com.example.wujie.zhihu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.detail.ItemActivity;
import com.example.wujie.zhihu.Interface.OnRecyclerItemClickListener;
import com.example.wujie.zhihu.R;
import com.example.wujie.zhihu.cache.LevelTwoCache;
import com.example.wujie.zhihu.support.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wujie on 2016/3/31.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TOPSTORIES = 0;
    private static final int TYPE_STORIES = 1;
    private static final int TYPE_BACKGROUND = 2;  //注意：数值不要大于getViewTypeCount()的值
    private static final int TYPE_FOOTER = 3;
    private static final int MAX_DISKSIZE = 10 * 1024 *1024;
    private static final int MAX_LRUCACHESIZE = 10 * 1024 *1024;

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ArrayList<HashMap<String, Object>> mList;
    private ImageLoader mImageLoader;

    public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, Object>> list){
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);

        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new LevelTwoCache(context, "image", MAX_DISKSIZE, MAX_LRUCACHESIZE,
                Bitmap.CompressFormat.JPEG, 70));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        if (viewType == TYPE_TOPSTORIES){
            view = mLayoutInflater.inflate(R.layout.view_pager, parent, false);
        } else if (viewType == TYPE_STORIES){
            view = mLayoutInflater.inflate(R.layout.recyclerview_item_card, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mRecyclerItemClickListener != null){
                        mRecyclerItemClickListener.onItemClick(view, (int)view.getTag());
                    }
                }
            });
        } else if (viewType == TYPE_FOOTER){
            view = mLayoutInflater.inflate(R.layout.footer, parent, false);
        }else {
            view = mLayoutInflater.inflate(R.layout.background, parent, false);
        }
        MyViewHolder viewHolder = new MyViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder)holder;
        if (getItemViewType(position) == TYPE_TOPSTORIES){
            viewHolder.linearLayoutPoints.removeAllViews();
            String[] top_Stories_Title = (String[])(mList.get(position).get("Top_Stories_Title"));
            Log.d("TOP", top_Stories_Title.toString());
            for (int i = 0; i < top_Stories_Title.length; i++){
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.ic_launcher);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(25, 25);
                lp.setMargins(10, 0, 10, 0);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewHolder.linearLayoutPoints.addView(imageView);
            }

            ArrayList<View> viewContainer = new ArrayList<View>();
            String[] top_Stories_Url = (String[])mList.get(position).get("Top_Stories_Url");
            for (int i = 0; i <top_Stories_Url.length; i++) {
                View imageView = mLayoutInflater.inflate(R.layout.view_pager_content, null); //-----------------------
                viewContainer.add(imageView);
            }

            for (int i = 0; i <top_Stories_Url.length; i++) {
                String test_url = "";
                test_url = top_Stories_Url[i];
                ImageLoader.ImageListener listener = ImageLoader.getImageListener((ImageView)viewContainer.get(i).findViewById(R.id.pager_image), R.color.colorwWhite, 0);
                mImageLoader.get(test_url, listener);
            }
            viewHolder.viewPager.setAdapter(new MyPageAdapter(viewContainer));
            viewHolder.viewPager.addOnPageChangeListener(new ViewpagerListener(viewHolder.linearLayoutPoints,
                    viewHolder.textView, top_Stories_Title));
            viewHolder.textView.setText(top_Stories_Title[position]);
        } else if (getItemViewType(position) == TYPE_BACKGROUND){
            Log.d("TAG", "-----------Background----");
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.imageView, 0, 0);
            mImageLoader.get(mList.get(position).get("Background").toString(), listener);
            viewHolder.textView.setText(mList.get(position).get("Description").toString());
        } else if (getItemViewType(position) == TYPE_FOOTER){
            if (position == 0){
                viewHolder.itemView.setVisibility(View.GONE);
            } else {
                viewHolder.itemView.setVisibility(View.VISIBLE);
            }
        }else {
            viewHolder.textView.setText(mList.get(position).get("Stories_Title").toString());
            String url = "";
            url = mList.get(position).get("Stories_Url").toString();
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.imageView, R.color.colorwWhite, 0);//设置了默认图片后就不会导致图片加载错乱闪动
            mImageLoader.get(url, listener);
            viewHolder.itemView.setTag(position);//为了让点击事件获得position
        }

    }


    private void setNewsList(ArrayList<HashMap<String, Object>> list) {
        mList = list;
    }

    private void addNewsList(ArrayList<HashMap<String, Object>> list) {
        mList.addAll(list);
    }

    public void updateNewsList(ArrayList<HashMap<String, Object>> list) {
        if (list.get(0).containsKey("Background") || list.get(0).containsKey("Top_Stories_Title")){
            setNewsList(list);
        } else {
            addNewsList(list);
        }

        notifyDataSetChanged();//在fragment中调用没效果？
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()){
            return TYPE_FOOTER;
        } else if (mList.get(position).containsKey("Stories_Title")){
            return TYPE_STORIES;
        }else if(mList.get(position).containsKey("Top_Stories_Title")){
            return TYPE_TOPSTORIES;
        } else {
            return TYPE_BACKGROUND;
        }



    }

    OnRecyclerItemClickListener mRecyclerItemClickListener = new OnRecyclerItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent();
            intent.setClass(mContext, ItemActivity.class);
            int id = 0;
            if (mList.get(position).get("Stories_Id") instanceof Double) {
                double m = (double) mList.get(position).get("Stories_Id");////为什么取出的数据变成了double
                id = (int) Math.floor(m);
            } else {
                id = (int) mList.get(position).get("Stories_Id");
            }
            intent.putExtra("url", Constants.Url.STORY_DETAIL + id);//////!!!!!超出范围，不能点击
            mContext.startActivity(intent);
            //getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        private ViewPager viewPager;
        private ViewGroup linearLayoutPoints;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_TOPSTORIES){
                textView = (TextView)itemView.findViewById(R.id.title_text);
                viewPager = (ViewPager)itemView.findViewById(R.id.view_pager);
                linearLayoutPoints = (ViewGroup)itemView.findViewById(R.id.points_group);
            } else if (viewType == TYPE_BACKGROUND || viewType == TYPE_STORIES){
                imageView = (ImageView)itemView.findViewById(R.id.imageView);
                textView = (TextView)itemView.findViewById(R.id.textView);
            }
        }
    }

    /**
     * 自定义RecyclerView 中item view点击回调方法
     */
    public class ViewpagerListener implements ViewPager.OnPageChangeListener{

        private ViewGroup linearLayoutPoints;
        private TextView textView;
        private String[] title;
        public ViewpagerListener(ViewGroup linearLayoutPoints, TextView textView, String[] title) {
            this.linearLayoutPoints = linearLayoutPoints;
            this.textView = textView;
            this.title = title;
        }

        //当页面被滑动时调用position :当前页面，及你点击滑动的页面
        // positionOffset:当前页面偏移的百分比positionOffsetPixels:当前页面偏移的像素位置
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当页面被选中时调用；position为当前选中页面的编号
        @Override
        public void onPageSelected(int position) {
            ((ImageView)linearLayoutPoints.getChildAt(position)).setImageResource(R.drawable.icon_add);
            if (position == 0){
                ((ImageView)linearLayoutPoints.getChildAt(position+1)).setImageResource(R.drawable.ic_launcher);
            } else if (position == linearLayoutPoints.getChildCount()-1) {
                ((ImageView)linearLayoutPoints.getChildAt(position-1)).setImageResource(R.drawable.ic_launcher);
            }else {
                ((ImageView)linearLayoutPoints.getChildAt(position-1)).setImageResource(R.drawable.ic_launcher);
                ((ImageView)linearLayoutPoints.getChildAt(position+1)).setImageResource(R.drawable.ic_launcher);
            }
            textView.setText(title[position]);
        }

        //当页面滑动状态改变时调用；有三种状态（0，1，2）。state ==1的时辰表示正在滑动，
        // state==2的时辰表示滑动完毕了，state==0的时辰表示什么都没做
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
