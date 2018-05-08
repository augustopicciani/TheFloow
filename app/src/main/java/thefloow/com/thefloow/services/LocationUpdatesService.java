package thefloow.com.thefloow.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import thefloow.com.thefloow.R;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.repositories.JourneyRepository;
import thefloow.com.thefloow.repositories.LocationRepository;
import thefloow.com.thefloow.util.Utils;

/**
 * Created by Augusto on 05/05/2018.
 */

public class LocationUpdatesService extends Service {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final float SMALLEST_UPDATE_INTERVAL = 10;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private boolean isTracking = false;
    private  List<LatLng> currentLocationPoints;
    private int NOTIFICATION_ID = 1;
    private Date startDate = null;


    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Store current location coordinates
         */
        currentLocationPoints = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                List<Location> locationList = locationResult.getLocations();
                mLocation = locationList.get(locationList.size() - 1);
                Log.d("test", "Location from service: " + mLocation.getLatitude() + " " + mLocation.getLongitude());
                Log.d("test", "istracking: " + isTracking);

                LocationRepository.getInstance().setCurrentLocation(mLocation);

                if (isTracking) {

                    currentLocationPoints.add(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                    LocationRepository.getInstance().setPoints(currentLocationPoints);

                } else {

                    if (currentLocationPoints.size() > 0) {
                        saveJourneyTrack();
                    }
                }


            }
        };
        createLocationRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLocationUpdates();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LoBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(!isTracking){
            stopLocationUpdates();
        }

        return true;
    }

    private void createLocationRequest() {
        // TODO: 06/05/2018 DECOMMENT SMALLEST DISPLACEMENT TO REDUCE DRAIN BATTERY
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        //mLocationRequest.setSmallestDisplacement(SMALLEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }

    @SuppressLint("MissingPermission")
    private void stopLocationUpdates(){

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /*
    Save Journey into database
     */
    private void saveJourneyTrack(){

        Date date = Calendar.getInstance().getTime();
        String journeyID = Utils.generateUniqueID();
        Log.d("test", "journeyID : " + journeyID);

        for(int i = 0; i < currentLocationPoints.size(); i++){
            LatLng currentItem = currentLocationPoints.get(i);
            if(i == 0){
                /*
                Store only first item with start Date previously setted
                 */
                JourneyModel journeyModel = new JourneyModel(journeyID, currentItem.latitude, currentItem.longitude, startDate);
                new SaveJourneyAsyncTask().execute(journeyModel);

            }else {
                JourneyModel journeyModel = new JourneyModel(journeyID, currentItem.latitude, currentItem.longitude, date);
                new SaveJourneyAsyncTask().execute(journeyModel);
            }
        }
        Toast.makeText(LocationUpdatesService.this, "Journey saved", Toast.LENGTH_SHORT).show();

    }

    private void setStartDate(Date value){
            this.startDate = value;

    }

    public void trackingToggle(){
        this.isTracking = isTracking ? false : true;
       // JourneyRepository.getInstance().setIsTracking(this.isTracking);
        setNotification();
        if(isTracking){
            setStartDate(Calendar.getInstance().getTime());
        }

    }


    public class LoBinder extends Binder {

        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * AsyncTask that is used to save all current location points into DB
     */
    private class SaveJourneyAsyncTask extends AsyncTask<JourneyModel, Void, Void> {


        @Override
        protected Void doInBackground(final JourneyModel... params) {
            Log.d("test", "save journey..");
            JourneyRepository.getInstance().saveJourney(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            currentLocationPoints.clear();
            setStartDate(null);
        }
    }

    /**
     * Set notification to show when tracking is on or off
     */
    private void setNotification(){

        String content = isTracking ? getString(R.string.service_tracking_on) : getString(R.string.service_tracking_off);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CH_ID");

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.service_notification_title))
                .setContentText(content)
                .setContentInfo(getString(R.string.service_notification_info));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}
