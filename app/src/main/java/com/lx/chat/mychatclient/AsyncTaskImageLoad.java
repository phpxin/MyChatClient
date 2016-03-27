package com.lx.chat.mychatclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by QiCheng on 2016/3/27 0027.
 * @author lixin65535@126.com
 */
public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Bitmap> {

    private ImageView Image=null;

    public AsyncTaskImageLoad(ImageView img)
    {
        Image=img;
    }

    //运行在子线程中
    protected Bitmap doInBackground(String... params) {
        try
        {
            URL url=new URL(params[0]);

            //Log.i(Config.LOG_TAG, "doInBackground url " + params[0]);
            SysLog.log("doInBackground url " + params[0]);

            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("POST"); ///  请求图片会导致 405的返回，405 - 用来访问本页面的 HTTP 谓词不被允许（方法不被允许）
            conn.setConnectTimeout(5000);
            if(conn.getResponseCode()==200)
            {
                InputStream input=conn.getInputStream();
                Bitmap map= BitmapFactory.decodeStream(input);
                return map;
            }else{
                //Log.i(Config.LOG_TAG, "getResponseCode " + conn.getResponseCode());
                SysLog.log("getResponseCode " + conn.getResponseCode());
            }
        } catch (Exception e)
        {
            //Log.i(Config.LOG_TAG, "doInBackground url failed " + e.getMessage());
            SysLog.log("doInBackground url failed " + e.getMessage());
        }

        return null;
    }

    protected void onPostExecute(Bitmap result)
    {

        if(Image!=null && result!=null)
        {
            Image.setImageBitmap(result);
        }else{
            if (Image == null){
                //Log.i(Config.LOG_TAG, "onPostExecute image is null");
                SysLog.log("onPostExecute image is null");
            }
            if (result == null){
                SysLog.log("onPostExecute result is null");
            }
        }

        super.onPostExecute(result);
    }
}
