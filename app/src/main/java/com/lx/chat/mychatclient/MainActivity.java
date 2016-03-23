package com.lx.chat.mychatclient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    public EditText ipaddr;
    public EditText username;
    public EditText password;
    public Button loginbtn;

    public String remoteIp = "";
    public int remotePort = 10001;
    
    private Bundle _b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setTitle("登陆");
		
		Config.rdThread = new ReadData();
		Config.rdThread.setHandler(this.myHandler);
		Config.rdThread.setDoit(true);
		
        ipaddr = (EditText)findViewById(R.id.ipaddr);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        loginbtn = (Button)findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Thread(sockThread)).start();
            }
        });
	}
	
	Runnable sockThread=new Runnable(){

        @Override
        public void run() {
            // 在线程开启socket 不阻塞UI
            // 构建socket client对象
            Log.i("lixin","1");

            int _rdata_l = 0;
            short _protocol = 0;
            int _flag = 0;
            
            remoteIp = ipaddr.getText().toString() ;
            
            Config.ServerAddr = remoteIp;

            try {
                Config.client = new Socket(InetAddress.getByName(remoteIp), remotePort);
                
                
                Config.clientSend = new BufferedOutputStream(Config.client.getOutputStream());

                //登陆
                String _username = username.getText().toString();
                String _password = password.getText().toString();

                char username[] = new char[200];	//由于c语言字符串需要定长，这里必须指定长度
                for (int i = 0; i < _username.length(); i++) {
                    username[i] = _username.charAt(i);
                }

                char password[] = new char[200];
                for (int i = 0; i < _password.length(); i++) {
                    password[i] = _password.charAt(i);
                }

                ByteArrayOutputStream clientPackageStream = new ByteArrayOutputStream(402);
                DataOutputStream _clientPackageStream = new DataOutputStream(clientPackageStream);
                int package_len = 4 + 2 + 200 + 200 ;
                _clientPackageStream.writeInt(package_len);
                _clientPackageStream.writeShort(Protocol.login);
                _clientPackageStream.write((new String(username, 0, username.length)).getBytes());
                _clientPackageStream.write((new String(password, 0, username.length)).getBytes());

                byte[] clientPackageBuf = clientPackageStream.toByteArray();	//字节数据

                clientPackageStream.close();

                Config.clientSend.write(clientPackageBuf, 0, clientPackageBuf.length);
                Config.clientSend.flush();

                (new Thread(Config.rdThread)).start(); // 开启读取线程
                
            } catch (IOException e) {
                Log.i("lixin",e.getMessage());
            } catch(Exception e){
            	 Log.i("lixin",e.getMessage());
            }
        }

    };
    
    
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			_b = msg.getData(); // bundle这东西就是个包，数据打包用它传递过来

			switch (msg.what) {

			case HandleMess.MESS_LOGINOK :
				
				int flag = _b.getInt("flag") ;
				
				if(flag == 1){
	                //使用意图对象切换窗口
	                Intent showDemoPage=new Intent();
	                //showDemoPage.setClass(getApplicationContext(), UlistActivity.class);
                    showDemoPage.setClass(getApplicationContext(), UsersActivity.class);
	                //切换窗体
	                startActivity(showDemoPage);
				}else{
					Toast.makeText(getApplicationContext(), "登陆失败：用户名/密码错误", Toast.LENGTH_SHORT).show();
				}

				break;
			}
			super.handleMessage(msg);
		}
	};
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
