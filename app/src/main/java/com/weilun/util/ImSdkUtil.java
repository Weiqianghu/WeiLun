package com.weilun.util;

import android.util.Log;

import imsdk.data.IMMyself;

/**
 * Created by 胡伟强 on 2016/1/9.
 */
public class ImSdkUtil {

    public static void imLogin(String username){
        // 设置本用户的用户名
        IMMyself.setCustomUserID(username);

        // 设置本用户密码
        IMMyself.setPassword(username);

        // 一键登录
        IMMyself.login(true, 5, new IMMyself.OnActionListener() {
            @Override
            public void onSuccess() {
                Log.d("im","login:"+"一键登录成功");
            }

            @Override
            public void onFailure(String error) {
                if (error.equals("Timeout")) {
                    error = "一键登录超时";
                } else if (error.equals("Wrong Password")) {
                    error = "密码错误";
                }

                Log.d("im","error:"+error);
            }
        });
    }


    public static void sendTextMessage(String targetUser,String content){
        // 动态配置超时时长
        IMMyself.sendText(content, targetUser, (content.length() / 400 + 1) * 5, new IMMyself.OnActionListener() {
            @Override
            public void onSuccess() {
                Log.d("im","发送成功");
            }

            @Override
            public void onFailure(String error) {
                Log.d("im","发送失败 -- " + error);
            }
        });
    }

}
