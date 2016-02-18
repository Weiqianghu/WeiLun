package com.weilun.util;

import android.content.Context;
import android.util.Log;

import com.weilun.bean.FriendsBean;
import com.weilun.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 胡伟强 on 2015/12/17.
 */
public class AddFriendUtil {

    public static void addFriend(final Context context, UserBean currentUser, UserBean friendBean) {
        if (!verify(context, currentUser, friendBean,friendBean))
            return;
        final FriendsBean friend = new FriendsBean();

        BmobRelation userRelation = new BmobRelation();
        userRelation.add(currentUser);

        BmobRelation friendRelation = new BmobRelation();
        friendRelation.add(friendBean);

        friend.setUser(userRelation);
        friend.setFriend(friendRelation);

        BmobQuery<FriendsBean> objectQuery = new BmobQuery<>();
        objectQuery.addWhereEqualTo("user", currentUser);
        objectQuery.findObjects(context, new FindListener<FriendsBean>() {
            @Override
            public void onSuccess(List<FriendsBean> list) {
                if (list.size() > 0) {
                    friend.setObjectId(list.get(0).getObjectId());
                    friend.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("addfriend", "加好友成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.d("addfriend", "加好友失败");
                        }
                    });
                } else {
                    friend.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("addfriend", "加好友成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.d("addfriend", "加好友失败");
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("addfriend", "加好友失败");
            }
        });
    }

    private static boolean verify(Context context, UserBean currentUser, UserBean friendBean,UserBean friend) {
        if (currentUser.getObjectId() == friendBean.getObjectId())
            return false;
        return true;
    }
}
