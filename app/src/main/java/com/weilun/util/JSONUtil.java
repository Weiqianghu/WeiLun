package com.weilun.util;

import com.weilun.bean.ChatMessageBean;
import com.weilun.bean.PostsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡伟强 on 2015/12/29.
 */
public class JSONUtil {
    public static List<String> getPostsObjectId(String JSONString) {
        List<String> postsObjectIdList = new ArrayList<>();
        if (JSONString == null) {
            return postsObjectIdList;
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.optJSONArray("results");
            for (int i = 0, length = jsonArray.length(); i < length; i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String postsObjectIdStr = jsonObject.optString("objectId");
                postsObjectIdList.add(postsObjectIdStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postsObjectIdList;
    }

    public static List<ChatMessageBean> getMsgList(String JSONString){
        List<ChatMessageBean> msgList=new ArrayList<>();
        if (JSONString == null) {
            return msgList;
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.optJSONArray("results");
            for (int i = jsonArray.length()-1; i >=0; i--) {
                jsonObject = jsonArray.getJSONObject(i);
                ChatMessageBean msg=new ChatMessageBean();
                String belongUserMobileNo=jsonObject.optString("belongUserMobileNo");
                String content=jsonObject.optString("content");
                String sendTime=jsonObject.optString("sendTime");
                String targetUserMobileNo=jsonObject.optString("targetUserMobileNo");

                msg.setBelongUserMobileNo(belongUserMobileNo);
                msg.setContent(content);
                msg.setSendTime(sendTime);
                msg.setTargetUserMobileNo(targetUserMobileNo);

                msgList.add(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgList;
    }

    public static PostsBean getPostsBean(String JSONString) {
        PostsBean postsBean = new PostsBean();
        if (JSONString == null) {
            return postsBean;
        }
        try {
            JSONObject jsonObject = new JSONObject(JSONString);

            String objectId = jsonObject.optString("objectId");
            String title = jsonObject.optString("title");
            String updatedAt = jsonObject.optString("updatedAt");
            JSONArray jsonArray = jsonObject.optJSONArray("postsImg");

            List<String> postsImg = new ArrayList<>();
            if (jsonArray != null) {
                for (int i = 0, length = jsonArray.length(); i < length; i++) {
                    postsImg.add((String) jsonArray.get(i));
                }
            }
            String content = jsonObject.optString("content");
            String createdAt = jsonObject.optString("createdAt");

            postsBean.setObjectId(objectId);
            postsBean.setTitle(title);
            postsBean.setUpdatedAtStr(updatedAt);
            postsBean.setPostsImg(postsImg);
            postsBean.setContent(content);
            return postsBean;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postsBean;
    }
}
