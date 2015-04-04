package com.easygo.easywifi;
/**
 * Created by yhy on 2014/9/17.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiTest {
    WifiManager.WifiLock wifiLock;
    private WifiInfo mWifiInfo;
    private WifiInfo mWifiInfo2;
    private WifiManager mWifiManager;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;

    public WifiTest(Context context){
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo=mWifiManager.getConnectionInfo();
    }
    /*
    * 打开wifi
    *
    * */
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
            //wifiLock.acquire();
        }
    }

    public WifiInfo getConnection()
    {
        WifiInfo mInfo=mWifiManager.getConnectionInfo();
        return  mInfo;
    }

    public int TransNum(){
        int str;
        int i=mWifiManager.getWifiState();
        switch (i){
            case 0:
                str= R.string.closing;
                break;
            case 1:
                str=R.string.unable;
                break;
            case 2:
                str=R.string.opening;
                break;
            case 3:
                str=R.string.connected;
                break;
            default:
                str=R.string.nodata;
                break;
        }
        return str;
    }
    public void closeWifi(){
        if (mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
            //wifiLock.release();
        }
    }

    public void disconnect_current(){
        mWifiInfo2=mWifiManager.getConnectionInfo();
        int current_id=0;
        current_id=mWifiInfo2.getNetworkId();
        if (current_id!=0){
            //mWifiManager.disableNetwork(current_id);
            mWifiManager.disconnect();
        }
    }

    public int checkstate(){
        return mWifiManager.getWifiState();
    }

    public WifiInfo checkinfo()
    {
        return mWifiManager.getConnectionInfo();
    }

    public void startScan(){
        mWifiManager.startScan();
        mWifiList=mWifiManager.getScanResults();
        mWifiConfiguration=mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getmWifiList(){
        return mWifiList;
    }

    public  WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits1(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1)
        {
            //config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
            return config;
        }
        else if(Type == 2)
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            return config;
        }
        else if(Type == 3)
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
            return config;
        }
        else
            return null;
    }
    public WifiConfiguration IsExsits1(String SSID) { // 查看以前是否已经配置过该SSID
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }



    public void addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接

        int wcgID = mWifiManager.addNetwork(wcg);
        //mWifiManager.disconnect();
        mWifiManager.enableNetwork(wcgID,true);
        //mWifiManager.reconnect();

    }

}
