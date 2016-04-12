package com.example.wujie.zhihu.data;

/**
 * Created by wujie on 2016/4/11.
 */
public class AppRepository{

    private AppRepository(){

    }

    public static AppRepository getnstance(){
        return new AppRepository();
    }
}
