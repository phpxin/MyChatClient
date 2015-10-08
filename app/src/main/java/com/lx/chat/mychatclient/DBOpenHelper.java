package com.lx.chat.mychatclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/10/8.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context) {
        super(context, "my.db", null, 1);
        //数据库文件名my.db, 游标null使用系统游标, 版本 1 ；必须有版本，方便以后数据库升级
        //DBOpenHelper(Context context, String name, CursorFactory factory, int version)
    }

    //数据库第一次创建时调用
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table msg(id integer primary key autoincrement, uid integer, fid integer, content text, type integer, addtime integer)");
    }

    //数据库版本号发生变更时调用
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        //db.execSQL("alter table test add address varchar(32) null");
    }

}
