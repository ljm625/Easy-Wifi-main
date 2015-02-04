package com.easygo.easywifi;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by TrixZ on 2014/12/14.
 */

public  class WifiFragment extends Fragment {
   private View rootView;
    private TextView ssid2,info2;
    public WifiFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.wificonnect, container, false);

        ImageView img = (ImageView)rootView.findViewById(R.id.status);
        img.setBackgroundResource(R.drawable.flash);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
            return rootView;//gai
    }
    public void UpdateText(String ssid,String status){
      //  info2= (TextView)rootView.findViewById(R.id.wifistat);
     //   info2.setText(status);
   //   while (ssid2==null);
       TextView tmp=(TextView) getView().findViewById(R.id.wifiname);
        tmp.setText(ssid);
        tmp=(TextView) getView().findViewById(R.id.wifistat);
        tmp.setText(status);

    }

}