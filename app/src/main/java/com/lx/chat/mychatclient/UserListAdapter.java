package com.lx.chat.mychatclient;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2016/3/23.
 */
public class UserListAdapter extends BaseAdapter {

    private List<UserBean> userlist;
    private int res;
    private LayoutInflater inflater ;//布局填充器

    public UserListAdapter(Context context, List<UserBean> _userlist, int _res)
    {
        this.userlist = _userlist ;
        this.res = _res ;
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

        UserBean user = this.userlist.get(i) ;
        Bitmap userHeaderImageRs = getHttpImg(user.getAvatar()) ;

        userHeaderView.setImageBitmap(userHeaderImageRs);
        userNameView.setText(user.getNickname());

        return convertView;
    }

    private Bitmap getHttpImg(String Url)
    {
        URL httpimageUrl = null ;
        Bitmap img = null;

        try {
            httpimageUrl = new URL(Url);

            HttpURLConnection huc = (HttpURLConnection) httpimageUrl.openConnection();
            huc.setConnectTimeout(6000);
            huc.setDoInput(true);
            InputStream is = huc.getInputStream();

            img = BitmapFactory.decodeStream(is);

            is.close();

        }catch(Exception e){
            Log.i("cchat", "getHttpImg " + e.getMessage());
        }


        return img;
    }
}
