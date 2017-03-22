package com.example.christantia.googlemap;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Christantia on 3/20/2017.
 */

public class DrawerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final static String TAG="Debug";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        //setContentView(R.layout.activity_maps);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        getSupportActionBar().hide();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LinearLayout list = (LinearLayout) findViewById(R.id.destination_list);

        //add new list item programatically + code the buttons
        View[] newItem = addNewItemInList(list, R.drawable.search, "Name", "Address", "Opening Hours");
        Log.d(TAG, "AIYO " + newItem.toString());
        newItem[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered");
                //return to MapsActivity
                return false;
            }
        });
        newItem[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered2");
                //return to MapsActivity
                return false;
            }
        });
        /*addNewItemInList(list, R.drawable.search, "Name", "Address", "Opening Hours");
        addNewItemInList(list, R.drawable.search, "Name", "Address", "Opening Hours");
        addNewItemInList(list, R.drawable.search, "Name", "Address", "Opening Hours");*/


    }

    private View[] addNewItemInList(LinearLayout list, int pictureId, String name, String address, String openingHours){
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        DestinationInfo info = new DestinationInfo(pictureId, name, address,openingHours);
        DestinationListView view = new DestinationListView(this, info);
        view.setBackgroundColor(0xffe6ffd8);
        view.setLayoutParams(new LinearLayout.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
        a.addView(view);
        list.addView(a);
        View[] returned = new View[2];
        returned[0] = ((ViewGroup)view).getChildAt(2);
        returned[1] = ((ViewGroup)view).getChildAt(3);
        return returned;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMyLocationEnabled(true);


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 13.0f));
            }
        });


        //mMap.setOnMyLocationButtonClickListener(this);



        // Add a marker in Sydney and move the camera

        /*LatLng singapore = new LatLng(1.3521, 103.8198);
        mMap.addMarker(new MarkerOptions().position(singapore).title("Marker in Singapore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(singapore));*/
    }


}
