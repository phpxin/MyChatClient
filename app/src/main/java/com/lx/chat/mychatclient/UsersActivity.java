package com.lx.chat.mychatclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        List<UserBean> userlist = new ArrayList<UserBean>();

        UserBean _b = new UserBean();
        _b.setUid(1);
        _b.setAccount("lalalalal");
        _b.setAvatar("http://192.168.3.17:8080/uploads/5.jpg?1458620685");
        _b.setNickname("lixin");

        userlist.add(_b) ;

        UserListAdapter ulAdapter = new UserListAdapter(getApplicationContext(), userlist, R.layout.userlist) ;

        ListView lv = (ListView)findViewById(R.id.lv) ;

        lv.setAdapter(ulAdapter);


        /*
        HashMap<String,String> ulist = new HashMap<String, String>() ;
        ulist.put("uname", "lx") ;
        ulist.put("header", "http://192.168.3.17:8080/uploads/5.jpg?1458620685") ;

        ArrayList<HashMap<String,String>> ulist_l = new ArrayList<HashMap<String,String>>();
        ulist_l.add(ulist ) ;

        Adapter ad = new SimpleAdapter(getApplicationContext(), ulist_l, R.layout.userlist,
                new String[]{"uname", "header"}, new int[]{R.id.uname, R.id.header}) ;

        ListView lv = (ListView)findViewById(R.id.lv) ;

        lv.setAdapter((ListAdapter) ad);

        */

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
}
