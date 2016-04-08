package com.example.wujie.zhihu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wujie on 2016/4/6.
 */
public class NoBoringDataBase {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private String[] allColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_DATE_OR_THEME_ID,
            DBHelper.COLUMN_CONTENT
    };

    public NoBoringDataBase(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void insertDailyNewsList(int id, String tableName, ArrayList<HashMap<String, Object>> content) {
        ContentValues values = new ContentValues();
        byte[] data = listToByteArray(content);
        values.put(DBHelper.COLUMN_DATE_OR_THEME_ID, id);
        values.put(DBHelper.COLUMN_CONTENT, data);

        database.insert(tableName, null, values);
    }


    public void updateNewsList(int id, String tableName, ArrayList<HashMap<String, Object>> content) {
        ContentValues values = new ContentValues();
        byte[] data = listToByteArray(content);
        values.put(DBHelper.COLUMN_DATE_OR_THEME_ID, id);
        values.put(DBHelper.COLUMN_CONTENT, data);
        database.update(tableName, values, DBHelper.COLUMN_DATE_OR_THEME_ID + "=" + id, null);
    }

    public void insertOrUpdateNewsList(int id, String tableName, ArrayList<HashMap<String, Object>> content) {
        if (newsOfTheDay(id, tableName) != null) {
            updateNewsList(id, tableName, content);
        } else {
            insertDailyNewsList(id, tableName, content);
        }
    }

    // That reminds you of Queen, huh? ;-)
    public ArrayList<HashMap<String, Object>> newsOfTheDay(int id, String tableName) {
        Cursor cursor = database.query(tableName,
                allColumns, DBHelper.COLUMN_DATE_OR_THEME_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        ArrayList<HashMap<String, Object>> newsList = cursorToNewsList(cursor);
        cursor.close();
        return newsList;
    }

    public int tableLastNewsId(String tableName){
        Cursor cursor = database.query(tableName,
                allColumns, null, null, null, null, null);
        int max = 0;
        int id = 0;
        if (cursor.moveToFirst()){
            do {
                id = cursor.getInt(1);
                if (id > max){
                    max = id;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return max;
    }
//不对，数据库里存储的数据可能不全，此方法得到的可能是更久以前的
    public int idBeforeId(int id, String tableName){
        Cursor cursor = database.query(tableName,
                allColumns, null, null, null, null, null);
        int targetId = 0;
        int middleId = 0;
        if (cursor.moveToFirst()){
            do {
                middleId = cursor.getInt(1);
                if (middleId < id & middleId >targetId){
                    targetId = middleId;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return targetId;
    }

    private ArrayList<HashMap<String, Object>> cursorToNewsList(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            //return new GsonBuilder().create().fromJson(cursor.getString(2), Constants.Type.ArrayListType);
            return byteArrayToList(cursor.getBlob(2));
        } else {
            return null;
        }
    }

    public byte[] listToByteArray(ArrayList<HashMap<String, Object>> list){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        byte data[] = null;
        try {
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(list);
            objectOutputStream.flush();
            data = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<HashMap<String, Object>> byteArrayToList(byte[] data){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
        ArrayList<HashMap<String, Object>> list = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
            list = (ArrayList<HashMap<String, Object>>) inputStream.readObject();
            inputStream.close();
            arrayInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
