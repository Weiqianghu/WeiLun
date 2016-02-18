package com.weilun.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.weilun.weilun.R;

public class LoginAndRegisterActivity extends AppCompatActivity {

    private int count=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
    }


    public void gotoLogin(View view){
        Intent intent=new Intent(LoginAndRegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        LoginAndRegisterActivity.this.finish();
    }

    public void gotoRegister(View view){
        Intent intent=new Intent(LoginAndRegisterActivity.this,RegisterActivity.class);
        startActivity(intent);
        LoginAndRegisterActivity.this.finish();
    }

    public void exit(View v){
        if(count<2){
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            count++;
            new Thread(){
                public void run(){
                    try{
                        sleep(3000);
                        count=1;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else{
            this.finish();
        }
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit(null);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
