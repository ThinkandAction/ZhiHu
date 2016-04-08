package com.example.wujie.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wujie on 2016/4/6.
 */
public final class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "home_news_lists";
    public static final String TABLE_NAME_1 = "no_boring_news_lists";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_OR_THEME_ID = "date_or_theme_id";
    public static final String COLUMN_CONTENT = "content";

    public static final String DATABASE_NAME = "daily_news.db";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_HOME
            = "CREATE TABLE " + TABLE_NAME
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE_OR_THEME_ID + " INTEGER, "
            + COLUMN_CONTENT + " TEXT NOT NULL);";

    private static final String CREATE_NOBORING
            = "CREATE TABLE " + TABLE_NAME_1
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE_OR_THEME_ID + " INTEGER, "
            + COLUMN_CONTENT + " TEXT NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HOME);
        db.execSQL(CREATE_NOBORING);
    }

    //可以改善，不删除
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        onCreate(db);
    }
}