package com.example.wujie.zhihu.detail;

import android.content.Context;
import android.widget.ImageView;

import com.example.wujie.zhihu.BasePresenter;
import com.example.wujie.zhihu.BaseView;

/**
 * Created by wujie on 2016/4/16.
 */
public interface DetailContract {
    interface View extends BaseView<Presenter> {

        Context getViewContent();

        ImageView getBackgroundView();

        void setCommentCount(int count);

        void setSupportCount(int count);

        void showCommentActivity();

        void showDetail(String body);

        void showLoginActivity();

        void showShareDialog();
    }
    interface Presenter extends BasePresenter {

        void doStar();

        void doSupport();

        void loadExtra(int id);

        void loadDetail(int id);

        void openCommentActivity();

        void openLoginActivity();

        void openShareDialog();
    }
}
