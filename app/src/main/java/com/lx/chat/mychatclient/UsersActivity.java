package com.lx.chat.mychatclient;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private String userListApi = "http://" + Config.ServerAddr + ":8080/index.php?module=user&action=getList" ;
    private Bundle _b;
    List<UserBean> userlist;
    ListView lv;
    UserListAdapter ulAdapter;

    private SwipeRefreshLayout listRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        userlist = new ArrayList<UserBean>();

        ulAdapter = new UserListAdapter(getApplicationContext(), userlist, R.layout.userlist) ;



        lv = (ListView)findViewById(R.id.lv) ;

        lv.setAdapter(ulAdapter);


        listRefresh = (SwipeRefreshLayout)findViewById(R.id.userListRf);

        listRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Thread(sockHttpConnection)).start();
            }
        });


        (new Thread(sockHttpConnection)).start();

        Config.rdThread.setDoit(true);
        Config.rdThread.setHandler(this.myHandler);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //当用户返回该界面时触发

        Config.rdThread.setDoit(true);
        Config.rdThread.setHandler(this.myHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    Runnable sockHttpConnection = new Runnable() {

        @Override
        public void run() {

            URL url = null;

            int uid = Config.my.getUid() ;

            try {
                // 创建url对象
                url = new URL(userListApi);

            } catch (MalformedURLException e1) {
                Log.i("lixin", e1.getMessage());

            }

            OutputStream ots = null;
            BufferedReader ins = null;

            // 通过http将用户信息提交
            HttpURLConnection hUrlConn = null;
            try {
                // 通过url对象获取并初始化http连接对象
                hUrlConn = (HttpURLConnection) url.openConnection();

                // 设置开启post方法
                hUrlConn.setRequestMethod("POST");

                // 打开http连接
                hUrlConn.connect();

                //req = new BufferedWriter(new OutputStreamWriter(hUrlConn.getOutputStream())) ;
                //req.write("uid="+uid);
                //发送post请求，post以键值对夹&符保存在正文中发送
                ots=hUrlConn.getOutputStream();
                String queryString = "uid="+uid;
                ots.write(queryString.getBytes());


                // 接收返回值
                ins = new BufferedReader(new InputStreamReader(
                        hUrlConn.getInputStream()));

                String content = "";
                String line = "";

                while ((line = ins.readLine()) != null) {
                    content += line;
                }

                //通过句柄（就是一个管子）更新列表数据
                Message mess=new Message();
                mess.what = HandleMess.MESS_UPUSERLIST;
                Bundle _bb = new Bundle();
                _bb.putString("content", content);
                mess.setData(_bb);
                UsersActivity.this.myHandler.sendMessage(mess);

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (ProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }finally {
                try {
                    if (ins != null) {
                        ins.close();
                    }
                    if (ots != null) {
                        ots.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
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
                case HandleMess.MESS_UPUSERLIST:

                    try{
                        // 解析json
                        String content = _b.getString("content");
                        JSONTokener jsonParser = new JSONTokener(content);

                        JSONObject jsonRoot = (JSONObject) jsonParser.nextValue();

                        int code = jsonRoot.getInt("ret") ;

                        if (code!=1){
                            Toast.makeText(getApplicationContext(), "request error code = "+code, Toast.LENGTH_SHORT).show();
                        }else {

                            JSONObject data = (JSONObject) jsonRoot.getJSONObject("data");

                            JSONArray jarr = (JSONArray) data.getJSONArray("userlist");

                            Object userItem;
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject _li = jarr.getJSONObject(i);
                                int uid = _li.getInt("id");
                                String account = _li.getString("name");
                                String avatar = _li.getString("avatar");


                                UserBean _b = new UserBean();

                                _b.setUid(uid);
                                _b.setAccount(account);
                                _b.setAvatar(avatar);
                                _b.setNickname(account);

                                userlist.add(_b);

                            }

                            ulAdapter.notifyDataSetChanged();


                        }

                    }catch (JSONException e){

                        Log.i("lixin", e.getMessage()) ;
                    }finally {
                        listRefresh.setRefreshing(false); // 停止刷新
                    }



                    break;

                case HandleMess.MESS_RECVMSG :
                    String sender = _b.getString("uid");

                    Log.i("lixin", "save db") ;

                    DBA dba = new DBA(getApplicationContext()) ;
                    MsgBean _msg = new MsgBean();
                    _msg.uid = Integer.parseInt(sender);
                    _msg.content = _b.getString("content");
                    _msg.fid = Config.my.uid ;
                    _msg.addtime = 0 ;
                    _msg.type = 1 ;

                    dba.insertMsg(_msg);

                    break;
            }
            super.handleMessage(msg);
        }
    };

}
