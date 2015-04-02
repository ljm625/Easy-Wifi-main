package com.easygo.easywifi;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

//微博登陆用包
//import com.sina.*;

/**
 * Created by TrixZ on 2014/9/19.
 */
public class EasyWifiMain extends FragmentActivity implements InfoDialog.NoticeDialogListener, SpeedTest.NoticeDialogListener1
{
    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    //test

    static final String[] COUNTRIES = new String[]{
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan"};
    private static Handler handler2, handler3;
    private static View bdmap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public int counter = 0;
    public Marker marker1;
    MapView mMapView = null;
    MapFragment newmap;
    WifiTest wifiadmin;
    WifiManager wific;
    TextView tv;
    Timer timer = new Timer(true);
    java.util.List<ScanResult> List1, List3;
    StringBuffer SB = new StringBuffer();
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    private volatile boolean isFristLocation = true;
    private BaiduMap mBaiduMap;
    private MyLocationListener mMyLocationListener;
    private float mCurrentAccracy;
    private SpeedTest speedTest;
    private InfoDialog infoDialog;
    private int mXDirection;
    private MapFragmentOne baidu;
    private int selectedIndex;
    private int requestnum,wifinum;
    private Bundle wifibundle;
    private Bundle globalbundle; //新添加修改SAVEDBUNDLE
    private int selectedColor = Color.parseColor("#1b1b1b");
    private ScanResult sr,sr1;
    private List<WifiInfo> mlist=new ArrayList<WifiInfo>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();

            for (int i = 0; i < mlist.size(); i++) {


                WifiInfo tmp = mlist.get(i);
                if (tmp.mac != null) {      //无网络检测
                    Double latitude = tmp.latitude;
                    Double longtitude = tmp.longtitude;
                    SB = SB.append(" " + tmp.ssid + " ");
                    System.out.println("SSID-->" + tmp.ssid);

                    System.out.println("Encrypt-->" + tmp.encryption);


                    LatLng point = new LatLng(latitude, longtitude);
                    SetButton(i, tmp.ssid, point, tmp.encryption, tmp.signal);
                }
            }
            // TODO: UI界面的更新等相关操作
            //
        }
    };
    private WifiFragment wifif=new WifiFragment();
    private Long ts;
    private ListView mDrawerList,mLeftDrawer;
    private LinearLayout leftRL;
    private DrawerLayout drawerLayout;
    private boolean uploaded=false;
    private int[] DrawerItemName={R.string.app_home,R.string.app_wifi,R.string.app_settings,R.string.app_about};
    private int[] DrawerItemIcon={R.drawable.home,R.drawable.wifi2,R.drawable.settings,R.drawable.about};
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    Runnable networkTask = new Runnable() {


        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Bundle data = new Bundle();
            System.out.println("Starting fingding data");
            for (int i = 0; i < List1.size(); i++) {
                sr = List1.get(i);
                int level = wific.calculateSignalLevel(sr.level, 100);
                int start = sr.capabilities.indexOf('[');
                int stop = sr.capabilities.indexOf(']');
                sr.capabilities = sr.capabilities.substring(start + 1, stop);
                WifiInfo tmpwifi = new WifiInfo();                       //2015.3.10 Changed by Ljm625
                try {
                    HttpPost request = new HttpPost("http://www.52mzone.com/easywifi/ewifi.php");
                    //2015.3.10 Changed by Ljm625
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("SSID", sr.SSID));
                    params.add(new BasicNameValuePair("MAC_ID", sr.BSSID));
                    params.add(new BasicNameValuePair("latitude", mCurrentLantitude + ""));
                    params.add(new BasicNameValuePair("longtitude", mCurrentLongitude + ""));
                    params.add(new BasicNameValuePair("encrypt", sr.capabilities));
                    params.add(new BasicNameValuePair("signal", level + ""));
                    HttpEntity httpentity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                    // request.setEntity(new StringEntity(params.toString(), HTTP.UTF_8));

                    // 键为null或使用json不支持的数字格式(NaN, infinities)
                    request.setEntity(httpentity);

                    HttpClient httpclient = new DefaultHttpClient();
                    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
                    // 检测超时
                    httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
                    HttpResponse httpResponse = httpclient.execute(request);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {

                        String retSrc = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject result = new JSONObject(retSrc);
                        tmpwifi.ssid = result.getString("ssid");
                        tmpwifi.mac = result.getString("mac");
                        tmpwifi.encryption = sr.capabilities;
                        tmpwifi.uplink = result.getInt("uplink");
                        tmpwifi.downlink = result.getInt("downlink");
                        Boolean islocated = result.getBoolean("islocated");
                        tmpwifi.signal = level;
                        System.out.println("This locate is generated by web" + " ssid---->" + tmpwifi.ssid);
                        System.out.println("This locate is generated by web" + " locate---->" + islocated);

                        //  data.putInt("uplink", result.getInt("uplink"));
                        //    data.putInt("downlink",result.getInt("downlink"));
                        //    data.putString("islocated", result.getString("islocated"));
                        //  data.putDouble("latitude", result.getDouble("latitude"));
                        //  data.putDouble("longtitude",result.getDouble("longtitude"));
                        //  data.putInt("rate", result.getInt("rate"));
                        //  data.putString("passwd", result.getString("pass"));
                        if (islocated == true) {
                            tmpwifi.latitude = result.getDouble("latitude");
                            tmpwifi.longtitude = result.getDouble("longtitude");
                            System.out.println("This locate is generated by web" + "---->" + tmpwifi.ssid);
                        } else {
                            Double latitude = mCurrentLantitude;
                            Double longtitude = mCurrentLongitude;
                            double plus = 0.0001;
                            Long tsLong = System.currentTimeMillis() / 1000;
                            Random r = new Random(tsLong + i);
                            int ran1 = r.nextInt(5);
                            Boolean isright = r.nextBoolean();
                            for (int g = 0; g < ran1 + 1; g++) {
                                if (isright)

                                    latitude = latitude + plus;
                                else
                                    latitude = latitude - plus;
                            }
                            Random r1 = new Random(tsLong + 10 * i);
                            ran1 = r1.nextInt(5);
                            isright = r1.nextBoolean();
                            for (int g = 0; g < ran1 + 1; g++) {
                                if (isright)

                                    longtitude = longtitude - plus;
                                else
                                    longtitude = longtitude + plus;
                            }
                            tmpwifi.latitude = latitude;
                            tmpwifi.longtitude = longtitude;
                        }
                        tmpwifi.rate = result.getInt("rate");
                        tmpwifi.passwd = result.getString("pass");
                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    ;

                } catch (IOException e) {
                    e.printStackTrace();
                    ;

                } catch (JSONException e) {
                    e.printStackTrace();
                    ;
                }
                mlist.add(i, tmpwifi); //打包信息进入Wifiinfo的List 这样可以统一管理、类似结构体
                System.out.println("The id is " + i);
            }
            Message msg = new Message();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    private ActionBarDrawerToggle mDrawerToggle;

    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
// TODO: handle exception
            Log.v("error", e.toString());
        }
        return false;
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public void onDialogPositiveClick(DialogFragment dialog,String SSID,String passwd,Boolean encrypt1) {
        // User touched the dialog's positive button
//        View DialogView =test.inflate(R.layout.infowin,null);
    //    EditText passwd1=(EditText)DialogView.findViewById(R.id.password);
    //     String pass=passwd1.getText().toString();
        System.out.println("Wtf----->ok");
       System.out.println("Wtf1----->"+passwd);
        if(!encrypt1){
            WifiConfiguration wifiConfig = wifiadmin.CreateWifiInfo(SSID,"",1);
            if (wifiConfig==null){

            }else{
                //int Netid=wifiadmin.addNetwork(wifiConfig);
                wifiadmin.disconnect_current();
                wifiadmin.addNetwork(wifiConfig);
                //Toast.makeText(Second_Win.this,"connect succeed!",1).show();

            }
        }
        else{
            WifiConfiguration wifiConfig = wifiadmin.CreateWifiInfo(SSID,passwd,3);
            if (wifiConfig==null){

            }else{
                //int Netid=wifiadmin.addNetwork(wifiConfig);
                wifiadmin.disconnect_current();
                wifiadmin.addNetwork(wifiConfig);
                //Toast.makeText(Second_Win.this,"connect succeed!",1).show();

            }
        }
        //清空对话框
        infoDialog = null;
        new CheckWifiStatus().start();
    }


    public void onDialogPositiveClick1(DialogFragment dialog) {
        // User touched the dialog's positive button
//        View DialogView =test.inflate(R.layout.infowin,null);
        //    EditText passwd1=(EditText)DialogView.findViewById(R.id.password);
        //     String pass=passwd1.getText().toString();

    }

    public void onDialogNegativeClick1(DialogFragment dialog) {
        // User touched the dialog's positive button
//        View DialogView =test.inflate(R.layout.infowin,null);
        //    EditText passwd1=(EditText)DialogView.findViewById(R.id.password);
        //     String pass=passwd1.getText().toString();
        speedTest = null;
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        infoDialog = null;
    }

    private void initMyLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        System.out.println("start-->location");

    }

    public void onReconnect(View v)
    {
        wifiadmin.disconnect_current();

    }

    private void initWifi()
    {
        wifiadmin=new WifiTest(this);
        if (SB != null) {
            SB = new StringBuffer();
        }
        wifiadmin.openWifi();
        wifiadmin.startScan();
        List1 = wifiadmin.getmWifiList();
        double plus=0.0001;
        double latitude=mCurrentLantitude;
        double longtitude=mCurrentLongitude;

        if (List1 != null) {
           // for (int i = 0; i < List1.size(); i++) {
           //     sr = List1.get(i);
           //     int level=wific.calculateSignalLevel(sr.level,100);
           //     int start=sr.capabilities.indexOf('[');
           //     int stop=sr.capabilities.indexOf(']');
           //     sr.capabilities=sr.capabilities.substring(start+1,stop);
           //     WifiInfo tmpwifi=new WifiInfo();                       //2015.3.10 Changed by Ljm625
           //     tmpwifi.ssid=sr.SSID;
           //     tmpwifi.mac=sr.BSSID;
           //     tmpwifi.signal=level;
           //     String islocated;

             if(uploaded==false) {

                 if (isConnect(this)==true) {
                     new Thread(networkTask).start();
                 }else;
             } else {
                 System.out.println("Not Getting Data From Server");
                 Message msg = new Message();
                 Bundle data=new Bundle();
                 data.putString("value", "请求结果");
                 msg.setData(data);
                 handler.sendMessage(msg);
             }




/**
 * 网络操作相关的子线程
 */



                //添加侧边栏的WIFI列表
              //  System.out.println("Signal------->"+level+"");
               //设置坐标等信息的显示




        }
    }

    public Boolean getSetting() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);

        // SharedPreferences prefs = getSharedPreferences("com.easygo.easywifi_settings", Context.MODE_WORLD_READABLE);
        Boolean setdefault = prefs.getBoolean("quit_setting", false);  //false表示没有查到checkbox这个key的返回值
        Boolean setupdate = prefs.getBoolean("update_check_setting", false);  //false表示没有查到checkbox这个key的返回值
        Boolean setquit = prefs.getBoolean("default_setting", false);  //false表示没有查到checkbox这个key的返回值
        Boolean setnotification =prefs.getBoolean("notification_setting", false);  //false表示没有查到checkbox这个key的返回值
        // System.out.println(setnotification);
        return setnotification;
    }

    private void notification(){
        String WifID=null;
        android.net.wifi.WifiInfo wifiinfo=wifiadmin.checkinfo();
        WifID=wifiinfo.getSSID().toString();
        NotificationCompat.Builder mBuilder;
        if (WifID=="0x")
        {
            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.wifi)
                            .setContentTitle("EasyWifi is running ")
                            .setContentText("Not Connected ");
        }
        else {
            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.wifi)
                            .setContentTitle("EasyWifi is running ")
                            .setContentText("Connecting to " + WifID);
        }
        Intent resultIntent = new Intent(this, EasyWifiMain.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(EasyWifiMain.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 001;
        mNotificationManager.notify(mId, mBuilder.build());

    }

    private void SetButton(int i,String SSID,LatLng point,String authtype,int signallevel)
    {
        Bundle extrainfo=new Bundle();
       // LatLng point = new LatLng(39.969052, 116.362668);
        BitmapDescriptor bitmap;
        if (signallevel>70) {
            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.wifi_blue);
        }
        else if(signallevel>40)
        {
            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.wifi_orange);
        }
        else
        {
            bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.wifi_black);
        }
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        Marker marker = (Marker) (baidu.getBaidu().addOverlay(option));
        //extrainfo.putString("id","1");
        extrainfo.putInt("id",i);
        extrainfo.putInt("signal",signallevel);
        extrainfo.putInt("uplink",i);
        extrainfo.putInt("downlink",signallevel);
        extrainfo.putString("SSID",SSID);
        extrainfo.putString("auth", authtype);
        extrainfo.putBoolean("isopen",false);
        extrainfo.putDouble("latitude",point.latitude);
        extrainfo.putDouble("longitude",point.longitude);

        marker.setExtraInfo(extrainfo);

        baidu.getBaidu().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //System.out.println("Positioned!");
                Bundle temp = marker.getExtraInfo();
                String tmpid = temp.getString("id");
                String SSID = temp.getString("SSID");
                String auth = temp.getString("auth");
                int signal = temp.getInt("signal");
                Long tsLong1 = System.currentTimeMillis() / 1000;
                Random r = new Random(tsLong1);
                int up = r.nextInt(10);
                int down = r.nextInt(10);

                System.out.println("Auth--->" + auth);


                globalbundle = new Bundle();
                globalbundle.putString("SSID", SSID);
                globalbundle.putInt("up", up);
                globalbundle.putInt("down", down);
                globalbundle.putInt("signal", signal);


                if (auth.indexOf("ESS") >= 0 ||auth.indexOf("WPS") >= 0) {
                    globalbundle.putBoolean("encrypt", false);
                } else {
                    globalbundle.putBoolean("encrypt", true);
                }
                //     infoDialog = new InfoDialog();
                //   infoDialog.show(getFragmentManager(), "Dialog", globalbundle);
                speedTest = new SpeedTest();                            //测试用
                speedTest.show(getFragmentManager(), "speed");
                boolean tmpopen = temp.getBoolean("isopen");


                return true;
            }
        });




  //      Button button = new Button(getApplicationContext());
   //     button.setBackgroundResource(R.drawable.popup);
   //     button.setTextColor(Color.parseColor("#000000"));
   //     button.setText("测试WIFI");
   //     LatLng pt = new LatLng(39.969052, 116.362668);
    //    InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);
    //    mBaiduMap.showInfoWindow(mInfoWindow);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //      mMapView.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();      //2015.3.29 修复一个对话框引起的crash
        if (infoDialog != null) {
            if (!infoDialog.isAdded()) {
                infoDialog.show(getFragmentManager(), "Dialog", globalbundle);
            }
        }
        //   if (speedTest != null) {
        //        if (!speedTest.isAdded()) {
        //            speedTest.show(getFragmentManager(), "speed");
        //        }
        //    }
//       mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //     mMapView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        baidu.getBaidu().setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();


       baidu.getBaidu().setMyLocationEnabled(true);
        //   BitmapDescriptor mLocMarker = BitmapDescriptorFactory
        //           .fromResource(R.drawable.custom_loc);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, null);
        baidu.getBaidu().setMyLocationConfigeration(configuration);
//        mMapView.showZoomControls(false);
        if (!mLocationClient.isStarted())
        {
            mLocationClient.start();
        }
    }

    public void checkupdate(View view)
    {
       Toast toast= Toast.makeText(getApplicationContext(),R.string.new_version,Toast.LENGTH_LONG);
        toast.show();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        wifiadmin = new WifiTest(this);
        if (SB != null) {
            SB = new StringBuffer();
        }
        wifiadmin.openWifi();
        wifiadmin.startScan();
        List3 = wifiadmin.getmWifiList();

        for (int i = 0; i < List3.size(); i++) {
            sr = List3.get(i);
            int start = sr.capabilities.indexOf('[');
            int stop = sr.capabilities.indexOf(']');
            sr.capabilities = sr.capabilities.substring(start + 1, stop);
            System.out.println("Encrypt-->" + sr.capabilities);


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", sr);
            map.put("text", sr.SSID);
            int level = wific.calculateSignalLevel(sr.level, 100);
            map.put("signal", level);

            if (sr.capabilities.indexOf("ESS") >= 0) {
                map.put("img", R.drawable.opennew);
            } else {
                map.put("img", R.drawable.lock);
            }

            map.put("img_pre", R.drawable.wifi);
            list2.add(map);
        }

        return list2;
    }

    private List<Map<String, Object>> getData2() {
        List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
        if (SB != null) {
            SB = new StringBuffer();
        }
        for (int i = 0; i < DrawerItemName.length; i++) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", EasyWifiMain.this.getString(DrawerItemName[i]));

            map.put("img_icon", DrawerItemIcon[i]);
            list2.add(map);
        }

        return list2;
    }

    public void onOpenLeftDrawer(View view) {

        drawerLayout.openDrawer(leftRL);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFristLocation = true;
        SDKInitializer.initialize(getApplicationContext());


        handler3 = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2:
                        StartSpeedTest();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        handler2 = new Handler(EasyWifiMain.this.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Boolean test = getSetting();
                        if (test == true) notification();
                        break;
                    case 2:
                        StartSpeedTest();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                counter = counter + 1;
                Message message = new Message();
                message.what = 1;
                handler2.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);


        if (isConnect(this) == false) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.network_error)
                    .setMessage(R.string.network_message)
                    .setPositiveButton(R.string.drawer_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
// TODO Auto-generated method stub
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }
                    }).show();
        }

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);

        setContentView(R.layout.main);

        mDrawerList = (ListView) findViewById(R.id.right_drawer);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Easy Wifi");
            }

        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        baidu = new MapFragmentOne();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, baidu).commitAllowingStateLoss();
        // mMapView=fragment.getBaidu();


        initMyLocation();   //下面部分为处理Progress Bar 进度的代码
        SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.progressBar2) {
                    Integer theProgress = (Integer) data;
                    ((ProgressBar) view).setProgress(theProgress);
                    // we are dealing with the ProgressBar so set the progress and return true(to let the adapter know you binded the data)
                    // set the progress(the data parameter, I don't know what you actually store in the progress column(integer, string etc)).
                    return true;
                }
                return false; // we are dealing with the TextView so return false and let the adapter bind the data
            }
        };
        SimpleAdapter newadapter = new SimpleAdapter(this, getData(), R.layout.listitem,
                new String[]{"img_pre", "text", "img", "signal"},
                new int[]{R.id.img_pre, R.id.text, R.id.img, R.id.progressBar2});
        newadapter.setViewBinder(viewBinder);
        mDrawerList.setAdapter(newadapter);

        mLeftDrawer.setAdapter(new SimpleAdapter(this, getData2(), R.layout.listdrawer,
                new String[]{"img_icon", "text"},
                new int[]{R.id.img_icon, R.id.text2}));

        leftRL = (LinearLayout) findViewById(R.id.whatYouWantInLeftDrawer);
        mLeftDrawer.setItemChecked(0, true);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mLeftDrawer.setOnItemClickListener(new LeftDraweItemClickListener());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void StartSpeedTest() {
        System.out.println("BackgroundCheck----->" + isApplicationBroughtToBackground(EasyWifiMain.this.getApplicationContext()));
        if (isApplicationBroughtToBackground(EasyWifiMain.this.getApplicationContext())) {

        } else {
            speedTest = new SpeedTest();
            speedTest.show(getFragmentManager(), "speed");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_wifi:
                openWifi();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void openWifi() {
        Boolean isopen = drawerLayout.isDrawerOpen(mDrawerList);
        if (isopen) {
            drawerLayout.closeDrawer(mDrawerList);
        } else drawerLayout.openDrawer(mDrawerList);
    }

    private void center2myLoc() {
        LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        baidu.getBaidu().animateMapStatus(u);
    }

    public static class InfoFragment extends Fragment {
        public InfoFragment() {

            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about, container, false);
            //   MapView fuckbaidu=(MapView)rootView.findViewById(R.id.bmapView);
            //    BaiduMap mBaiduMap=fuckbaidu.getMap();
            //    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder(mBaiduMap.getMapStatus()).zoom(19).build()));
            //    mBaiduMap.setMyLocationEnabled(true);
            //    mBaiduMap.setMaxAndMinZoomLevel(19, 17);
//            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            //      String planet = "Test";
            //       System.out.println("Q---->123");

            //  int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
            //          "drawable", getActivity().getPackageName());
            //  ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle("关于");
            return rootView;//gai
        }
    }

    class CheckWifiStatus extends Thread   //确认WIFI状态，决定是否进行测速
    {  //TODO: 需要把设置选项判断的代码加进去
        @Override
        public void run() {

            // TODO Auto-generated method stub
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            // System.out.println("Output------>" + networkInfo.getTypeName());
            String netstat = networkInfo.getTypeName().toString().trim();
            while (!netstat.equals("WIFI")) {
                try {
                    connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                    netstat = networkInfo.getTypeName().toString().trim();
                    Thread.sleep(2000);
                    System.out.println("Info----->" + netstat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message msg = new Message();
            msg.what = 2;
            handler3.sendMessage(msg);
        }

    }

    public class WifiInfo   //2015.3.10 Changed by Ljm625 设置一个类当做结构体使用
    {
        public String ssid;
        public String mac;
        public Double longtitude;
        public Double latitude;
        public int uplink;
        public int downlink;
        public int signal;
        public String encryption;
        public String passwd;
        public int rate;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println("outhere--->" + l + "    " + i);
            selectItem(i);
        }

        private void selectItem(int i) {
            Adapter test1 = mDrawerList.getAdapter();
            Map<String, Object> map = (HashMap) test1.getItem(i);
            sr1 = (ScanResult) map.get("id");
            int level = wific.calculateSignalLevel(sr1.level, 100);

            System.out.println("wtf-->" + sr1.SSID);
            String SSID = sr1.SSID;
            String auth = sr1.capabilities;
            Long tsLong1 = System.currentTimeMillis() / 1000;
            Random r = new Random(tsLong1);
            int up = r.nextInt(10);
            int down = r.nextInt(10);
            System.out.println("Auth--->" + auth);
            globalbundle = new Bundle();
            globalbundle.putString("SSID", SSID);
            globalbundle.putInt("up", up);
            globalbundle.putInt("down", down);
            globalbundle.putInt("signal", level);

            if (auth.indexOf("ESS") >= 0) {
                globalbundle.putBoolean("encrypt", false);
            } else {
                globalbundle.putBoolean("encrypt", true);
            }
            infoDialog = new InfoDialog();
            infoDialog.show(getFragmentManager(), "Dialog", globalbundle);


            mDrawerList.setItemChecked(i, true);
            drawerLayout.closeDrawer(mDrawerList);
        }
    }

    private class LeftDraweItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            view.setSelected(true);
            selectItem(i);
            if (i == 2) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new Settings())
                        .commitAllowingStateLoss();
                getActionBar().setTitle(R.string.app_settings);
            } else if (i == 0) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, baidu).commitAllowingStateLoss();
                isFristLocation = true;
                getActionBar().setTitle(R.string.app_name);
            } else if (i == 3) {
                getActionBar().setTitle(R.string.app_about);
                Fragment fragment = new InfoFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            } else {
                getActionBar().setTitle(R.string.app_wifi);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, wifif).commitAllowingStateLoss();

                // wifif.UpdateText("fuck","fuck");

            }
        }

        private void selectItem(int i) {
            mLeftDrawer.setItemChecked(i, true);
            drawerLayout.closeDrawer(leftRL);
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            System.out.println("data-->received");
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            // 构造定位数据

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mXDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mCurrentAccracy = location.getRadius();
            // 设置定位数据
            baidu.getBaidu().setMyLocationData(locData);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            // 设置自定义图标
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, mCurrentMarker);
            baidu.getBaidu().setMyLocationConfigeration(config);
            // 第一次定位时，将地图位置移动到当前位置
            if (isFristLocation) {
                isFristLocation = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                initWifi();
                baidu.getBaidu().animateMapStatus(u);
            }
        }
    }
}
