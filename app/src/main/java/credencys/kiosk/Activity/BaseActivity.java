package credencys.kiosk.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import credencys.kiosk.Activity.Util.MyApplicationClass;

/**
 * Created by Credencys on 15/07/2015.
 */
public class BaseActivity extends Activity implements LocationListener {

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    /**
     * Used for when checking the play service is available or not application and request time.
     */
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * This constant for the location update at below specified interval time.
     */
    private final long INTERVAL = 1000 * 10;
    /**
     * This constant for the location update at below specified fastest interval time.
     */
    private final long FASTEST_INTERVAL = 1000 * 5;
    /**
     * This is object of location.
     */
    public Location locationUser;
    /**
     * This is GoogleApiClient object,access from the play service lib for getting the location.
     */
    public WindowManager.LayoutParams layoutParamsStatusBar;
    public WindowManager mWindowManager;
    MyApplicationClass myApplicationClass;
    LocationManager locationManager;

    public boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApplicationClass = (MyApplicationClass) getApplicationContext();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, BaseActivity.this);
        //  intitializeGoogleApiClient();
    }


    public void makePrefered() {
        final PackageManager packageManager = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        startActivity(selector);
    }

    public boolean isMyAppLauncherDefault() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);

        // the packageName of your application
        String packageName = getPackageName();
        List<ComponentName> preferredActivities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        // You can use name of your package here as third argument
        packageManager.getPreferredActivities(filters, preferredActivities, packageName);

        if (preferredActivities != null && preferredActivities.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param view            list of all views
     * @param onClickListener object of interface from activity/fragment.
     */
    public void onClickToSelf(@SuppressWarnings("rawtypes") List view, View.OnClickListener onClickListener) {
        for (Object object : view) {
            ((View) object).setOnClickListener(onClickListener);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationUser = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * This is used for init the google api client from play service for gSetting the location of the user and after getting the location its turn off.
     *//*
    public void intitializeGoogleApiClient() {
        if (checkPlayServices() == false) {
            Toast.makeText(this, "play_service_is_not_available_",
                    Toast.LENGTH_SHORT).show();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(BaseActivity.this).addApi(LocationServices.API).addConnectionCallbacks(BaseActivity.this)
                    .addOnConnectionFailedListener(BaseActivity.this).build();
            mGoogleApiClient.connect();
            createLocationRequest();
        }
    }

    *//**
     * @return boolean for the check the current play service version installed in device
     *//*
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, BaseActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //showCustomToast(getResources().getString(R.string.play_service_is_not_available_));

            }
            return false;
        }
        return true;
    }

    *//**
     * this is used for start the location request for the used to getting the location.
     *//*
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    *//**
     * Stop the location updates
     *//*
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        // Log.d(Constant.TAG, "Location update stopped .......................");
    }

    *//*
     * (non-Javadoc)
     *
     * @see com.google.android.gms.location.LocationListener#onLocationChanged(android .location.Location)
     *//*
    @Override
    public void onLocationChanged(Location location) {
        this.locationUser = location;
        System.out.println("BaseActivity.onLocationChanged()" + location);
        if (mGoogleApiClient.isConnected()) {
            // stopLocationUpdates();
            //mGoogleApiClient.disconnect();
        }
    }

    *//*
     * (non-Javadoc)
     *
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks #onConnected(android.os.Bundle)
     *//*
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constant.TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    *//**
     *
     *//*
    private void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(Constant.TAG, "Location update started ..............: ");
    }

    *//*
     * (non-Javadoc)
     *
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks #onConnectionSuspended(int)
     *//*
    @Override
    public void onConnectionSuspended(int i) {

    }

    *//*
     * (non-Javadoc)
     *
     * @see com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener #onConnectionFailed(com.google.android.gms.common.ConnectionResult)
     *//*
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Constant.TAG, "Connection failed: " + connectionResult.toString());
    }*/
}
