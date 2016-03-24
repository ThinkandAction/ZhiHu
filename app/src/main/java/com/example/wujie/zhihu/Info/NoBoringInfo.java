package com.example.wujie.zhihu.Info;

import java.util.List;

/**
 * Created by wujie on 2016/3/21.
 */
public class NoBoringInfo {

    /**
     * stories : [{"images":["http://pic1.zhimg.com/e3f596c7ed9e470733f0637adb6124e4.jpg"],"type":0,"id":7468668,"title":" "}]
     * subscriibed : False
     * image : ""
     * image_source :
     * name :
     * editors : [{"avatar":"","bio":"","id":1,"name":"","url":""}]
     * description : 为你发现最有趣的新鲜事，建议在 WiFi 下查看
     * background : http://pic1.zhimg.com/a5128188ed788005ad50840a42079c41.jpg
     * color : 8307764
     */

    private String subscriibed;
    private String image_source;
    private String name;
    private String description;
    private String background;
    private int color;
    /**
     * images : ["http://pic1.zhimg.com/e3f596c7ed9e470733f0637adb6124e4.jpg"]
     * type : 0
     * id : 7468668
     * title :
     */

    private List<StoriesBean> stories;
    private String image;
    /**
     * avatar :
     * bio :
     * id : 1
     * name :
     * url :
     */

    private List<EditorsBean> editors;

    public String getSubscriibed() {
        return subscriibed;
    }

    public void setSubscriibed(String subscriibed) {
        this.subscriibed = subscriibed;
    }

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<EditorsBean> getEditors() {
        return editors;
    }

    public void setEditors(List<EditorsBean> editors) {
        this.editors = editors;
    }

    public static class StoriesBean {
        private int type;
        private int id;
        private String title;
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class EditorsBean {
        private String avatar;
        private String bio;
        private int id;
        private String name;
        private String url;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
