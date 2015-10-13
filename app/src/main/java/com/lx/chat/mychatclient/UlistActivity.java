package com.lx.chat.mychatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class UlistActivity extends Activity {
	
	private WebView htdoc;
	private String userListApi = "http://" + Config.ServerAddr + ":8080/index.php?module=user&action=getList" ;
	private Bundle _b;
	private JsBridge jb;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ulist);
		
		setTitle("好友列表");
		
		// load webview
		htdoc = (WebView) findViewById(R.id.htdoc);
		
		WebSettings webSettings = htdoc.getSettings();
		webSettings.setJavaScriptEnabled(true); //设置浏览器支持js，必须在设置cookie之前设置，否则会使cookie失效
		
		jb = new JsBridge(getApplicationContext(), this.myHandler);
		htdoc.addJavascriptInterface(jb, "jsbridge");
		
		htdoc.loadUrl("file:///android_asset/userlist.html");
		
		(new Thread(sockHttpConnection)).start();

		Config.rdThread.setDoit(true);
		Config.rdThread.setHandler(this.myHandler);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//当用户返回该界面时触发

		Config.rdThread.setDoit(true);
		Config.rdThread.setHandler(this.myHandler);
	}

	Runnable sockHttpConnection = new Runnable() {

		@Override
		public void run() {

			URL url = null;

			try {
				// 创建url对象
				url = new URL(userListApi);

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
				
				//通过句柄（就是一个管子）更新列表数据
				Message mess=new Message();
				mess.what = HandleMess.MESS_UPUSERLIST;
				Bundle _bb = new Bundle();
				_bb.putString("content", content);
				mess.setData(_bb);
				UlistActivity.this.myHandler.sendMessage(mess);

			} catch (MalformedURLException e) {
				e.printStackTrace();

			} catch (ProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

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
			case HandleMess.MESS_UPUSERLIST:
				String html = _b.getString("content");
				jb.setJsonData(html);
				htdoc.loadUrl("javascript:updateContent('" + html + "')");
				break;

			case HandleMess.MESS_RECVMSG :
				String sender = _b.getString("uid");

				Log.i("lixin", "save db") ;

				DBA dba = new DBA(getApplicationContext()) ;
				MsgBean _msg = new MsgBean();
				_msg.uid = Integer.parseInt(sender);
				_msg.content = _b.getString("content");
				_msg.fid = Config.my.uid ;
				_msg.addtime = 0 ;
				_msg.type = 1 ;

				dba.insertMsg(_msg);

				break;
			}
			super.handleMessage(msg);
		}
	};

}
