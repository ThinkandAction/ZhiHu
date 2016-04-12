package com.example.wujie.zhihu.main;

import com.example.wujie.zhihu.data.AppRepository;

/**
 * Created by wujie on 2016/4/12.
 */
public class MainPresenter implements MainContract.Presenter {

    private final AppRepository mAppRepository;
    private final MainContract.View mMainView;

    public MainPresenter(AppRepository appRepository, MainContract.View view){
        mAppRepository = appRepository;
        mMainView = view;

        mMainView.setPresenter(this);
    }

    @Override
    public void openMenuSet() {
        mMainView.showMenuSet();
    }

    @Override
    public void openLoginActivity() {
        mMainView.showLoginActivity();
    }

    @Override
    public void start() {

    }
}
