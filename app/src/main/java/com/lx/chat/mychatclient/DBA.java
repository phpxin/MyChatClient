package com.lx.chat.mychatclient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * 获取单个用户缓存消息列表
     */
    public ArrayList<MsgBean> getMsgListByFid( int fid ) {
        SQLiteDatabase db = dbo.getReadableDatabase() ;
        String sql = "select * from msg where uid=" + fid ;
        //Log.i("lixin", sql);

        Cursor cs = db.rawQuery(sql, null) ;
        ArrayList<MsgBean> mbs = new ArrayList<MsgBean>() ;

        while ( cs.moveToNext() )
        {
            MsgBean _mb = new MsgBean() ;
            _mb.id = cs.getInt(cs.getColumnIndex("id")) ;
            _mb.fid = cs.getInt(cs.getColumnIndex("fid")) ;
            _mb.content = cs.getString(cs.getColumnIndex("content")) ;

            mbs.add(_mb) ;
        }

        cs.close();


        db = dbo.getWritableDatabase();
        sql = "delete from msg where uid=" + fid ;
        db.execSQL(sql);

        db.close();
        return mbs;
    }

    public boolean insertMsg(MsgBean msg) {

        SQLiteDatabase db = dbo.getWritableDatabase() ;

        String sql = "insert into msg(uid, fid, content, type, addtime) values("+msg.uid+","+msg.fid+",'"+msg.content+"',"+msg.type+","+msg.addtime+")";

        db.execSQL(sql);

        return true;
    }
}
