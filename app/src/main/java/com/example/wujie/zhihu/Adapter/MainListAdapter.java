package com.example.wujie.zhihu.Adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.view.ViewPager;
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
import com.example.wujie.zhihu.JsonLatestNews;
import com.example.wujie.zhihu.R;

import java.util.ArrayList;


/**
 * Created by wujie on 2016/3/13.
 */
public class MainListAdapter extends BaseAdapter {

    private String[] mUrl;
    private String[] mTitle;
    private String[] top_Stories_Title;
    private String[] top_Stories_Url;
    private String background;
    private String descripton;
    private int mResource;
    private int mText;
    private int mImage;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private int top_resource;
    private ArrayList<View> viewContainer;
    private ViewPager viewPager;
    private ViewGroup linearLayoutPoints;
    private TextView mTextView;
    private int mType;
    private int image_resources;

    public MainListAdapter(Context context, String[] title, String[] url, String background,String description, int resource, int image_resource, int text, int image, int type) {
        mContext = context;
        mUrl = url;
        mTitle = title;
        this.background = background;
        this.descripton = description;
        mResource = resource;
        this.image_resources = image_resource;
        mText = text;
        mImage = image;
        mType = type;
        mLayoutInflater = LayoutInflater.from(context);

        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    public MainListAdapter(Context context, String[] title, String[] url, String[] top_Stories_Title,
                           String[] top_Stories_Url, int resource, int top_resource, int text, int image, int type) {
        mContext = context;
        mUrl = url;
        mTitle = title;
        this.top_Stories_Title = top_Stories_Title;
        this.top_Stories_Url = top_Stories_Url;
        mResource = resource;
        this.top_resource = top_resource;
        mText = text;
        mImage = image;
        mType = type;
        mLayoutInflater = LayoutInflater.from(context);

        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public int getCount() {
        return mUrl.length+1;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (position == 0 && mType == 1){
            view = mLayoutInflater.inflate(top_resource, parent, false);
            for (int i = 0; i < top_Stories_Url.length; i++){
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
            for (int i = 0; i <top_Stories_Url.length; i++) {
                View imageView = mLayoutInflater.inflate(R.layout.view_pager_content, null);
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
        }else if (position == 0 && mType == 0){
            view = mLayoutInflater.inflate(image_resources, parent, false);
            RequestQueue mQueue = Volley.newRequestQueue(mContext);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
            mImageLoader.get(background, listener);
            TextView textView = (TextView)view.findViewById(R.id.title_text);
            textView.setText(descripton);

        } else {
            if (convertView == null){
                view = mLayoutInflater.inflate(mResource, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(mImage);
                viewHolder.textView= (TextView) view.findViewById(mText);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.textView.setText(mTitle[position-1]);
            String url = "";
            url = mUrl[position-1];
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
