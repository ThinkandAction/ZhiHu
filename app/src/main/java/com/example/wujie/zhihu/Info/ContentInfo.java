package com.example.wujie.zhihu.Info;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wujie on 2016/3/20.
 */
public class ContentInfo {

    /**
     * body :
     * image_source : Yestone.com 版权图片库
     * title : 深夜惊奇 · 朋友圈错觉
     * image : http://pic3.zhimg.com/2d41a1d1ebf37fb699795e78db76b5c2.jpg
     * share_url : http://daily.zhihu.com/story/4772126
     * js : []
     * recommenders : [{"avatar":"http://pic2.zhimg.com/fcb7039c1_m.jpg"},{"avatar":"http://pic1.zhimg.com/29191527c_m.jpg"},{"avatar":"http://pic4.zhimg.com/e6637a38d22475432c76e6c9e46336fb_m.jpg"},{"avatar":"http://pic1.zhimg.com/bd751e76463e94aa10c7ed2529738314_m.jpg"},{"avatar":"http://pic1.zhimg.com/4766e0648_m.jpg"}]
     * ga_prefix : 050615
     * section : {"thumbnail":"http://pic4.zhimg.com/6a1ddebda9e8899811c4c169b92c35b3.jpg","id":1,"name":"深夜惊奇"}
     * type : 0
     * id : 4772126
     * css : ["http://news.at.zhihu.com/css/news_qa.auto.css?v=1edab"]
     */

    private String body;
    private String image_source;
    private String title;
    private String image;
    private String share_url;
    private String ga_prefix;
    /**
     * thumbnail : http://pic4.zhimg.com/6a1ddebda9e8899811c4c169b92c35b3.jpg
     * id : 1
     * name : 深夜惊奇
     */

    @SerializedName("section")
    private SectionBean theme;
    private int type;
    private int id;
    private List<?> js;
    /**
     * avatar : http://pic2.zhimg.com/fcb7039c1_m.jpg
     */

    private List<RecommendersBean> recommenders;
    private List<String> css;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public SectionBean getTheme() {
        return theme;
    }

    public void setTheme(SectionBean theme) {
        this.theme = theme;
    }

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

    public List<?> getJs() {
        return js;
    }

    public void setJs(List<?> js) {
        this.js = js;
    }

    public List<RecommendersBean> getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(List<RecommendersBean> recommenders) {
        this.recommenders = recommenders;
    }

    public List<String> getCss() {
        return css;
    }

    public void setCss(List<String> css) {
        this.css = css;
    }

    public static class SectionBean {
        private String thumbnail;
        private int id;
        private String name;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
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
    }

    public static class RecommendersBean {
        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
