package com.lx.chat.mychatclient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class WriteData implements Runnable{
	private int toUid = 0;
	private String content = "";
	
	private Handler myHandler;
	
	WriteData(int _uid, String _content, Handler _h){
		this.toUid = _uid;
		this.content = _content;
		
		this.myHandler = _h;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.i("lixin","写入线程开启");
		
		if(this.toUid == 0){
			Log.i("lixin", "接收用户UID不能为0!");
			return ;
		}else{
			Log.i("lixin", "将要发送数据：touid is "+this.toUid+", content is "+this.content);
		}
		
		ByteArrayOutputStream sockWriteBuff = new ByteArrayOutputStream();
		DataOutputStream sockeWriter = new DataOutputStream(sockWriteBuff);
		
		try{
			
			short c_protocol = Protocol.sendmsg;
			int fromUid = Config.my.getUid();
			int toUid = this.toUid;
			String content = this.content;
			
			int len = content.getBytes().length ; //content.length();
			
			int data_len = 4 + 2 + 4 + 4 + 4 + len ;
			
			//数据长度
			sockeWriter.writeInt(data_len);
			//协议
			sockeWriter.writeShort(c_protocol);
			//头
			sockeWriter.writeInt(fromUid);
			sockeWriter.writeInt(toUid);
			sockeWriter.writeInt(len);
			//正文
			sockeWriter.write(content.getBytes());
			
			byte[] r_data = sockWriteBuff.toByteArray();
			
			Config.clientSend.write(r_data, 0, r_data.length);
			Config.clientSend.flush();
			
			//通过句柄（就是一个管子）更新列表数据
			Message mess=new Message();
			mess.what=1;
			Bundle _bb = new Bundle();
			_bb.putString("content", content);
			_bb.putString("uid", fromUid+"");
			mess.setData(_bb);
			this.myHandler.sendMessage(mess);

			
		}catch(Exception e){
			Log.i("lixin", e.getMessage());
		}finally{
			try {
				sockeWriter.close();	//关闭包装流，即可同时关闭内部流
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
