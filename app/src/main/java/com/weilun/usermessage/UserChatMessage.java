package com.weilun.usermessage;

import android.os.AsyncTask;

import com.weilun.bean.ChatMessageBean;
import com.weilun.util.HttpUtil;
import com.weilun.util.JSONUtil;

import java.util.List;

/**
 * Created by 胡伟强 on 2016/1/9.
 */
public class UserChatMessage {

    public static List<ChatMessageBean> getChatMsgListOfFriend(String userMobileNo, String friendMobileNo,int start) {
        String msgJSONStr = HttpUtil.getJSONStringByMethod("getSendMsgByFriendMobileNo", userMobileNo, friendMobileNo,start);
        return JSONUtil.getMsgList(msgJSONStr);
    }

}
