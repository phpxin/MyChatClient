package com.lx.chat.mychatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	
	private WebView htdoc;
	private Bundle _b;
	private JsBridge jb;
	
	private int fid;
	private String fname;
	private String favatar;
	
	private String userInfoApi = "http://" + Config.ServerAddr + ":8080/index.php?module=user&action=getInfo" ;
	
	@Override
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface"})
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		setTitle("聊天");
		
		//datas 
		Bundle bun=this.getIntent().getExtras(); //通过进来的意图对象获取数据
		
		try{
			fid = Integer.parseInt(bun.getString("fid"));
			userInfoApi = userInfoApi + "&uid=" + fid;
		}catch(NumberFormatException _e){
			Toast.makeText(getApplicationContext(), "fid is error", Toast.LENGTH_SHORT).show();
			
            Intent showDemoPage=new Intent();
            showDemoPage.setClass(getApplicationContext(), UlistActivity.class);
            startActivity(showDemoPage);
		}
		
		// load webview
		htdoc = (WebView) findViewById(R.id.htdoc);
		
		WebSettings webSettings = htdoc.getSettings();
		webSettings.setJavaScriptEnabled(true); //设置浏览器支持js，必须在设置cookie之前设置，否则会使cookie失效
		
		jb = new JsBridge(getApplicationContext(), this.myHandler) ;
		jb.setFriendId(fid);
		htdoc.addJavascriptInterface(jb, "jsbridge");
		
		htdoc.loadUrl("file:///android_asset/chat.html");

		selectFriendCacheMsg(); //必须在url获取完成后调用
		
		(new Thread(sockHttpConnection)).start();
		
		// webview end
		
		Config.rdThread.setDoit(true);
		Config.rdThread.setHandler(this.myHandler);
		
	}

	/**
	 * 获取数据库缓存消息,必须在url获取完成后调用
	 */
	private void selectFriendCacheMsg()
	{
		DBA dba = new DBA(getApplicationContext()) ;
		ArrayList<MsgBean> mbs = dba.getMsgListByFid(fid) ;

		Log.i("lixin", "ddddddd  sqlite is running") ;

		if (mbs.isEmpty())
			return ;

		String html = "" ;

		for (MsgBean _mb : mbs)
		{
			html += "<div class=\"left\"><img src=\""+favatar+"\" class=\"avatar fl\" /><span class=\"msg fl ml-10\">"+_mb.content+"</span><div class=\"clean\"></div></div>" ;

		}

		Log.i("lixin", html) ;

		htdoc.loadUrl("javascript:updateContent('" + html + "')");
	}
	
	Runnable sockHttpConnection = new Runnable() {

		@Override
		public void run() {

			URL url = null;

			try {
				// 创建url对象
				url = new URL(userInfoApi);

			} catch (MalformedURLException e1) {
				Log.i("lixin", e1.getMessage());

			}

			// 通过http将用户信息提交
			HttpURLConnection hUrlConn;
			try {
				// 通过url对象获取并初始化http连接对象
				hUrlConn = (HttpURLConnection) url.openConnection();

				// 设置开启post方法
				hUrlConn.setRequestMethod("GET");

				// 打开http连接
				hUrlConn.connect();

				// 接收返回值
				BufferedReader ins = new BufferedReader(new InputStreamReader(
						hUrlConn.getInputStream()));

				String content = null;
				String line = "";

				while ((line = ins.readLine()) != null) {
					content += line;
				}
				// 使用自定义函数，去除BOM头
				content=ChatUtil.JSONTokener(content);
				Log.i("lixin",content);
				
				// 解析json
				JSONTokener jsonParser = new JSONTokener(content);
				JSONObject jarr = (JSONObject) jsonParser.nextValue();
				
				int ret = jarr.getInt("ret");
				
				if(ret == 1){
					JSONObject data = (JSONObject) jarr.getJSONObject("data").getJSONObject("info");
					fname = data.getString("name");
					favatar = data.getString("avatar");
					
				}else{
					//failed
					Log.i("lixin", "get user Info failed: parse json failed");
				}

			} catch (MalformedURLException e) {
				//e.printStackTrace();
				Log.i("lixin", e.getMessage());

			} catch (ProtocolException e) {
				Log.i("lixin", e.getMessage());

			} catch (IOException e) {
				Log.i("lixin", e.getMessage());

			} catch (JSONException e) {
				Log.i("lixin", e.getMessage());
			}

		}

	};	

	// 主线程操作句柄
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			_b = msg.getData(); // bundle这东西就是个包，数据打包用它传递过来

			switch (msg.what) {

			case HandleMess.MESS_RECVMSG :
				String sender = _b.getString("uid");
				
				if(sender.equals(Config.my.getUid()+"")){
					String html = "<div class=\"right\"><img src=\""+Config.my.getAvatar()+"\" class=\"avatar fr\" /><span class=\"msg fr mr-10\">"+_b.getString("content")+"</span><div class=\"clean\"></div></div>" ;
					htdoc.loadUrl("javascript:updateContent('" + html + "')");
				}else if(sender.equals(fid+"")){
					String html = "<div class=\"left\"><img src=\""+favatar+"\" class=\"avatar fl\" /><span class=\"msg fl ml-10\">"+_b.getString("content")+"</span><div class=\"clean\"></div></div>" ;
					htdoc.loadUrl("javascript:updateContent('" + html + "')");
				}else{
					//非当前聊天用户，执行其他动作，提示用户或保存数据库
					//Toast.makeText(getApplicationContext(), "其他用户发来消息，uid "+sender, Toast.LENGTH_SHORT).show();

					DBA dba = new DBA(getApplicationContext()) ;
					MsgBean _msg = new MsgBean();
					_msg.uid = Integer.parseInt(sender);
					_msg.content = _b.getString("content");
					_msg.fid = Config.my.uid ;
					_msg.addtime = 0 ;
					_msg.type = 1 ;

					dba.insertMsg(_msg);
				}

				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
