package com.weilun.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.weilun.bean.UserBean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 胡伟强 on 2015/12/8.
 */
public class UserUtil {

    public static void updateUser(final Context context, final UserBean userBean){
        UserBean userInfo = BmobUser.getCurrentUser(context,UserBean.class);
        userBean.update(context,userInfo.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("bmob","更新成功");
                loginUser(context,userBean);
            }
            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.d("bmob","更新失败"+msg);
            }
        });
    }

    public static void loginUser(Context context,UserBean userBean){
        userBean.login(context, new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d("bmob","登录成功");
            }
            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.d("bmob","登录失败"+msg);
            }
        });
    }

    public static void initUser(UserBean userBean){
        if(userBean.getAge()==null){
            userBean.setAge(0);
        }
    }

    public static void updateCurrentUserPassword(final Context context, String oldPassword, String newPassword){
        BmobUser.updateCurrentUserPassword(context, oldPassword, newPassword, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.i("smile", "密码修改成功，可以用新密码进行登录啦");
                Toast.makeText(context,"密码修改成功，可以用新密码进行登录啦",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.i("smile", "密码修改失败："+msg+"("+code+")");
                Toast.makeText(context,"密码修改失败,可能是原密码输入错误:"+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
