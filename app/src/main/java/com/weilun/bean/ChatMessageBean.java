package com.weilun.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 胡伟强 on 2016/1/9.
 */
public class ChatMessageBean extends BmobObject {
    private String belongUserMobileNo;
    private String sendTime;
    private String targetUserMobileNo;
    private String content;

    private String userImgUrl;
    private String friendImgUrl;

    public String getFriendImgUrl() {
        return friendImgUrl;
    }

    public void setFriendImgUrl(String friendImgUrl) {
        this.friendImgUrl = friendImgUrl;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getBelongUserMobileNo() {
        return belongUserMobileNo;
    }

    public void setBelongUserMobileNo(String belongUserMobileNo) {
        this.belongUserMobileNo = belongUserMobileNo;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getTargetUserMobileNo() {
        return targetUserMobileNo;
    }

    public void setTargetUserMobileNo(String targetUserMobileNo) {
        this.targetUserMobileNo = targetUserMobileNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
