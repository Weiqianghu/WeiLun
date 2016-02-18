package com.weilun.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 胡伟强 on 2015/12/9.
 */
public class FriendsBean extends BmobObject {
    private BmobRelation user;
    private BmobRelation friend;

    public BmobRelation getUser() {
        return user;
    }

    public void setUser(BmobRelation user) {
        this.user = user;
    }

    public BmobRelation getFriend() {
        return friend;
    }

    public void setFriend(BmobRelation friend) {
        this.friend = friend;
    }
}
