package com.easygo.easywifi;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TrixZ on 2014/12/14.
 */

public  class WifiFragment extends Fragment {
    private static Handler handler;
    public int counter = 0;
    WifiTest wifiadmin;
    TextView tv;
    Timer timer = new Timer(true);
    AnimationDrawable frameAnimation;
    WifiManager wific;
    private View rootView;
    private TextView ssid2, info2, downspeed;
    private ImageView rate;
    private String ratedSSID = "";
    private boolean isok=false;
    private List<EasyWifiMain.WifiInfo> mlist = new ArrayList<EasyWifiMain.WifiInfo>();

    public WifiFragment() {
        // Empty constructor required for fragment subclasses

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public void setWifiList(List<EasyWifiMain.WifiInfo> list) {
        mlist = list;
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
        downspeed = (TextView) rootView.findViewById(R.id.downspeed);

        info2 = (TextView)rootView.findViewById(R.id.wifistat);
        isok=true;

        rate = (ImageView) rootView.findViewById(R.id.rating);



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
                        //2015.4.6 ADD BY LJM


                        WifiInfo mInfo=wifiadmin.getConnection();


                        if(wifiadmin.TransNum()==R.string.closing||wifiadmin.TransNum()==R.string.unable||wifiadmin.TransNum()==R.string.connected){
                            frameAnimation.stop();
                        }
                        if (mInfo.getSSID() != "0x" && mInfo.getSSID() != "<unknown ssid>") { //更新了判断机制
                            int stop = mInfo.getSSID().length();
                            String ssid1 = mInfo.getSSID().substring(1, stop - 1);
                            ssid2.setText(ssid1);
                            EasyWifiMain.WifiInfo tmp = new EasyWifiMain.WifiInfo();
                            if (mlist != null) {
                                int i1 = 0;
                                while (i1 < mlist.size()) {
                                    tmp = mlist.get(i1);
                                    if (mInfo.getBSSID().equals(tmp.mac))
                                        i1 = mlist.size() + 1;
                                    else i1 = i1 + 1;
                                }
                                if (tmp != null && i1 == mlist.size() + 1) {
                                    downspeed.setText(tmp.downlink + "");

                                    if (!ratedSSID.equals(ssid1))           //判断评级
                                    {
                                        System.out.println("We are rating stars on " + ssid1);
                                        int down = tmp.downlink;
                                        int level = wific.calculateSignalLevel(mInfo.getRssi(), 100);
                                        if (level >= 80 && down >= 200) {
                                            rate.setImageResource(R.drawable.rate4);
                                        } else if (level >= 80 && down >= 100) {
                                            rate.setImageResource(R.drawable.rate3);
                                        } else if (level >= 40 && down >= 30) {
                                            rate.setImageResource(R.drawable.rate2);
                                        } else if (level >= 20) {
                                            rate.setImageResource(R.drawable.rate1);
                                        } else {
                                            rate.setImageResource(R.drawable.rate0);
                                        }
                                        ratedSSID = ssid1;
                                    }


                                }
                            } else downspeed.setText(R.string.unknown);


                        }
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