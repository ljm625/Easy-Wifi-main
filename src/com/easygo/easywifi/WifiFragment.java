package com.easygo.easywifi;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TrixZ on 2014/12/14.
 */

public  class WifiFragment extends Fragment {
   private View rootView;
    WifiTest wifiadmin;
    public int counter = 0;
    TextView tv;
    Timer timer = new Timer(true);
    private static Handler handler;
    private TextView ssid2,info2;
    private boolean isok=false;
    AnimationDrawable frameAnimation;
    public WifiFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        rootView = inflater.inflate(R.layout.wificonnect, container, false);

        ImageView img = (ImageView)rootView.findViewById(R.id.status);
        ssid2 = (TextView)rootView.findViewById(R.id.wifiname);
        info2 = (TextView)rootView.findViewById(R.id.wifistat);
        isok=true;
        img.setBackgroundResource(R.drawable.flash);
        // Get the background, which has been compiled to an AnimationDrawable object.

        frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        wifiadmin=new WifiTest(getActivity().getApplicationContext()); //fix by ljm


        handler =new Handler(){
            public void handleMessage(Message msg){
                switch(msg.what){
                    case 1:

                        WifiInfo mInfo=wifiadmin.getConnection();
                        if(wifiadmin.TransNum()==R.string.closing||wifiadmin.TransNum()==R.string.unable||wifiadmin.TransNum()==R.string.connected){
                            frameAnimation.stop();
                        }
                        if(mInfo.getSSID()!="0x")
                        ssid2.setText(mInfo.getSSID());
                        else   ssid2.setText("Easy Wifi");
                        if(wifiadmin.TransNum()==R.string.closing)
                        info2.setText(R.string.closing);
                        else if (wifiadmin.TransNum()==R.string.opening) info2.setText(R.string.opening);
                        else if (wifiadmin.TransNum()==R.string.unable) info2.setText(R.string.unable);
                        else if (wifiadmin.TransNum()==R.string.connected) info2.setText(R.string.connected);



                        break;
                }
                super.handleMessage(msg);
            }
        };
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                counter=counter+1;
                Message message = new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 0,1000);

            return rootView;//gai
    }
    public void UpdateText(String ssid,String status){
      //  info2= (TextView)rootView.findViewById(R.id.wifistat);
     //   info2.setText(status);
   //   while (ssid2==null);
        //while(isok==false);
        ssid2.setText(ssid);
        info2.setText(status);

    }

}