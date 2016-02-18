package com.weilun.usermessage;

import android.content.Context;
import android.content.SharedPreferences;

import com.weilun.bean.PostsBean;
import com.weilun.bean.UserBean;
import com.weilun.util.HttpUtil;
import com.weilun.util.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 胡伟强 on 2015/12/28.
 */
public class UserReviewRemind {
    private static int reviewNumber = 0;
    private static String userObjectid;

    public static synchronized String getUserReviewCount(final Context context) {
        final UserBean user = BmobUser.getCurrentUser(context, UserBean.class);
        userObjectid = user.getObjectId();
        String userPostsJSONStr = HttpUtil.getJSONStringByMethod("getPostsByUserId", userObjectid);
        List<String> userPostsObjectIdList = JSONUtil.getPostsObjectId(userPostsJSONStr);

        synchronized (UserReviewRemind.class) {
            reviewNumber = 0;
            for (int i = 0, length = userPostsObjectIdList.size(); i < length; i++) {
                String reviewNumberJSONStr = HttpUtil.
                        getJSONStringByMethod("getCommentsNumByPostsId", userPostsObjectIdList.get(i));
                reviewNumber += Integer.valueOf(reviewNumberJSONStr);
            }
        }
        int newNumber = reviewNumber - loadReviewCount(context, userObjectid);
        return String.valueOf(newNumber);
    }

    public static void saveReviewCount(Context context) {
        saveReviewCount(context, userObjectid, reviewNumber);
    }

    public static void saveReviewCount(Context context, String userObjectId, int reviewNum) {
        SharedPreferences share = context.getSharedPreferences(userObjectId, 0);
        SharedPreferences.Editor edit = share.edit(); //编辑文件
        edit.putInt("reviewNum", reviewNum);
        edit.commit();  //保存数据信息
    }

    public static int loadReviewCount(Context context, String userObjectId) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences(userObjectId, 0);
        return share.getInt("reviewNum", 0);
    }

    public static List<String> getReviewPostsList(Context context) {
        List<String> reviewPostsList=new ArrayList<>();
        String userPostsJSONStr = HttpUtil.getJSONStringByMethod("getPostsByUserId", userObjectid);
        List<String> userPostsObjectIdList = JSONUtil.getPostsObjectId(userPostsJSONStr);

        for (int i = 0, length = userPostsObjectIdList.size(); i < length; i++) {
            String postsId=userPostsObjectIdList.get(i);
            String reviewNumberJSONStr = HttpUtil.
                    getJSONStringByMethod("getCommentsNumByPostsId",postsId );
            if(Integer.valueOf(reviewNumberJSONStr)>loadReviewCountByPostsId(context,userObjectid,postsId)){
                reviewPostsList.add(postsId);
            }

        }
        return reviewPostsList;
    }

    public static List<PostsBean> getPostsList(Context context){
        List<String> postsIdList=getReviewPostsList(context);
        List<PostsBean> postsBeanList=new ArrayList<>();
        UserBean user = BmobUser.getCurrentUser(context, UserBean.class);
        for(int i=0,length=postsIdList.size();i<length;i++){
            String postsJSONStr = HttpUtil.
                    getJSONStringByMethod("getPostsByPostsId", postsIdList.get(i));

            PostsBean postsBean=JSONUtil.getPostsBean(postsJSONStr);
            postsBean.setAuthor(user);
            postsBeanList.add(postsBean);
        }
        return postsBeanList;
    }


    public static void saveReviewCountByPostsId(final Context context, final String userObjectId, final String postsObjectId) {

        new Thread(){
            public void run(){
                String reviewNumberJSONStr = HttpUtil.
                        getJSONStringByMethod("getCommentsNumByPostsId",postsObjectId );
                SharedPreferences share = context.getSharedPreferences(userObjectId, 0);
                SharedPreferences.Editor edit = share.edit(); //编辑文件
                edit.putInt(postsObjectId, Integer.valueOf(reviewNumberJSONStr));
                edit.commit();  //保存数据信息
            }
        }.start();
    }

    public static int loadReviewCountByPostsId(Context context, String userObjectId, String postsObjectId) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences(userObjectId, 0);
        return share.getInt(postsObjectId, 0);
    }
}
