package com.example.wujie.zhihu.theme;

import android.content.Context;

import com.example.wujie.zhihu.BasePresenter;
import com.example.wujie.zhihu.BaseView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wujie on 2016/4/13.
 */
public interface ThemeContract {
    interface View extends BaseView<Presenter> {

        Context getViewContent();

        void setLoad(boolean load);

        void setRefreshIndicator(boolean refresh);

        void setVisiableNewsId(int id);

        void showNews(ArrayList<HashMap<String, Object>> list);

        void showNoNews();

        void showAlertNoInternet();

    }

    interface Presenter extends BasePresenter {

        void loadNews(int date);

        void refreshNews();

        void setThemeId(int id);

    }

}
