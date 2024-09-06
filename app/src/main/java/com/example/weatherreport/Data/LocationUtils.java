package com.example.weatherreport.Data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;

/**
 * 获取经纬度、位置工具类
 */
public class LocationUtils {

    @SuppressLint("StaticFieldLeak")
    private volatile static LocationUtils uniqueInstance;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private final Context mContext;

    private LocationUtils(Context context) {
        mContext = context;
        getLocation();
    }

    public static LocationUtils getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils( context );
                }
            }
        }
        return uniqueInstance;
    }

    private void getLocation() {
        locationManager = (LocationManager) mContext.getSystemService( Context.LOCATION_SERVICE );
        List<String> providers = locationManager.getProviders( true );
        if (providers.contains( LocationManager.NETWORK_PROVIDER )) {
            Log.d( "TAG", "如果是网络定位" );
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains( LocationManager.GPS_PROVIDER )) {
            Log.d( "TAG", "如果是GPS定位" );
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Log.d( "TAG", "没有可用的位置提供器" );
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation( locationProvider );
        if (location != null) {
            setLocation( location );
        }

    }

    private void setLocation(Location location) {
        this.location = location;
        String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
        Log.d( "TAG", address );
    }
    public Location showLocation() {
        return location;
    }
    public static String getAddress(Context context,Location location) {
        List<Address> result = null;
        String address = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
//                result = gc.getFromLocation(37.833832912379386,
//                        112.4759295159054, 1);
                Toast.makeText(context, "获取地址信息：" + result.toString(), Toast.LENGTH_LONG).show();
                Log.v("TAG", "获取地址信息：" + result.toString());
                for (int i = 0; i < result.size(); i++) {
                    Log.d("TAG", result.get(i).getSubAdminArea());
                    Log.d("TAG", result.get(i).getAdminArea());
                    Log.d("TAG", result.get(i).getLocality());
                    Log.d("TAG", result.get(i).getAddressLine(0));
                    address = result.get(i).getAddressLine(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

}