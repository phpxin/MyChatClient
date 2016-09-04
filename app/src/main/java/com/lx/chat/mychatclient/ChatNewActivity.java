package com.lx.chat.mychatclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lx.chat.adapters.MsgListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatNewActivity extends AppCompatActivity {

    private Bundle _b;
    ListView lv;
    EditText content;
    Button submit;
    ArrayList<HashMap<String, String>> msgs;
    MsgListAdapter pa;

    private int fid;
    private String fname;
    private String favatar;

    private String userInfoApi = "http://" + Config.ServerAddr + ":8080/index.php?module=user&action=getInfo" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);

        setTitle("聊天");

        //datas
        Bundle bun=this.getIntent().getExtras(); //通过进来的意图对象获取数据

        try{
            fid = Integer.parseInt(bun.getString("fid"));
            userInfoApi = userInfoApi + "&uid=" + fid;
        }catch(NumberFormatException _e){
            Toast.makeText(getApplicationContext(), "fid is error", Toast.LENGTH_SHORT).show();

            Intent showDemoPage=new Intent();
            showDemoPage.setClass(getApplicationContext(), UlistActivity.class);
            startActivity(showDemoPage);
        }


        msgs = new ArrayList<HashMap<String, String>>() ;


        lv = (ListView) findViewById(R.id.list) ;
        lv.setFooterDividersEnabled(false);
        lv.setHeaderDividersEnabled(false);

        pa = new MsgListAdapter(getApplicationContext(), msgs, R.layout.item2);
        lv.setAdapter(pa);


        content = (EditText) findViewById(R.id.editmsg_content);
        submit = (Button) findViewById(R.id.editmsg_btn);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _c = content.getEditableText().toString();

                int _toUid = fid;
                String _content = _c;

                WriteData _wd = new WriteData(_toUid, _content, myHandler);

                (new Thread(_wd)).start();
            }
        });



        (new Thread(sockHttpConnection)).start();

        Config.rdThread.setDoit(true);
        Config.rdThread.setHandler(this.myHandler);
    }

    /**
     * 获取数据库缓存消息,必须在url获取完成后调用
     */
    private void selectFriendCacheMsg()
    {
        DBA dba = new DBA(getApplicationContext()) ;
        ArrayList<MsgBean> mbs = dba.getMsgListByFid(fid) ;

        if (mbs.isEmpty())
            return ;

        for (MsgBean _mb : mbs)
        {

            HashMap<String, String> chatMsg = new HashMap<String, String>();
            chatMsg.put("date", "12:40");
            chatMsg.put("content", _mb.content) ;
            chatMsg.put("avatar", favatar);
            chatMsg.put("position", "left");
            msgs.add(chatMsg);

        }

        pa.notifyDataSetChanged();     //调用适配器的方法，刷新列表
        lv.setSelection(ListView.FOCUS_DOWN);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //当用户返回该界面时触发

        selectFriendCacheMsg(); //必须在url获取完成后调用

        Config.rdThread.setDoit(true);
        Config.rdThread.setHandler(this.myHandler);
    }

    Runnable sockHttpConnection = new Runnable() {

        @Override
        public void run() {

            URL url = null;

            try {
                // 创建url对象
                url = new URL(userInfoApi);

            } catch (MalformedURLException e1) {
                Log.i("lixin", e1.getMessage());

            }

            // 通过http将用户信息提交
            HttpURLConnection hUrlConn;
            try {
                // 通过url对象获取并初始化http连接对象
                hUrlConn = (HttpURLConnection) url.openConnection();

                // 设置开启post方法
                hUrlConn.setRequestMethod("GET");

                // 打开http连接
                hUrlConn.connect();

                // 接收返回值
                BufferedReader ins = new BufferedReader(new InputStreamReader(
                        hUrlConn.getInputStream()));

                String content = null;
                String line = "";

                while ((line = ins.readLine()) != null) {
                    content += line;
                }
                // 使用自定义函数，去除BOM头
                content=ChatUtil.JSONTokener(content);
                Log.i("lixin",content);

                // 解析json
                JSONTokener jsonParser = new JSONTokener(content);
                JSONObject jarr = (JSONObject) jsonParser.nextValue();

                int ret = jarr.getInt("ret");

                if(ret == 1){
                    JSONObject data = (JSONObject) jarr.getJSONObject("data").getJSONObject("info");
                    fname = data.getString("name");
                    favatar = data.getString("avatar");
                    selectFriendCacheMsg(); //必须在url获取完成后调用
                }else{
                    //failed
                    Log.i("lixin", "get user Info failed: parse json failed");
                }

            } catch (MalformedURLException e) {
                //e.printStackTrace();
                Log.i("lixin", e.getMessage());

            } catch (ProtocolException e) {
                Log.i("lixin", e.getMessage());

            } catch (IOException e) {
                Log.i("lixin", e.getMessage());

            } catch (JSONException e) {
                Log.i("lixin", e.getMessage());
            }

        }

    };

    // 主线程操作句柄
    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            _b = msg.getData(); // bundle这东西就是个包，数据打包用它传递过来

            switch (msg.what) {

                case HandleMess.MESS_RECVMSG :
                    String sender = _b.getString("uid");

                    if(sender.equals(Config.my.getUid()+"")){

                        HashMap<String, String> chatMsg = new HashMap<String, String>();
                        chatMsg.put("date", "12:40");
                        chatMsg.put("content", _b.getString("content")) ;
                        chatMsg.put("position", "right");
                        chatMsg.put("avatar", Config.my.avatar);
                        msgs.add(chatMsg);

                        pa.notifyDataSetChanged();     //调用适配器的方法，刷新列表

                        lv.setSelection(ListView.FOCUS_DOWN);
                    }else if(sender.equals(fid+"")){

                        HashMap<String, String> chatMsg = new HashMap<String, String>();
                        chatMsg.put("date", "12:40");
                        chatMsg.put("content", _b.getString("content")) ;
                        chatMsg.put("position", "left");
                        chatMsg.put("avatar", favatar);
                        msgs.add(chatMsg);

                        pa.notifyDataSetChanged();     //调用适配器的方法，刷新列表
                        lv.setSelection(ListView.FOCUS_DOWN);
                    }else{
                        //非当前聊天用户，执行其他动作，提示用户或保存数据库

                        DBA dba = new DBA(getApplicationContext()) ;
                        MsgBean _msg = new MsgBean();
                        _msg.uid = Integer.parseInt(sender);
                        _msg.content = _b.getString("content");
                        _msg.fid = Config.my.uid ;
                        _msg.addtime = 0 ;
                        _msg.type = 1 ;

                        dba.insertMsg(_msg);
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };
}
