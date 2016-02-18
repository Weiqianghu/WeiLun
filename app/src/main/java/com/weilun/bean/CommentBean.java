package com.weilun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 胡伟强 on 2015/12/16.
 */
public class CommentBean extends BmobObject {
    protected String content;
    private UserBean author;
    private PostsBean posts;
    private String updatedAtStr;

    public String getUpdatedAtStr() {
        return updatedAtStr;
    }

    public void setUpdatedAtStr(String updatedAtStr) {
        this.updatedAtStr = updatedAtStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public PostsBean getPosts() {
        return posts;
    }

    public void setPosts(PostsBean posts) {
        this.posts = posts;
    }
}
