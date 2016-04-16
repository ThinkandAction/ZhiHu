package com.example.wujie.zhihu.detail;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.wujie.zhihu.GsonRequest;
import com.example.wujie.zhihu.Info.ContentInfo;
import com.example.wujie.zhihu.Info.StoryExtraInfo;
import com.example.wujie.zhihu.cache.LruImageCache;
import com.example.wujie.zhihu.support.Constants;

/**
 * Created by wujie on 2016/4/16.
 */
public class DetailPresenter implements DetailContract.Presenter {

    private final DetailContract.View mDetailView;
    private RequestQueue mQueue;

    public DetailPresenter(DetailContract.View view){
        mDetailView = view;
        mDetailView.setPresenter(this);
    }

    private void getRequestQueue(){
        mQueue = Volley.newRequestQueue(mDetailView.getViewContent());
    }

    @Override
    public void doStar() {

    }

    @Override
    public void doSupport() {

    }

    @Override
    public void loadExtra(int id) {
        GsonRequest gsonRequest = new GsonRequest<StoryExtraInfo>(Constants.Url.STORY_EXTRA + id,
                StoryExtraInfo.class, new Response.Listener<StoryExtraInfo>() {
            @Override
            public void onResponse(StoryExtraInfo storyExtraInfo) {
                mDetailView.setCommentCount(storyExtraInfo.getComments());
                mDetailView.setSupportCount(storyExtraInfo.getPopularity());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG", volleyError.getMessage(), volleyError);
                Log.e("error", "error!!!!!!!");
            }
        });
        mQueue.add(gsonRequest);
    }

    @Override
    public void loadDetail(final int id) {
        if (mQueue == null){
            getRequestQueue();
        }
        GsonRequest gsonRequest = new GsonRequest<ContentInfo>(Constants.Url.STORY_DETAIL + id,
                ContentInfo.class,
                new Response.Listener<ContentInfo>() {
                    @Override
                    public void onResponse(ContentInfo response) {
                        String linkCss = "<link rel=\"stylesheet\" href=\""+response.getCss().iterator().next()+ "\"type=\"text/css\">";
                        String body = "<html><header>" + linkCss + "</header>" + response.getBody() + "</body></html>";
                        mDetailView.showDetail(body);
                        if (response.getImage() != null){
                            ImageLoader mImageLoader = new ImageLoader(mQueue, new LruImageCache(10*1024*1024));
                            ImageLoader.ImageListener listener = ImageLoader.getImageListener(mDetailView.getBackgroundView(), 0, 0);
                            mImageLoader.get(response.getImage(), listener);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Log.e("error", "error!!!!!!!");

            }
        });
        mQueue.add(gsonRequest);
    }

    @Override
    public void openCommentActivity() {
        mDetailView.showCommentActivity();
    }

    @Override
    public void openLoginActivity() {
        mDetailView.showLoginActivity();
    }

    @Override
    public void openShareDialog() {
        mDetailView.showShareDialog();
    }

    @Override
    public void start() {

    }
}
