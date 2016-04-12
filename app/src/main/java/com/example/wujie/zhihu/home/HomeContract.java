package com.example.wujie.zhihu.home;

import android.content.Context;

import com.example.wujie.zhihu.BasePresenter;
import com.example.wujie.zhihu.BaseView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wujie on 2016/4/11.
 */
public interface HomeContract {
    interface View extends BaseView<Presenter>{

        Context getViewContent();

        void setLoad(boolean load);

        void setRefreshIndicator(boolean refresh);

        void setVisiableNewsDate(int date);

        void showNews(ArrayList<HashMap<String, Object>> list);

        void showNoNews();

        void showAlertNoInternet();

    }

    interface Presenter extends BasePresenter{

        void loadNews();

    }

}
