package com.example.wujie.zhihu.Info;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wujie on 2016/3/13.
 */
public class JsonLatestNews {
    private String date;

    private ArrayList<Stories> stories ;

    private ArrayList<Top_stories> top_stories ;

    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setStories(ArrayList<Stories> stories){
        this.stories = stories;
    }
    public ArrayList<Stories> getStories(){
        return this.stories;
    }
    public void setTop_stories(ArrayList<Top_stories> top_stories){
        this.top_stories = top_stories;
    }
    public ArrayList<Top_stories> getTop_stories(){
        return this.top_stories;
    }


    public class Stories {
        private ArrayList<String> images ;

        private int type;

        private int id;

        private String ga_prefix;

        private String title;

        public void setImages(ArrayList<String> images){
            this.images = images;
        }
        public ArrayList<String> getImages(){
            return this.images;
        }
        public void setType(int type){
            this.type = type;
        }
        public int getType(){
            return this.type;
        }
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setGa_prefix(String ga_prefix){
            this.ga_prefix = ga_prefix;
        }
        public String getGa_prefix(){
            return this.ga_prefix;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }

    }


    public class Top_stories {
        private String image;

        private int type;

        private int id;

        private String ga_prefix;

        private String title;

        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setType(int type){
            this.type = type;
        }
        public int getType(){
            return this.type;
        }
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setGa_prefix(String ga_prefix){
            this.ga_prefix = ga_prefix;
        }
        public String getGa_prefix(){
            return this.ga_prefix;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }

    }
}
