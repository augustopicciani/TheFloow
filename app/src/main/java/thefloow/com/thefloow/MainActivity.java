package thefloow.com.thefloow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.List;

import io.reactivex.functions.Consumer;
import thefloow.com.thefloow.constants.Constants;
import thefloow.com.thefloow.model.local.JourneyModel;
import thefloow.com.thefloow.services.LocationUpdatesService;
import thefloow.com.thefloow.util.Utils;
import thefloow.com.thefloow.views.journeys.JourneyActivity;
import thefloow.com.thefloow.views.main.viewmodel.MainViewModel;


public class MainActivity extends AppCompatActivity {


    private static final int REQ_CODE = 1000;
    private MapView mMapView;
    private LinearLayout panelDetails;
    private TextView journeyDetails;
    private Button btnJourneyDismiss;
    private FloatingActionButton btnTracking;
    private Intent serviceIntent;
    private LocationUpdatesService mService;
    private MainViewModel viewModel;
    private GoogleMap googleMap;
    private boolean mBound = false;
    private FloatingActionButton btnListJourney;
    private List<JourneyModel> selectedJourneyPoints;
    private int currentZoom = 15;
    private boolean shouldShowCurrentLocation = true;
    //private boolean shouldGoInBackground = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews(savedInstanceState);
        initViewModels();

    }

    @Override
    public void onResume() {
        super.onResume();
        initPermissions();
       //shouldGoInBackground = false;

    }

    private void initViews(Bundle savedInstanceState) {

        mMapView = findViewById(R.id.mapView);
        btnTracking = findViewById(R.id.btn_tracking_toggle);
        btnListJourney = findViewById(R.id.btn_list_journey);
        panelDetails = findViewById(R.id.ll_panel_details);
        journeyDetails = findViewById(R.id.tv_journey_detail);
        btnJourneyDismiss = findViewById(R.id.btn_journey_mode_dismiss);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        /**
         * On click dismiss map return to show user movements only and clear previous journey path
         */
        btnJourneyDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                panelDetails.setVisibility(View.GONE);
                shouldShowCurrentLocation = true;
                selectedJourneyPoints.clear();
                googleMap.clear();
            }
        });

        /**
         * Determine when app should track and record user movements or not
         */
        btnTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    mService.trackingToggle();
                }

               // shouldGoInBackground = true;

            }
        });

        /**
         * Button for list journeys saved
         */
        btnListJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //viewModel.getAllJourneys();
                Intent intent = new Intent(MainActivity.this, JourneyActivity.class);
                startActivityForResult(intent, REQ_CODE);
            }
        });


    }

    private void initPermissions() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean accept) throws Exception {
                if (accept) {
                    setupMap();
                    startServiceLocation();
                } else {
                    stopService();
                }
            }
        });
    }

    private void initViewModels() {

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCurrentLocationData().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                Log.d("test", "updates from activity: " + location.getLongitude());

                if (googleMap != null) {

                    /*
                    move camera on current location if journey path is not selected
                     */
                    if(shouldShowCurrentLocation) {
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
            }
        });

        viewModel.getMovementPoints().observe(this, new Observer<List<LatLng>>() {
            @Override
            public void onChanged(@Nullable List<LatLng> list) {
                drawLineCurrentMovement(list);
                if (selectedJourneyPoints != null && selectedJourneyPoints.size() > 0) {
                    drawLineSelectedJourney(selectedJourneyPoints);
                }
            }
        });

        viewModel.getSelectedJourney().observe(this, new Observer<List<JourneyModel>>() {
            @Override
            public void onChanged(@Nullable List<JourneyModel> list) {
                googleMap.clear();
                selectedJourneyPoints = list;
                shouldShowCurrentLocation = false;
                fillJourneyDetails();
                drawLineSelectedJourney(list);
                moveCamera(new LatLng(list.get(0).getLati(), list.get(0).getLongi()));
            }
        });

//        viewModel.getIsTracking().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean isTracking) {
//                /*
//                IF RECORDING APP SHOULD FINISH
//                 */
//                if(isTracking && shouldGoInBackground){
//                    finish();
//                }
//            }
//        });


        viewModel.getCurrentLocation();
        viewModel.getPointsForLine();
        //viewModel.doGetIsTracking();
    }



    private void setupMap() {

        if(googleMap == null) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    googleMap.setMyLocationEnabled(true);

                }
            });
        }
    }

    private void drawLineCurrentMovement(List<LatLng> list) {

        Log.d("test", "drawline current...");
        //Clear marker and polylines
        googleMap.clear();

        PolylineOptions options = new PolylineOptions().width(9).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < list.size(); i++) {
            LatLng point = list.get(i);
            options.add(point);
        }
        googleMap.addPolyline(options);
    }

    private void drawLineSelectedJourney(List<JourneyModel> list) {

        Log.d("test", "drawline journey...");

        PolylineOptions options = new PolylineOptions().width(9).color(Color.RED).geodesic(true);
        for (int i = 0; i < list.size(); i++) {
            LatLng point = new LatLng(list.get(i).getLati(), list.get(i).getLongi());
            options.add(point);
        }
        googleMap.addPolyline(options);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationUpdatesService.LoBinder binder = (LocationUpdatesService.LoBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void moveCamera(LatLng currentLocation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.latitude, currentLocation.longitude), getCurrentZoom()));


    }


    @Override
    protected void onPause() {
        super.onPause();
        stopService();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE) {
            String journeySelectedID = data.getStringExtra(Constants.JOURNEY_ID_EXTRA);
            Log.d("test", "selected journeyID : " + journeySelectedID);
            viewModel.getSelectedJourneys(journeySelectedID);
        }
    }

    private void startServiceLocation() {
            serviceIntent = new Intent(MainActivity.this, LocationUpdatesService.class);
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            startService(serviceIntent);

    }

    private void stopService() {

        if (serviceIntent != null && mBound) {
            unbindService(mConnection);
            //stopService(serviceIntent);
        }
    }




    private int getCurrentZoom() {
        return currentZoom;
    }

//    private void setCurrentZoom(int currentZoom) {
//        this.currentZoom = currentZoom;
//    }

    /**
     * Populate top panel if journey is selected
     */
    private void fillJourneyDetails(){

        if (selectedJourneyPoints.size() > 0) {
            String startTime = Utils.getTimeFromDate(selectedJourneyPoints.get(0).getJourneyDate());
            String stopTime = Utils.getTimeFromDate(selectedJourneyPoints.get(selectedJourneyPoints.size() - 1).getJourneyDate());
            String startDate = Utils.getStringDate(selectedJourneyPoints.get(0).getJourneyDate());
            String stopDate = Utils.getStringDate(selectedJourneyPoints.get(selectedJourneyPoints.size() - 1).getJourneyDate());
            Log.d("test", "startTime : " + startTime);
            Log.d("test", "stopTime : " + stopTime);
            journeyDetails.setText(startDate + " " + startTime + " - " + stopDate + " " + stopTime);
            panelDetails.setVisibility(View.VISIBLE);
        }
    }


}
