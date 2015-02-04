package com.easygo.easywifi;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.location.LocationClientOption.LocationMode;

import java.util.*;

/**
 * Created by TrixZ on 2014/9/19.
 */
public class EasyWifiMain extends FragmentActivity implements InfoDialog.NoticeDialogListener
{
    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface


    MapView mMapView = null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    private volatile boolean isFristLocation = true;
    private BaiduMap mBaiduMap;
    private MyLocationListener mMyLocationListener;
    private float mCurrentAccracy;
    MapFragment newmap;
    private int mXDirection;
    WifiTest wifiadmin;
    WifiManager wific;
    private MapFragmentOne baidu;
    private int selectedIndex;
    private int selectedColor = Color.parseColor("#1b1b1b");
    private ScanResult sr,sr1;
    private WifiFragment wifif=new WifiFragment();
    private Long ts;
    private ListView mDrawerList,mLeftDrawer;
    private LinearLayout leftRL;
    private DrawerLayout drawerLayout;
    private static View bdmap;
    private int[] DrawerItemName={R.string.app_home,R.string.app_wifi,R.string.app_settings,R.string.app_about};
    private int[] DrawerItemIcon={R.drawable.home,R.drawable.wifi2,R.drawable.settings,R.drawable.about};
    java.util.List<ScanResult> List1,List3;

    static final String[] COUNTRIES = new String[] {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan"};

            StringBuffer SB=new StringBuffer();
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    public Marker marker1;
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    private ActionBarDrawerToggle mDrawerToggle;

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
                wifiadmin.addNetwork(wifiConfig);

                //Toast.makeText(Second_Win.this,"connect succeed!",1).show();

            }
        }
        else{
            WifiConfiguration wifiConfig = wifiadmin.CreateWifiInfo(SSID,passwd,3);
            if (wifiConfig==null){

            }else{
                //int Netid=wifiadmin.addNetwork(wifiConfig);
                wifiadmin.addNetwork(wifiConfig);

                //Toast.makeText(Second_Win.this,"connect succeed!",1).show();

            }
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
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
            for (int i = 0; i < List1.size(); i++) {
                sr = List1.get(i);
                int level=wific.calculateSignalLevel(sr.level,100);
                //添加侧边栏的WIFI列表
                System.out.println("Signal------->"+level+"");
               //设置坐标等信息的显示
                latitude=mCurrentLantitude;
                longtitude=mCurrentLongitude;
                SB = SB.append(" " + sr.SSID + " ");
                System.out.println("SSID-->"+sr.SSID);
                int start=sr.capabilities.indexOf('[');
                int stop=sr.capabilities.indexOf(']');
                sr.capabilities=sr.capabilities.substring(start+1,stop);
                System.out.println("Encrypt-->"+sr.capabilities);

                Long tsLong = System.currentTimeMillis()/1000;
                Random r = new Random(tsLong+i);
                int ran1=r.nextInt(5);
                Boolean isright=r.nextBoolean();
                for(int g=0;g<ran1+1;g++)
                {
                    if(isright)

                    latitude=latitude+plus;
                    else
                    latitude=latitude-plus;
                }
                Random r1 = new Random(tsLong+10*i);
                ran1=r1.nextInt(5);
                isright=r1.nextBoolean();
                for(int g=0;g<ran1+1;g++)
                {
                    if(isright)

                        longtitude=longtitude-plus;
                    else
                        longtitude=longtitude+plus;
                }

                LatLng point = new LatLng(latitude, longtitude);
                SetButton(i,sr.SSID,point,sr.capabilities,level);

            }

        }
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
        extrainfo.putString("SSID",SSID);
        extrainfo.putString("auth", authtype);
        extrainfo.putBoolean("isopen",false);
        extrainfo.putDouble("latitude",point.latitude);
        extrainfo.putDouble("longitude",point.longitude);

        marker.setExtraInfo(extrainfo);

        baidu.getBaidu().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                System.out.println("Positioned!");
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


                Bundle savedinfo = new Bundle();
                savedinfo.putString("SSID", SSID);
                savedinfo.putInt("up", up);
                savedinfo.putInt("down", down);
                savedinfo.putInt("signal",signal);


                if (auth.indexOf("ESS") >= 0) {
                    savedinfo.putBoolean("encrypt", false);
                } else {
                    savedinfo.putBoolean("encrypt", true);
                }
                InfoDialog Showdialog = new InfoDialog();
                Showdialog.show(getFragmentManager(), "Dialog", savedinfo);

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
        super.onResume();
//        mMapView.onResume();
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
        BitmapDescriptor mLocMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.custom_loc);
        MyLocationConfiguration configuration=new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,false,mLocMarker);
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
        wifiadmin=new WifiTest(this);
        if (SB != null) {
            SB = new StringBuffer();
        }
        wifiadmin.openWifi();
        wifiadmin.startScan();
        List3 = wifiadmin.getmWifiList();

        for (int i = 0; i < List3.size(); i++) {
            sr = List3.get(i);
            int start=sr.capabilities.indexOf('[');
            int stop=sr.capabilities.indexOf(']');
            sr.capabilities=sr.capabilities.substring(start+1,stop);
            System.out.println("Encrypt-->"+sr.capabilities);


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id",sr);
            map.put("text", sr.SSID);
            int level=wific.calculateSignalLevel(sr.level,100);
            map.put("signal",level);

            if (sr.capabilities.indexOf("ESS")>=0) {
                map.put("img", R.drawable.opennew);
            }
            else {
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
            map.put("text",  EasyWifiMain.this.getString(DrawerItemName[i]));

            map.put("img_icon", DrawerItemIcon[i]);
            list2.add(map);
        }

        return list2;
    }
    public  void onOpenLeftDrawer(View view) {

        drawerLayout.openDrawer(leftRL);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFristLocation = true;
        SDKInitializer.initialize(getApplicationContext());



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
        getFragmentManager().beginTransaction().replace(R.id.content_frame, baidu).commit();
       // mMapView=fragment.getBaidu();


        initMyLocation();   //下面部分为处理Progress Bar 进度的代码
        SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.progressBar2) {
                    Integer theProgress = (Integer) data;
                    ((ProgressBar)view).setProgress(theProgress);
                    // we are dealing with the ProgressBar so set the progress and return true(to let the adapter know you binded the data)
                    // set the progress(the data parameter, I don't know what you actually store in the progress column(integer, string etc)).
                    return true;
                }
                return false; // we are dealing with the TextView so return false and let the adapter bind the data
            }
        };
        SimpleAdapter newadapter=new SimpleAdapter(this, getData(), R.layout.listitem,
                new String[]{"img_pre", "text", "img","signal"},
                new int[]{R.id.img_pre, R.id.text, R.id.img,R.id.progressBar2});
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
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println("outhere--->"+l+"    "+i);
            selectItem(i);
        }

        private void selectItem(int i) {
          Adapter test1=  mDrawerList.getAdapter();
            Map<String, Object> map = (HashMap)test1.getItem(i);
           sr1=(ScanResult) map.get("id");
            int level=wific.calculateSignalLevel(sr1.level,100);

            System.out.println("wtf-->"+sr1.SSID);
            String SSID=sr1.SSID;
            String auth=sr1.capabilities;
            Long tsLong1 = System.currentTimeMillis()/1000;
            Random r = new Random(tsLong1);
            int up=r.nextInt(10);
            int down=r.nextInt(10);
            System.out.println("Auth--->"+auth);
            Bundle savedinfo=new Bundle();
            savedinfo.putString("SSID", SSID);
            savedinfo.putInt("up", up);
            savedinfo.putInt("down",down);
            savedinfo.putInt("signal",level);

            if (auth.indexOf("ESS")>=0) {
                savedinfo.putBoolean("encrypt",false);
            }
            else {
                savedinfo.putBoolean("encrypt",true);
            }
            InfoDialog Showdialog=new InfoDialog();
            Showdialog.show(getFragmentManager(),"Dialog",savedinfo);


            mDrawerList.setItemChecked(i, true);
            drawerLayout.closeDrawer(mDrawerList);
        }
    }

    private class LeftDraweItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            view.setSelected(true);
            selectItem(i);
            if(i==2) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new Settings())
                        .commit();
                getActionBar().setTitle(R.string.app_settings);
            }
            else if (i==0){
                getFragmentManager().beginTransaction().replace(R.id.content_frame, baidu).commit();
                isFristLocation=true;
                getActionBar().setTitle(R.string.app_name);
            }
            else if (i==3){
                getActionBar().setTitle(R.string.app_about);
                Fragment fragment = new InfoFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
            else{
                getActionBar().setTitle(R.string.app_wifi);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, wifif).commit();

          //      wifif.UpdateText("fuck","fuck");

            }
        }
        private void selectItem(int i) {
            mLeftDrawer.setItemChecked(i, true);
            drawerLayout.closeDrawer(leftRL);
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

    public class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
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
            if (isFristLocation)
            {
                isFristLocation = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                initWifi();
                baidu.getBaidu().animateMapStatus(u);
            }
        }
    }

    private void openWifi(){
        Boolean isopen=drawerLayout.isDrawerOpen(mDrawerList);
        if(isopen) {
            drawerLayout.closeDrawer(mDrawerList);
        }
        else  drawerLayout.openDrawer(mDrawerList);
    }
    private void center2myLoc()
    {
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
}
