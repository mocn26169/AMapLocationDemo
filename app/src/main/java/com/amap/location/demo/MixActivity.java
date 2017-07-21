package com.amap.location.demo;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MixActivity extends Activity {


    private TextView tv;

    LocationManager lm = null;

    Location myLocation = null;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.SSSZ");


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);//加载定位页面

        tv = (TextView) findViewById(R.id.tv);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }


    @Override
    public void onPause() {

        super.onPause();
        lm.removeUpdates(listener);
    }

    @Override
    public void onResume() {

        super.onResume();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // 实际上报时间
            // String time = sdf.format(new Date(location.getTime()));
            // timeText.setText("实际上报时间：" + time);

            if (isBetterLocation(location, myLocation)) {
                // 获取纬度
                double lat = location.getLatitude();
                // 获取经度
                double lon = location.getLongitude();
                // 位置提供者
                String provider = location.getProvider();
                // 位置的准确性
                float accuracy = location.getAccuracy();
                // 高度信息
                double altitude = location.getAltitude();
                // 方向角
                float bearing = location.getBearing();
                // 速度 米/秒
                float speed = location.getSpeed();

                String locationTime = sdf.format(new Date(location.getTime()));
                String currentTime = null;

                if (myLocation != null) {
                    currentTime = sdf.format(new Date(myLocation.getTime()));
                    myLocation = location;

                } else {
                    myLocation = location;
                }

                // 获取当前详细地址
                StringBuffer sb = new StringBuffer();
                if (myLocation != null) {
                    Geocoder gc = new Geocoder(MixActivity.this);
                    List<Address> addresses = null;
                    try {
                        addresses = gc.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        sb.append(address.getCountryName() + address.getLocality());
                        sb.append(address.getSubThoroughfare());

                    }
                }

                tv.setText("经度：" + lon + "\n纬度：" + lat + "\n服务商：" + provider + "\n准确性：" + accuracy + "\n高度：" + altitude + "\n方向角：" + bearing
                        + "\n速度：" + speed + "\n上次上报时间：" + currentTime + "\n最新上报时间：" + locationTime + "\n您所在的城市：" + sb.toString());

            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("tag", "onStatusChanged: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("tag", "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("tag", "onProviderDisabled: " + provider);
        }

    };

    private static final int TWO_MINUTES = 1000 * 1 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
