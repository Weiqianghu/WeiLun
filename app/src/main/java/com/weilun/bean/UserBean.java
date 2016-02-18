package com.weilun.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by 胡伟强 on 2015/12/7.
 */
public class UserBean extends BmobUser implements Serializable {
    private Boolean sex;
    private String sexStr;
    private String img;
    private Integer age;

    public UserBean() {
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getImg() {
        if(img==null)
            return "http://file.bmob.cn/M02/E3/AB/oYYBAFZmgSCAKD2FAAAEdkC6sfc044.jpg";
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSexStr() {
        if(this.sex==null)
            return "未知";
        else if(this.sex==true)
            return "男";
        return "女";
    }
}
