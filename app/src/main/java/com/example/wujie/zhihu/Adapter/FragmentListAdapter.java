package com.example.wujie.zhihu.Adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.BitmapCache;
import com.example.wujie.zhihu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wujie on 2016/3/26.
 */
public class FragmentListAdapter extends BaseAdapter {

    private static final int TYPE_TOPSTORIES = 0;
    private static final int TYPE_STORIES = 1;
    private static final int TYPE_BACKGROUND = 2;  //注意：数值不要大于getViewTypeCount()的值

    private String[] top_Stories_Title;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private ArrayList<View> viewContainer;
    private ViewPager viewPager;
    private ViewGroup linearLayoutPoints;
    private TextView mTextView;
    private List<HashMap<String, Object>> mList;
    private int[] mLayout;


    public FragmentListAdapter(Context context, List<HashMap<String, Object>> list, int[] layout){
        mContext = context;
        mList = list;
        mLayout = layout;
        mLayoutInflater = LayoutInflater.from(context);

        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).containsKey("Top_Stories_Title")){
            return TYPE_TOPSTORIES;
        } else if (mList.get(position).containsKey("Stories_Title")){
            return TYPE_STORIES;
        }else {
            return TYPE_BACKGROUND;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (getItemViewType(position) == TYPE_TOPSTORIES){
            view = mLayoutInflater.inflate(mLayout[0], parent, false);
            top_Stories_Title = (String[])mList.get(position).get("Top_Stories_Title");
            Log.d("TOP", top_Stories_Title.toString());
            for (int i = 0; i < top_Stories_Title.length; i++){
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.ic_launcher);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(25, 25);
                lp.setMargins(10, 0, 10, 0);
                imageView.setLayoutParams(lp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                linearLayoutPoints = (ViewGroup)view.findViewById(R.id.points_group);
                linearLayoutPoints.addView(imageView);
            }

            ArrayList<View> arrayList = new ArrayList<View>();
            String[] top_Stories_Url = (String[])mList.get(position).get("Top_Stories_Url");
            for (int i = 0; i <top_Stories_Url.length; i++) {
                View imageView = mLayoutInflater.inflate(R.layout.view_pager_content, null); //-----------------------
                arrayList.add(imageView);
            }
            //viewContainer = new ArrayList<View>();
            viewContainer = arrayList;

            RequestQueue mQueue = Volley.newRequestQueue(mContext);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
            for (int i = 0; i <top_Stories_Url.length; i++) {
                String test_url = "";
                test_url = top_Stories_Url[i];
                ImageLoader.ImageListener listener = ImageLoader.getImageListener((ImageView)viewContainer.get(i).findViewById(R.id.pager_image), 0, 0);
                mImageLoader.get(test_url, listener);
            }
            mTextView = (TextView)view.findViewById(R.id.title_text);
            viewPager = (ViewPager)view.findViewById(R.id.view_pager);
            viewPager.setAdapter(new MyPageAdapter(viewContainer));
            mTextView.setText(top_Stories_Title[position]);
            viewPager.addOnPageChangeListener(new ViewpagerListener());
        }else if (getItemViewType(position) == TYPE_BACKGROUND){
            view = mLayoutInflater.inflate(mLayout[2], parent, false);
            RequestQueue mQueue = Volley.newRequestQueue(mContext);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
            mImageLoader.get(mList.get(position).get("Background").toString(), listener);
            TextView textView = (TextView)view.findViewById(R.id.title_text);
            textView.setText(mList.get(position).get("Description").toString());

        } else {
            if (convertView == null){
                view = mLayoutInflater.inflate(mLayout[1], parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.main_list_image);
                viewHolder.textView= (TextView) view.findViewById(R.id.main_list_textview);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.textView.setText(mList.get(position).get("Stories_Title").toString());
            String url = "";
            url = mList.get(position).get("Stories_Url").toString();
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.imageView, 0, 0);
            mImageLoader.get(url, listener);
        }
        return view;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    public class ViewpagerListener implements ViewPager.OnPageChangeListener{

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
            mTextView.setText(top_Stories_Title[position]);
        }

        //当页面滑动状态改变时调用；有三种状态（0，1，2）。state ==1的时辰表示正在滑动，
        // state==2的时辰表示滑动完毕了，state==0的时辰表示什么都没做
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
