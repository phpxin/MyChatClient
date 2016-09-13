package com.lx.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lx.chat.mychatclient.AsyncTaskImageLoad;
import com.lx.chat.mychatclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MsgListAdapter extends BaseAdapter {


    //需要的成员
    private ArrayList<HashMap<String, String>> msgs; //要绑定的数据
    private int resource; //绑定条目的界面
    private LayoutInflater inflater ;//布局填充器
    private String uri ;
    HashMap<Integer, View> IMap = new HashMap<Integer, View>();

    //构造方法获取数据和界面
    public MsgListAdapter(Context context, ArrayList<HashMap<String, String>> msgs, int resource){
        this.msgs=msgs;
        this.resource=resource;
        //向系统申请布局填充器
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMsgs(ArrayList<HashMap<String, String>> msgs)
    {
        this.msgs=msgs;
    }



    //实现接口 获取数据总数
    public int getCount() {
        return msgs.size();
    }

    //实现接口 获取指定条目
    public Object getItem(int position) {
        return msgs.get(position);
    }

    //实现接口 获取指定条目id
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        TextView date ;
        TextView right ;
        ImageView userHeaderView ;
    }

    //最重要的接口 获取listView界面
    public View getView(final int position, View convertView, ViewGroup parent) {


        Map<String, String> _item = this.msgs.get(position);
        String pos = _item.get("position");

        ViewHolder holder = new ViewHolder();

        if(convertView==null){

            //resource 指定使用哪个资源文件(item.xml)生成view对象
            //设置填充对象
            if (pos == "right")
                convertView = inflater.inflate(R.layout.item2, null);//null 代表没有根元素
            else
                convertView = inflater.inflate(R.layout.item, null);//null 代表没有根元素


            holder.date = (TextView)convertView.findViewById(R.id.send_date);
            holder.right = (TextView)convertView.findViewById(R.id.msgcontent);
            holder.userHeaderView = (ImageView) convertView.findViewById(R.id.avatar) ;

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.date.setText(_item.get("date"));
        holder.right.setText(_item.get("content"));

        //加载图片资源
        uri = _item.get("avatar");
        LoadImage(holder.userHeaderView, uri);

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
