package com.easygo.easywifi;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;

import java.util.Map;

/**
 * Created by TrixZ on 2014/12/14.
 */

public class MapFragmentOne extends Fragment
{
    private View rootView;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private double mCurrentLantitude;
    private double mCurrentLongitude;
    public MapFragmentOne() {

        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.baidumap, container, false);
        //获取百度地图并对应到对象mBaiduMap
        mMapView=(MapView)rootView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //设置百度地图的默认缩放级别 19为最大
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder(mBaiduMap.getMapStatus()).zoom(19).build()));
        //设置为显示自己的定位位置
        mBaiduMap.setMyLocationEnabled(true);
        //设置最高和最低的缩放级别
        mBaiduMap.setMaxAndMinZoomLevel(19, 17);
        mMapView.showZoomControls(false);
        return rootView;
    }

    public BaiduMap getBaidu(){
        return mBaiduMap;
    }
}