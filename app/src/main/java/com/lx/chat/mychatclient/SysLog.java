package com.lx.chat.mychatclient;

import android.util.Log;


/**
 * Created by QiCheng on 2016/3/27 0027.
 *
 * @author lixin65535@126.com
 */
public class SysLog {

    public static final String LOG_TAG = "cchat" ;

    public static final int LOG_INFO = 1 ;
    public static final int LOG_WARNNING = 2 ;
    public static final int LOG_ERROR = 3 ;


    public static void log(String msg, int level)
    {
        Log.i(SysLog.LOG_TAG, "log : " + msg);
    }

    public static void log(String msg){
        SysLog.log(msg, SysLog.LOG_INFO);
    }


}
