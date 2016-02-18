package com.weilun.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by 胡伟强 on 2015/12/9.
 */
public class PostsBean extends BmobObject implements Serializable {
    private String title;
    private String content;
    private UserBean author;
    private String updatedAtStr;
    private List<String> postsImg;

    public String getUpdatedAtStr() {
        return updatedAtStr;
    }

    public void setUpdatedAtStr(String updatedAtStr) {
        this.updatedAtStr = updatedAtStr;
    }

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPostsImg() {
        return postsImg;
    }

    public void setPostsImg(List<String> postsImg) {
        this.postsImg = postsImg;
    }
}
