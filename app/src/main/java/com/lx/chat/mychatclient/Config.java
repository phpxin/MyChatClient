package com.lx.chat.mychatclient;

import java.io.BufferedOutputStream;
import java.net.Socket;

/**
 * 比较完整的阻塞式Socket客户端实例
 * @author Administrator
 *
 */
public class Config {
	public static UserBean my = null;
	public static Socket client = null;
	public static BufferedOutputStream clientSend = null;
	public static String ServerAddr = "";
	public static ReadData rdThread = null;

	
	static {
		
		my = new UserBean();
	}	//一个类可以使用不包含在任何方法体中的静态代码块，当类被载入时，静态代码块被执行，且只被执行一次，静态块常用来执行类属性的初始化。
}
