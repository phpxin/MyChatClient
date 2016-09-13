package com.lx.chat.mychatclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lx.chat.listeners.UserListClickListener;

import java.util.List;


/**
 * Created by QiCheng on 2016/3/23.
 * @author lixin65535@126.com
 */
public class UserListAdapter extends BaseAdapter {

    private List<UserBean> userlist;
    private int res;
    private LayoutInflater inflater ;//布局填充器
    private String uri ;
    private Context context;



    public UserListAdapter(Context _context, List<UserBean> _userlist, int _res)
    {
        this.userlist = _userlist ;
        this.res = _res ;
        this.context = _context;
        //向系统申请布局填充器
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.userlist.size();
    }

    @Override
    public Object getItem(int i) {
        return this.userlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.userlist.get(i).getUid();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if(convertView==null){
            //resource 指定使用哪个资源文件(item.xml)生成view对象
            //设置填充对象
            convertView= inflater.inflate(this.res, null);//null 代表没有根元素
        }

        //使用findViewById方法 获取item.xml每个条目的 textView对象
        ImageView userHeaderView = (ImageView) convertView.findViewById(R.id.header) ;
        TextView userNameView = (TextView) convertView.findViewById(R.id.uname) ;
        TextView userIntroView = (TextView) convertView.findViewById(R.id.intro) ;

        UserBean user = this.userlist.get(i) ;

        userNameView.setText(user.getNickname());
        userIntroView.setText("这是用户签名");


        //加载图片资源
        uri = user.getAvatar();
        LoadImage(userHeaderView, uri);

        convertView.setOnClickListener(new UserListClickListener(context, user.getUid()));

        return convertView;
    }

    private void LoadImage(ImageView img, String path)
    {
        //异步加载图片资源
        AsyncTaskImageLoad async=new AsyncTaskImageLoad(img);
        //执行异步加载，并把图片的路径传送过去
        async.execute(path);
    }


}
