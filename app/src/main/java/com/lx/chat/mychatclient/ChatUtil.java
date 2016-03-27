package com.lx.chat.mychatclient;


public class ChatUtil {
	
	/**
	 * 去除BOM头
	 * @param in
	 * @return
	 */
    public static String JSONTokener(String in) {  
 	
    	if(in.startsWith("null") || in.startsWith("NULL"))
    	{
    		in = in.substring(4);
    	}
    	
        return in;
   	}

}
