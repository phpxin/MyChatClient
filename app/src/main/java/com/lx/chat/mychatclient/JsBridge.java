package com.lx.chat.mychatclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JsBridge {
	private Context context ;
	private Handler myHandler;
	private String jsonData ; // 是否完成加载 : done/running : 已完成/加载中
	private int friendId ; // 聊天目标uid
	
	public JsBridge(Context c, Handler h) 
	{
		context = c ;
		myHandler = h ;
		
		jsonData = "";
	}
	
	public void setFriendId(int fid)
	{
		friendId = fid;
	}
	
	@JavascriptInterface
	public void alertMsg(String msg)
	{
		Toast.makeText(context, "tip : " + msg, Toast.LENGTH_SHORT).show();
	}
	
	@JavascriptInterface
	public void gotoChat(String fid)
	{
        Intent showDemoPage=new Intent(); //使用意图对象切换窗口
        showDemoPage.setClass(context, ChatActivity.class); //参数1是一个context对象，参数2是窗体类
        showDemoPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        Bundle mainBundle=new Bundle(); //设置bundle对象，传递数据
        mainBundle.putString("fid", fid);
        showDemoPage.putExtras(mainBundle);
        
        context.startActivity(showDemoPage); //切换窗体
	}
	
	public void setJsonData(String d)
	{
		jsonData = d;
	}
	
	@JavascriptInterface
	public String getJsonData()
	{
		return jsonData;
	}
	
	@JavascriptInterface
	public void sendMsg(String msg)
	{
		int _toUid = friendId;
		String _content = msg;

		WriteData _wd = new WriteData(_toUid, _content, this.myHandler);
		
		(new Thread(_wd)).start();
	}
}
