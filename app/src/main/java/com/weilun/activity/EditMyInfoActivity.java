package com.weilun.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.internal.Util;
import com.weilun.bean.UserBean;
import com.weilun.ui.ClearEditText;
import com.weilun.util.UserUtil;
import com.weilun.weilun.R;

import cn.bmob.v3.BmobUser;

public class EditMyInfoActivity extends AppCompatActivity {

    private TextView mobileNo;
    private TextView userName;
    private TextView age;
    private TextView sex;
    private UserBean userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);
        initView();
    }

    private void initView() {
        userInfo = BmobUser.getCurrentUser(EditMyInfoActivity.this, UserBean.class);
        mobileNo = (TextView) findViewById(R.id.mobileNo);
        userName = (TextView) findViewById(R.id.userName);
        age = (TextView) findViewById(R.id.age);
        sex = (TextView) findViewById(R.id.sex);

        mobileNo.setText(userInfo.getMobilePhoneNumber());
        userName.setText(userInfo.getUsername());
        UserUtil.initUser(userInfo);
        age.setText(userInfo.getAge() + "");
        sex.setText(userInfo.getSexStr());
    }

    public void editUserName(View view) {
        final EditText temp = new EditText(this);
        temp.setText(userName.getText());
        new AlertDialog.Builder(this).setTitle("修改用户名").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                temp).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (temp.getText().toString().length() > 1)
                    userName.setText(temp.getText());
                UserBean user = new UserBean();
                user.setUsername(temp.getText().toString());
                UserUtil.updateUser(EditMyInfoActivity.this, user);
            }
        }).setNegativeButton("取消", null).show();
    }

    public void editSex(View view) {
        final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
        final int choice= (userInfo.getSex()==null || userInfo.getSex()==true) ? 0 : 1;

        new AlertDialog.Builder(this).setTitle("修改性别").setSingleChoiceItems(R.array.sex, choice,choiceListener)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int choiceWhich=choiceListener.getWhich();
                        boolean sexBoole=  choiceWhich==0 ? true:false;
                        UserBean user = new UserBean();
                        user.setSex(sexBoole);
                        sex.setText(user.getSexStr());
                        UserUtil.updateUser(EditMyInfoActivity.this, user);
                    }
                }).setNegativeButton("取消", null).show();
    }

    public void editAge(View view) {
        final EditText temp = new EditText(this);
        temp.setInputType(InputType.TYPE_CLASS_NUMBER);
        temp.setText(age.getText());
        new AlertDialog.Builder(this).setTitle("修改年龄").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                temp).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (temp.getText().toString().length() > 1)
                    age.setText(temp.getText());
                UserBean user = new UserBean();
                user.setAge(Integer.valueOf(temp.getText().toString()));
                UserUtil.updateUser(EditMyInfoActivity.this, user);
            }
        }).setNegativeButton("取消", null).show();
    }

    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }

    public void changePassword(View view){


        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View changePasswordView=layoutInflater.inflate(R.layout.change_password,null);

        final EditText oldPassword = (EditText) changePasswordView.findViewById(R.id.oldPassword);
        final EditText newPassword = (EditText) changePasswordView.findViewById(R.id.newPassword);
        final EditText confirmPassword = (EditText) changePasswordView.findViewById(R.id.confirmPassword);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("修改密码");
        builder.setView(changePasswordView);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(oldPassword.getText()==null || newPassword.getText()==null ||
                        confirmPassword.getText()==null)
                    return;
                if(newPassword.getText().toString().length()<6 ||
                        !newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                    Log.d("hwq","newPassword.getText().toString():"+",confirmPassword.getText():"
                            +confirmPassword.getText().toString());
                    Toast.makeText(EditMyInfoActivity.this,"密码长度太短或两次输入密码不一致，请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }
                UserUtil.updateCurrentUserPassword(EditMyInfoActivity.this,oldPassword.getText().toString()
                ,newPassword.getText().toString());
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }
}
