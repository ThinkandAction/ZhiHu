package com.example.wujie.zhihu.main;

import com.example.wujie.zhihu.BasePresenter;
import com.example.wujie.zhihu.BaseView;

/**
 * Created by wujie on 2016/4/12.
 */
public interface MainContract {
    interface View extends BaseView<Presenter>{

        void showLoginActivity();

        void showMenuSet();
    }
    interface Presenter extends BasePresenter{

        void openMenuSet();

        void openLoginActivity();
    }
}
