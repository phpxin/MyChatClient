package com.lx.chat.mychatclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/10/14 0014.
 */
public class DBA {
    private Context _context;
    private DBOpenHelper dbo ;

    public DBA(Context c){
        _context = c ;
        dbo = new DBOpenHelper( _context ) ;
    }

    public boolean insertMsg(MsgBean msg) {

        SQLiteDatabase db = dbo.getWritableDatabase() ;

        String sql = "insert into msg(uid, fid, content, type, addtime) values("+msg.uid+","+msg.fid+",'"+msg.content+"',"+msg.type+","+msg.addtime+")";

        db.execSQL(sql);

        return true;
    }
}
