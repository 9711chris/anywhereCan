package com.example.christantia.googlemap;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationManager;
import android.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wunderlist.slidinglayer.LayerTransformer;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;
import com.wunderlist.slidinglayer.transformer.RotationTransformer;
import com.wunderlist.slidinglayer.transformer.SlideJoyTransformer;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private SlidingLayer mSlidingLayer;
    private RelativeLayout directions;

   /* public enum Toolbars {
        HOTELS(R.id.toolbarTop), EATERIES(R.id.toolbarTop), PARK(R.id.toolbarTop), YAHOO(20), ATT(25);
        private int value;

        private Toolbars(int value) {
            this.value = value;
        }
    }*/

    private final static String TAG="Debug";
    private LocationManager locationManager;
    private boolean mPermissionDenied = false;
    private final static int DRAWER_REQCODE = 1;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private View search;
    private View searchBar;
    private View hotel;
    private View eateries;
    private View park;
    private View museum;
    private View sport;
    private Toolbar toolbarTop;
    private TextView titleBar;
    private LinearLayout hotelLayer;
    private LatLng updatedLng;
    AlertDialog.Builder builder;
    private String tobeShownonMap;
    private String toPutonPlan;
    int i = 0;
    ArrayList<DestinationInfo> infos = new ArrayList<DestinationInfo>();
    ArrayList<View[]> infoItem = new ArrayList<View[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 300, 300);
        }
        /*if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/
        bindViews();
        initState();


        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (searchBar.getVisibility() == View.INVISIBLE) {
                    searchBar.setVisibility(View.VISIBLE);
                } else {
                    searchBar.setVisibility(View.INVISIBLE);
                }
            }
        });


        //initialize back key
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit the application?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
            }
        });


        //initialize lists
        LinearLayout list = (LinearLayout) findViewById(R.id.destination_list);

        //add new list item programatically + code the buttons
        addNewItemInList(list, R.drawable.search, "Name", "Address", "Opening Hours"); //dummy content

        //initialize list first and show using this iteration
        /*for (DestinationInfo cur : infos)
            addNewItemInList(list, cur.getPictureId(), cur.getName(), cur.getAddress(), cur.getOpeningHour());*/

        //initalize buttons
        hotel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                //intialize(1);
                //resetslidinglayer;
                setClicks("Hotels");
            }
        });
        //plan : list
        eateries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                //intialize(2);
                setClicks("Eateries");

            }
        });
        park.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                setClicks("Parks");
            }
        });
        museum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                setClicks("Museums");
            }
        });
        sport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                setClicks("Sports");
            }
        });


    }

    private void bindViews() {
        //Sliding layer for destination lists
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        //View for direction rundown
        directions = (RelativeLayout) findViewById(R.id.directions);
        //Search feature
        search = findViewById(R.id.search);
        searchBar = findViewById(R.id.toolbarSearch);
        //Menu
        hotel = findViewById(R.id.hotel);
        eateries = findViewById(R.id.eateries);
        park = findViewById(R.id.park);
        museum = findViewById(R.id.museum);
        sport = findViewById(R.id.sport);

        //Destination Lists' title
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        titleBar = (TextView) findViewById(R.id.titleBar);
    }

    private void initState() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSlidingLayer.getLayoutParams();
        mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_TOP);
        mSlidingLayer.setLayoutParams(rlp);
        mSlidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);

        mSlidingLayer.setOffsetDistance(getResources().getDimensionPixelOffset(R.dimen.offset_distance));
        mSlidingLayer.setPreviewOffsetDistance(getResources().getDimensionPixelOffset(R.dimen.preview_offset_distance));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "in back onkeydown");
                if (toolbarTop.getVisibility() == View.VISIBLE
                        || searchBar.getVisibility() == View.VISIBLE
                        || directions.getVisibility()==View.VISIBLE) {
                    returnToMap();
                    return true;
                }
                else {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            default:
                Log.d(TAG, "in default onkeydown");
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mGoogleApiClient.connect();
        Log.d(TAG, "inside map ready");
        mMap = googleMap;

        mUiSettings = (UiSettings) mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                updatedLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));

            }
        });


        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        // Add a marker in Sydney and move the camera

        LatLng singapore = new LatLng(1.3521, 103.8198);
        //LatLng current = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        //mMap.addMarker(new MarkerOptions().position(singapore).title("Marker in Singapore"));
        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (updatedLng != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLng, 13.0f));
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    private boolean addNewItemInList(LinearLayout list, int pictureId, String name, final String address, String openingHours){
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        DestinationInfo info = new DestinationInfo(pictureId, name, address,openingHours);
        DestinationListView view = new DestinationListView(this, info);
        view.setBackgroundColor(0xffe6ffd8);
        view.setLayoutParams(new LinearLayout.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
        a.addView(view);
        list.addView(a);
        View plus = ((ViewGroup)view).getChildAt(2);
        plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered");
                returnToMap();
                toPutonPlan = address;
                return true;
            }
        });
        View arrow = ((ViewGroup)view).getChildAt(3);
        arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered");
                returnToMap();
                tobeShownonMap = address;
                showDirectionOnMap(tobeShownonMap);
                return true;
            }
        });
        return false;
    }

    private void returnToMap(){

        if (mSlidingLayer.isOpened()) {
            mSlidingLayer.closeLayer(true);
        }
        mSlidingLayer.setVisibility(View.INVISIBLE);
        toolbarTop.setVisibility(View.INVISIBLE);
        searchBar.setVisibility(View.INVISIBLE);
        directions.setVisibility(View.INVISIBLE);
    }

    private void showDirectionOnMap(final String tobeShownonMap){
        //process address string through geolocation + show onmap

        //initialize relative layout
        View back = (View) findViewById(R.id.back);
        View addPlan = (View) findViewById(R.id.addPlan);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                directions.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        addPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toPutonPlan = tobeShownonMap;
                return true;
            }
        });

        //list out directions
        final LinearLayout directionList = (LinearLayout) findViewById(R.id.directionList);
        View walking = findViewById(R.id.walking);
        View publicTrans = findViewById(R.id.publictrans);
        View car = findViewById(R.id.car);
        //create 3 arrays, each for
        //iterate through direction list create textviews in array
        final TextView tv = new TextView(this); //dummy content
        tv.setTextSize(20);
        tv.setText("Direction");
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        layout.setMargins(10,10,0,0);
        tv.setLayoutParams(layout);
        //add different array according to option clicked
        walking.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(tv.getParent()!=null)
                    ((ViewGroup)tv.getParent()).removeView(tv); //
                directionList.addView(tv);
                return true;
            }
        });
        publicTrans.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        directions.setVisibility(View.VISIBLE);
    }

    private void setClicks(String type){
        if (mSlidingLayer.getVisibility() == View.INVISIBLE) {
            titleBar.setText(type);
            toolbarTop.setVisibility(View.VISIBLE);
            switch (type){
                case "Hotels":
                    mSlidingLayer.setVisibility(View.VISIBLE);
                    break;
                case "Eateries":
                    mSlidingLayer.setVisibility(View.VISIBLE);
                    break;
                case "Parks":
                    mSlidingLayer.setVisibility(View.VISIBLE);
                    break;
                case "Museums":
                    mSlidingLayer.setVisibility(View.VISIBLE);
                    break;
                case "Sports":
                    mSlidingLayer.setVisibility(View.VISIBLE);
                    break;
            }
        }
        else {
            mSlidingLayer.setVisibility(View.INVISIBLE);
            toolbarTop.setVisibility(View.INVISIBLE);
        }
    }

}
