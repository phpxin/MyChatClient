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

        for (int i=1; i<=12; i++) {

            UserBean _b = new UserBean();

            int _i = (i%6) + 1 ;

            _b.setUid(i);
            _b.setAccount("lalalalal");
            _b.setAvatar("http://123.56.255.62:8080/uploads/"+_i+".jpg");
            _b.setNickname("lixin "+i);

            userlist.add(_b);

        }

        UserListAdapter ulAdapter = new UserListAdapter(getApplicationContext(), userlist, R.layout.userlist) ;

        ListView lv = (ListView)findViewById(R.id.lv) ;

        lv.setAdapter(ulAdapter);
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
