package com.example.wujie.zhihu;

import android.app.Application;

import com.example.wujie.zhihu.db.NewsDataBase;

/**
 * Created by wujie on 2016/4/7.
 */
public class ZhiHuDailyApplication extends Application {
    private static ZhiHuDailyApplication applicationContext;
    private static NewsDataBase dataBase;

    /*public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }
*/
    public static ZhiHuDailyApplication getInstance() {
        return applicationContext;
    }

    public static NewsDataBase getDataBase() {
        return dataBase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;

        //initImageLoader(getApplicationContext());
        dataBase = new NewsDataBase(getApplicationContext());
        dataBase.open();
    }
}
