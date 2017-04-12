package com.example.christantia.googlemap;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
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
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.BottomSheetDialog;

import android.location.LocationManager;
import android.location.LocationListener;

import com.directions.route.AbstractRouting;
import com.directions.route.Segment;
import com.example.christantia.googlemap.data.LocationsContract;
import com.example.christantia.googlemap.data.LocationsDbHelper;
import com.example.christantia.googlemap.model.Destination;
import com.example.christantia.googlemap.model.MyGoogleAPI;
import com.example.christantia.googlemap.utilities.ObtainMapsData;
import com.google.android.gms.common.ConnectionResult;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback ,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LocationsDbHelper mDbHelper = new LocationsDbHelper(this);

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private SlidingLayer mSlidingLayer;
    private RelativeLayout directions;
    private Location mLastLocation;
    private MyGoogleAPI myGoogleAPI;

   /* public enum Toolbars {
        HOTELS(R.id.toolbarTop), EATERIES(R.id.toolbarTop), PARK(R.id.toolbarTop), YAHOO(20), ATT(25);
        private int value;

        private Toolbars(int value) {
            this.value = value;
        }
    }*/

    private final static String TAG = "Debug";
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
    private Destination tobeShownonMap;
    private Destination toPutonPlan;
    int i = 0;
    ArrayList<Destination> infoHawkers = new ArrayList<Destination>();
    ArrayList<Destination> infoHotels = new ArrayList<Destination>();
    ArrayList<Destination> infoParks = new ArrayList<Destination>();
    ArrayList<Destination> infoMuseums = new ArrayList<Destination>();
    ArrayList<Destination> infoSports = new ArrayList<Destination>();


    //shelina's
    private Button newButton;

    private static int buttonPlanId = 0;

    private static int bottomSheetId = 0;

    private ArrayList<DestinationItem> destinationList = new ArrayList<>();
    private PlanListAdapter destinationAdapter;
    private DynamicListView destinationListView;
    static public ArrayList<DestinationItem> ids = new ArrayList<DestinationItem>();

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loading = new ProgressDialog(this);

        loading.setMessage("Loading data from internet... Please wait.");
        loading.setIndeterminate(true);
        loading.setCanceledOnTouchOutside(false);


        new FetchData().execute(this);


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

        //mgoogleapiclient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
        /*LinearLayout list = (LinearLayout) findViewById(R.id.destination_list);

        //add new list item programatically + code the buttons
        addNewItemInList(list, "Name", "Address"); //dummy content
*/

        //initialize list first and show using this iteration
        /*for (DestinationInfo cur : infos)
            addNewItemInList(list, cur.getName(), cur.getAddress());
*/
        //initalize buttons
        hotel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToMap();
                LinearLayout list = (LinearLayout) findViewById(R.id.destination_list);
                list.removeAllViews();
                for (Destination cur : infoHotels) {
                    addNewItemInList(list, cur);
                }
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

        RelativeLayout planTab = (RelativeLayout) findViewById(R.id.plan_bar);

        planTab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View replace = findViewById(R.id.plan_bar);
                ViewGroup parent = (ViewGroup) replace.getParent();
                int index = parent.indexOfChild(replace);
                parent.removeView(replace);
                replace = getLayoutInflater().inflate(R.layout.plan_bar, parent, false);
                parent.addView(replace, index);
            }
        });

    }

    public void populateList() {
        System.out.println("POPULATE ANJENG");
        // TODO: jadiin dao
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor resultSet = db.rawQuery("SELECT * FROM " + LocationsContract.LocationsEntry.TABLE_NAME, null);
        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            int id = resultSet.getInt(0);
            String name = resultSet.getString(1);
            String category = resultSet.getString(2);
            String coordinates = resultSet.getString(3);
            String address = resultSet.getString(4);

            Destination result = new Destination (id,Double.parseDouble(coordinates.split(",")[0]),
                    Double.parseDouble(coordinates.split(",")[1]),address,name,category);

            if (category.equals("HAWKERCENTRE"))
                infoHawkers.add(result);
            else if (category.equals("HOTELS"))
                infoHotels.add(result);
            else if (category.equals("NATIONALPARKS"))
                infoParks.add(result);
            else if (category.equals("MUSEUM"))
                infoMuseums.add(result);
            else if (category.equals("PLAYSG"))
                infoSports.add(result);

            System.out.println("CATEGORY ANJENG " + category);

            resultSet.moveToNext();
        }
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
                        || directions.getVisibility() == View.VISIBLE) {
                    returnToMap();
                    return true;
                } else {
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

    //only run when last location is known
    @Override
    public void onConnected(Bundle connectionHint) {
       // for(int i=1;i<=2123123123;i++){int a=0;a=a+1;}
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("ANJENG connected mlast");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        }
        if(mLastLocation != null)System.out.println("ANJENG mlast != null");
        LatLng singapore = new LatLng(1.3521, 103.8198);
        LatLng current=singapore;
        if(mLastLocation != null)
            current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("i'm here marker in Singapore"));
        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (updatedLng != null)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 10.0f));
    }

    @Override
    public void onConnectionSuspended(int a)
    {
        System.out.println("connection suspended !");
    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mGoogleApiClient.connect();
        Log.d(TAG, "inside map ready");
        mMap = googleMap;
        mMap.setPadding(0, 0, 0, 150);
        mUiSettings = (UiSettings) mMap.getUiSettings();
        myGoogleAPI = new MyGoogleAPI(MapsActivity.this,this,mMap);
        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        //mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//            @Override
//            public void onMyLocationChange(Location arg0) {
//                updatedLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
//                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
//
//            }
//        });


        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        // Add a marker in Sydney and move the camera

        LatLng singapore = new LatLng(1.3521, 103.8198);
        LatLng current=singapore;

        if(mLastLocation != null)
            current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("i'm here marker in Singapore"));
        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (updatedLng != null)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 10.0f));
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
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
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

    private boolean addNewItemInList(LinearLayout list, final Destination cur) {
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.HORIZONTAL);
        DestinationListView view = new DestinationListView(this, cur);
        view.setBackgroundColor(0xffe6ffd8);
        view.setLayoutParams(new LinearLayout.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
        a.addView(view);
        View plus = view.findViewById(DestinationListView.PLUS_INT);
        plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered plus");
                returnToMap();
                toPutonPlan = cur;
                return true;
            }
        });
        View arrow = view.findViewById(DestinationListView.ARROW_INT);
        arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered arrow");
                returnToMap();
                tobeShownonMap = cur;
                showDirectionOnMap(tobeShownonMap);
                return true;
            }
        });
        list.addView(a);
        return false;
    }

    private void showDirectionOnMap(final Destination tobeShownonMap){
        System.out.println("NEED ROUTING ANJENG location ="+mLastLocation.getLatitude()+
        " ,"+mLastLocation.getLongitude() );
        System.out.println("NEED ROUTING destination ="+tobeShownonMap.getLatitude()+
                " ,"+tobeShownonMap.getLongitude() );
        myGoogleAPI.displayRoute(tobeShownonMap,mLastLocation,
                AbstractRouting.TravelMode.DRIVING,
                new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                new LatLng(tobeShownonMap.getLatitude(),tobeShownonMap.getLongitude()));
        //initialize relative layout
        View back = (View) findViewById(R.id.back_distance);
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

        View walking = findViewById(R.id.walking);
        View publicTrans = findViewById(R.id.publictrans);
        View car = findViewById(R.id.car);
        //add different array according to option clicked
        walking.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGoogleAPI.displayRoute(tobeShownonMap,mLastLocation,
                        AbstractRouting.TravelMode.WALKING,
                        new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                        new LatLng(tobeShownonMap.getLatitude(),tobeShownonMap.getLongitude()));
                return true;
            }
        });
        publicTrans.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGoogleAPI.displayRoute(tobeShownonMap,mLastLocation,
                        AbstractRouting.TravelMode.TRANSIT,
                        new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                        new LatLng(tobeShownonMap.getLatitude(),tobeShownonMap.getLongitude()));
                return true;
            }
        });
        car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGoogleAPI.displayRoute(tobeShownonMap,mLastLocation,
                        AbstractRouting.TravelMode.DRIVING,
                        new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                        new LatLng(tobeShownonMap.getLatitude(),tobeShownonMap.getLongitude()));return true;
            }
        });


        directions.setVisibility(View.VISIBLE);
    }

    private void returnToMap() {

        if (mSlidingLayer.isOpened()) {
            mSlidingLayer.closeLayer(true);
        }
        mSlidingLayer.setVisibility(View.INVISIBLE);
        toolbarTop.setVisibility(View.INVISIBLE);
        searchBar.setVisibility(View.INVISIBLE);
        directions.setVisibility(View.INVISIBLE);
    }


    private void setClicks(String type) {
        if (mSlidingLayer.getVisibility() == View.INVISIBLE) {
            titleBar.setText(type);
            toolbarTop.setVisibility(View.VISIBLE);
            mSlidingLayer.setVisibility(View.VISIBLE);
        } else {
            mSlidingLayer.setVisibility(View.INVISIBLE);
            toolbarTop.setVisibility(View.INVISIBLE);
        }
    }

    public void generateDestinationList() {
        DestinationItem item1 = new DestinationItem("destination1", "123", "456");
        DestinationItem item2 = new DestinationItem("destination2", "789", "123");
        DestinationItem item3 = new DestinationItem("destination3", "456", "789");
        DestinationItem item4 = new DestinationItem("destination4", "123", "789");
        DestinationItem item5 = new DestinationItem("destination5", "456", "123");
        destinationList.add(item1);
        destinationList.add(item2);
        destinationList.add(item3);
        destinationList.add(item4);
        destinationList.add(item5);
    }

    public void openTab(View view) {

        returnToMap();

        this.generateDestinationList();

        bottomSheetId = view.getId();
        ViewGroup parent = (ViewGroup) findViewById(R.id.plan_bar_new);
        Button btnToSheet = (Button) parent.findViewById(bottomSheetId);

        destinationAdapter =
                new PlanListAdapter(this,
                        R.layout.destination_list,
                        R.id.destination_name,
                        destinationList
                );
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View bottomSheetView = null;

        if (btnToSheet != null) {

            if (btnToSheet.equals((Button) findViewById(R.id.plan1))) {
                destinationListView = new DynamicListView(this);
                destinationListView.setDestinationList(destinationList);
                destinationListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                destinationListView.setAdapter(destinationAdapter);
                destinationListView.setPadding(0,0,0,10);
                destinationListView.setClipToPadding(false);

                bottomSheetView = inflater.inflate(R.layout.plan_window_1, null);

                LinearLayout ll = (LinearLayout) bottomSheetView.findViewById(R.id.destination);
                Button replace = (Button) ll.findViewById(R.id.btn_save);
                ll.removeView(replace);
                ll.addView(destinationListView);
                ll.addView(replace);

                destinationListView.setOnTouchListener(new View.OnTouchListener() {
                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });

               ImageButton delete = (ImageButton) bottomSheetView.findViewById(R.id.delete1);

                if (delete != null) {
                    delete.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (ids.size() > 0) {
                                for (int i = 0; i < ids.size(); i++) {
                                    destinationList.remove(ids.get(i));
//                                  items.remove(items.get(adapter.getItemViewType(ids.get(i))));
//                                  items.remove(adapter.getItemViewType(ids.get(i)));
                                }
                                destinationAdapter.notifyDataSetChanged();
                            }
                        }
                    });
               }
            }
            else {
                switch (bottomSheetId) {
                 case 1:
                    bottomSheetView = inflater.inflate(R.layout.bottom_sheet_2, null);
                    break;
                 case 2:
                    bottomSheetView = inflater.inflate(R.layout.bottom_sheet_3, null);
                    break;
                 case 3:
                    bottomSheetView = inflater.inflate(R.layout.bottom_sheet_4, null);
                    break;
                }
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        mBehavior.setPeekHeight(500);

        bottomSheetDialog.show();
    }

    public void newPlan(View view){
        View addPlan = findViewById(R.id.new_plan);
        ViewGroup parent = (ViewGroup) addPlan.getParent();
        int index = parent.indexOfChild(addPlan);
        Button template = (Button) findViewById(R.id.plan1);
        ViewGroup.LayoutParams params = template.getLayoutParams();

        if(buttonPlanId<3) {
            parent.removeView(addPlan);
            newButton = new Button(this);
            newButton.setLayoutParams(params);
            /*
            newButton.setBackgroundColor(0x27ae60);
            newButton.setAllCaps(true);
            newButton.setTextColor(0x2c3e50);
            newButton.setTypeface(Typeface.DEFAULT_BOLD);
            */
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTab(v);
                }
            });

            buttonPlanId ++;

            newButton.setId(buttonPlanId);
            String buttonText = "Plan " + (buttonPlanId+1);
            newButton.setText(buttonText);
            parent.addView(newButton);
            if(buttonPlanId!=3) parent.addView(addPlan);
            if(buttonPlanId==3) buttonPlanId=0;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("connection Failed!");
    }

    private class FetchData extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... contexts) {
            ObtainMapsData.saveAllKmlToDb(getApplicationContext(), mDbHelper);
            populateList();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.hide();
        }
    }
}
