package com.example.wujie.zhihu.main;


/**
 * Created by wujie on 2016/4/12.
 */
public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mMainView;

    public MainPresenter( MainContract.View view){
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
