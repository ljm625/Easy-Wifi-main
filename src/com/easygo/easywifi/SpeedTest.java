package com.easygo.easywifi;


import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by TrixZ on 2015/3/28.
 * 此文件为了实现Wifi的测速功能，并将数据传回主Activity当中,应该以悬浮窗的形式存在。当测速完成后，应当将数据送回服务器端进行处理。
 */
public class SpeedTest extends DialogFragment {

    NoticeDialogListener1 mListener;
    private Info info;
    private byte[] imageBytes;
    private boolean flag;
    private int last_degree = 0, cur_degree;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener1) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        info = new Info();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.speedtest, null);
        builder.setView(layout)
                .setPositiveButton(R.string.Button_Connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick1(SpeedTest.this);
                        try {

                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");

                            field.setAccessible(true);

                            field.set(dialog, false);
                            //   ConnectivityManager connectivityManager=(ConnectivityManager) EasyWifiMain.getSystemService(CONNECTIVITY_SERVICE);
                            //   NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                            info.hadfinishByte = 0;
                            info.speed = 0;
                            info.totalByte = 1024;
                            flag = true;

                            new DownloadThread().start();
                            new GetInfoThread().start();
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                        System.out.println("Clicked-->OK");   // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.Button_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");

                            field.setAccessible(true);

                            field.set(dialog, true);

                            flag = false;

                            mListener.onDialogNegativeClick1(SpeedTest.this);
                            SpeedTest.this.getDialog().cancel();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }

                });
        return builder.create();
    }

    public interface NoticeDialogListener1 {
        public void onDialogPositiveClick1(DialogFragment dialog);

        public void onDialogNegativeClick1(DialogFragment dialog);
    }

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
            System.out.println("Test----->startdown");
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
                System.out.println("Server--->Started");
                while (info.hadfinishByte < info.totalByte && flag) {
                    System.out.println("Server--->running");
                    Thread.sleep(1000);

                    sum += info.speed;
                    counter++;

                    cur_speed = (int) info.speed;
                    ave_speed = (int) (sum / counter);
                    System.out.println("Test----->cur_speed:" + info.speed / 1024 + "KB/S ave_speed:" + ave_speed / 1024);
                    // Message msg = new Message();
                    //  msg.arg1 = ((int) info.speed / 1024);
                    //  msg.arg2 = (ave_speed / 1024);
                    //  msg.what = 0x123;
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
