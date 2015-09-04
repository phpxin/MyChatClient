package com.lx.chat.mychatclient;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ReadData implements Runnable{
	private Handler handler ; // 保存当前对象上下文，不同的界面处理方法不同
	private boolean doit; // 是否处理 read data 动作
	
    public boolean isDoit() {
		return doit;
	}

	public void setDoit(boolean doit) {
		this.doit = doit;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	private class _Result{
		public Message mess;
		public boolean hasMess;
		
		public _Result() {
			mess = new Message();
			hasMess = false;
		}
	}
	
	public _Result stateMachine(short protocol, DataInputStream dis)
	{
		_Result result = new _Result();
		
		try{
			
			if(protocol==Protocol.sendmsg){
				//接收消息
				int fromUid = dis.readInt();
				int toUid = dis.readInt();
				int dataLen = dis.readInt();
				
				if(dataLen>0){
					byte[] content = new byte[dataLen];
					dis.read(content, 0, dataLen);
					
					String readContent = new String(content, "UTF-8");
					
					String _msg = "收到数据："+fromUid+"发送给"+toUid+":"+readContent;
					Log.i("lixin", _msg);
					
					//通过句柄（就是一个管子）更新列表数据
					result.mess.what = HandleMess.MESS_RECVMSG;
					Bundle _bb = new Bundle();
					_bb.putString("content", readContent);
					_bb.putString("uid", fromUid+"");
					result.mess.setData(_bb);
					result.hasMess = true;
					
				}else{
					Log.i("lixin", "没有收到数据");
				}
			}else if(protocol==Protocol.login_r){
				int _flag = dis.readInt();
				result.mess.what = HandleMess.MESS_LOGINOK;
				Bundle _bb = new Bundle();
				
				if(_flag!=1){
					Log.i("lixin", "登陆失败");
					_bb.putInt("flag", _flag);
				}else{
					int _my_uid = dis.readInt();
					Config.my.setUid(_my_uid);
					
					byte[] byteBuff = new byte[200];
					
					dis.read(byteBuff, 0, 200);
					String _username = new String(byteBuff);
					Config.my.setAccount(_username.substring(0, _username.indexOf('\0')));
					
					dis.read(byteBuff, 0, 200);
					String _password = new String(byteBuff);
					String user_pwd = _password.substring(0, _password.indexOf('\0'));
					
					dis.read(byteBuff, 0, 200);
					String _nickname = new String(byteBuff);
					Config.my.setNickname(_nickname.substring(0, _nickname.indexOf('\0')));
					
					dis.read(byteBuff, 0, 200);
					String _avatar = new String(byteBuff);
					Config.my.setAvatar(_avatar.substring(0, _avatar.indexOf('\0')));
					
					Log.i("lixin", "UID是"+Config.my.getUid());
					Log.i("lixin", "姓名是"+Config.my.getNickname());
					Log.i("lixin", "账号是"+Config.my.getAccount());
					Log.i("lixin", "头像是"+Config.my.getAvatar());
					
					_bb.putInt("flag", _flag);
				}
				
				result.mess.setData(_bb);
				result.hasMess = true;
				
			}else{
				int flag = dis.readInt();
				
				Log.i("lixin", "服务器返回  " + flag);
				
				result.hasMess = false;
			}	
		}catch(EOFException e){
			Log.i("lixin", "EOFException:"+e.getMessage());
		}catch(IOException e){
			Log.i("lixin", "IOException:"+e.getMessage());
		}catch(Exception e){
			Log.i("lixin", "Exception:"+e.getMessage());
		}
		
		return result;
	}

	@Override
    public void run() {
        // 在线程开启socket 不阻塞UI
        // 构建socket client对象
        Log.i("lixin","读取线程开启");

		try{

			DataInputStream dis = null;
			
			while(true){
				dis = new DataInputStream(Config.client.getInputStream());
				
				int data_len = dis.readInt(); //数据长度
				if(data_len <= 0)	continue;
				
				short protocol = dis.readShort();	//读取协议类型
				
				_Result result = stateMachine(protocol, dis);
    			
    			if(this.doit && result.hasMess)
    			{
    				handler.sendMessage(result.mess);
    			}
    			
			}
		
		}catch(EOFException e){
			Log.i("lixin", "EOFException:"+e.getMessage());
		}catch(IOException e){
			Log.i("lixin", "IOException:"+e.getMessage());
		}catch(Exception e){
			Log.i("lixin", "Exception:"+e.getMessage());
		}
        
        
    }
}
