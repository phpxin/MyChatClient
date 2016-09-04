package com.lx.chat.listeners;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lx.chat.mychatclient.ChatNewActivity;
import com.lx.chat.mychatclient.R ; //R文件在activity相关包中

import com.lx.chat.mychatclient.ChatActivity;

/**
 * Created by Administrator on 2016/9/4.
 */
public class UserListClickListener implements View.OnClickListener {


    private Context context;
    private int fid ;

    public UserListClickListener(Context _context, int _fid){

        this.context = _context ;
        this.fid = _fid ;
    }

    @Override
    public void onClick(View v) {
        Intent showDemoPage=new Intent(); //使用意图对象切换窗口
        showDemoPage.setClass(context, ChatNewActivity.class); //参数1是一个context对象，参数2是窗体类
        showDemoPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle mainBundle=new Bundle(); //设置bundle对象，传递数据
        mainBundle.putString("fid", fid+"");
        showDemoPage.putExtras(mainBundle);

        context.startActivity(showDemoPage); //切换窗体
    }
}
