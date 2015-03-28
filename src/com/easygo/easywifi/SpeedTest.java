package com.easygo.easywifi;

import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by TrixZ on 2015/3/28.
 * 此文件为了实现Wifi的测速功能，并将数据传回主Activity当中,应该以悬浮窗的形式存在。当测速完成后，应当将数据送回服务器端进行处理。
 */
public class SpeedTest {

    private Info info;
    private byte[] imageBytes;
    private boolean flag;
    private int last_degree = 0, cur_degree;

    public class Info //2015.3.28 Commited by Ljm625 此类为了进行测速数据的收集，相当于结构体
    {

        public double speed;
        public int hadfinishByte;
        public int totalByte;

    }

    class DownloadThread extends Thread   //主下载测速进程
    {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String url_string = "http://ww2.sinaimg.cn/large/6445479bjw1eqlhb8tvb3j21kw16o4qp.jpg";
            long start_time, cur_time;
            URL url;
            URLConnection connection;
            InputStream iStream;

            try {
                url = new URL(url_string);
                connection = url.openConnection();

                info.totalByte = connection.getContentLength();

                iStream = connection.getInputStream();
                start_time = System.currentTimeMillis();
                while (iStream.read() != -1 && flag) {

                    info.hadfinishByte++;
                    cur_time = System.currentTimeMillis();
                    if (cur_time - start_time == 0) {
                        info.speed = 1000;
                    } else {
                        info.speed = info.hadfinishByte / (cur_time - start_time) * 1000;
                    }
                }
                iStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    class GetInfoThread extends Thread   //发送msg信息，反应当时的速度
    {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            double sum, counter;
            int cur_speed, ave_speed;
            try {
                sum = 0;
                counter = 0;
                while (info.hadfinishByte < info.totalByte && flag) {
                    Thread.sleep(1000);

                    sum += info.speed;
                    counter++;

                    cur_speed = (int) info.speed;
                    ave_speed = (int) (sum / counter);
                    Log.e("Test", "cur_speed:" + info.speed / 1024 + "KB/S ave_speed:" + ave_speed / 1024);
                    Message msg = new Message();
                    msg.arg1 = ((int) info.speed / 1024);
                    msg.arg2 = ((int) ave_speed / 1024);
                    msg.what = 0x123;
                    //  handler.sendMessage(msg);
                }
                if (info.hadfinishByte == info.totalByte && flag) {
                    //  handler.sendEmptyMessage(0x100);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
