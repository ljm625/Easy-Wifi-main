package com.easygo.easywifi;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;



/**
 * Created by TrixZ on 2014/11/8.
 */
public class InfoDialog extends DialogFragment {
    Boolean encrypt;
    String SSID1, mac;
    int up, down, signal, rate;
    WifiTest wifiadmin;
    EditText passwd;
    //LayoutInflater test=LayoutInflater.from(EasyWifiMain.class);
    NoticeDialogListener mListener;
    private TextView WifiSSid;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void show(FragmentManager manager, String tag,Bundle savedinfo) {
        encrypt =savedinfo.getBoolean("encrypt");
        SSID1=savedinfo.getString("SSID");
        mac = savedinfo.getString("mac");
        up=savedinfo.getInt("up");
        down=savedinfo.getInt("down");
        signal=savedinfo.getInt("signal");
        rate = savedinfo.getInt("rate");
        System.out.println("Encrypt-->"+encrypt);
        super.show(manager, tag);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(encrypt==true) {
        View layout=inflater.inflate(R.layout.infowin, null);
            builder.setView(layout)
                    .setPositiveButton(R.string.Button_Connect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText passedit=(EditText)InfoDialog.this.getDialog().findViewById(R.id.password);
                            String pass=passedit.getText().toString();
                            mListener.onDialogPositiveClick(InfoDialog.this, SSID1, pass, true, mac);
                            System.out.println("Clicked-->OK");   // sign in the user ...
                        }
                    })
                    .setNegativeButton(R.string.Button_Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            InfoDialog.this.getDialog().cancel();
                        }
                    });
            TextView SSID = (TextView)layout.findViewById(R.id.ssid1);
            SSID.setText(SSID1+"");
            //TextView upload=(TextView)layout.findViewById(R.id.up1);
            //upload.setText(up*100+"KB/S");
            System.out.println("Ratings---->" + rate);
            if (rate == 1) {

                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 0);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 2) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 3) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 100);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 4) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 100);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 100);
            } else {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 0);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 0);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            }



            TextView download=(TextView)layout.findViewById(R.id.down1);
            download.setText(down + "KB/S");
            ProgressBar proc = (ProgressBar)layout.findViewById(R.id.progressBar);
            proc.setProgress(signal);



            return builder.create();


        }
        else
        {
            View layout=inflater.inflate(R.layout.infowin2, null);

            builder.setView(layout)
                .setPositiveButton(R.string.Button_Connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("Clicked-->OK");
                        mListener.onDialogPositiveClick(InfoDialog.this, SSID1, "", false, mac);
                    }
                })
                .setNegativeButton(R.string.Button_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(InfoDialog.this);
                        InfoDialog.this.getDialog().cancel();
                    }
                });
            TextView SSID = (TextView)layout.findViewById(R.id.ssid2);
            SSID.setText(SSID1 + "");
            // TextView upload=(TextView)layout.findViewById(R.id.up2);
            // upload.setText(up*100+"KB/S");

            if (rate == 1) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 0);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 2) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 3) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 100);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            } else if (rate == 4) {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 100);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 100);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 100);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 100);
            } else {
                ImageView rate1 = (ImageView) layout.findViewById(R.id.rating1);
                rate1.setAlpha((float) 0);
                ImageView rate2 = (ImageView) layout.findViewById(R.id.rating2);
                rate2.setAlpha((float) 0);
                ImageView rate3 = (ImageView) layout.findViewById(R.id.rating3);
                rate3.setAlpha((float) 0);
                ImageView rate4 = (ImageView) layout.findViewById(R.id.rating4);
                rate4.setAlpha((float) 0);
            }


            TextView download=(TextView)layout.findViewById(R.id.down2);
            download.setText(down + "KB/S");
            ProgressBar proc = (ProgressBar)layout.findViewById(R.id.progressBar);
            proc.setProgress(signal);

            return builder.create();
        }


    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String SSID, String passwd, Boolean encrypt1, String macaddr);

        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
