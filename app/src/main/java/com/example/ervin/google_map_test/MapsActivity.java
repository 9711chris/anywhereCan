package com.example.ervin.google_map_test;

        import android.app.ProgressDialog;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;

        import com.directions.route.AbstractRouting;
        import com.directions.route.Route;
        import com.directions.route.RouteException;
        import com.directions.route.Routing;
        import com.directions.route.RoutingListener;
        import com.directions.route.Segment;
        import com.example.ervin.google_map_test.DB.LocationsDbHelper;
        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.Polyline;
        import com.google.android.gms.maps.model.PolylineOptions;

        import java.util.ArrayList;
        import java.util.List;

        import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, com.directions.route.RoutingListener {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    protected LatLng start;
    protected LatLng end;
    private List<Polyline> polylines = new ArrayList<>();
    private static final int[] COLORS = new int[]{123123123, 234234234, 345345345};
    LocationsDbHelper dbHelper = new LocationsDbHelper(this);

    //SQLiteDatabase mydatabase = openOrCreateDatabase("mydatabase", MODE_PRIVATE, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MyDBInterface mydb = new MySQLiteDB(dbHelper);
        Plan temp = mydb.retrievePlan(1);
        System.out.println("id = "+temp.getId());

        System.out.println("MULAI NYIMPEN JING!!!");
/*
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Plan(pid INTEGER, did INTEGER, seq INTEGER);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Destination(did INTEGER, name VARCHAR, lat FLOAT, lng FLOAT, type VARCHAR);");
        mydatabase.execSQL("INSERT INTO myTable VALUES (1, 'asdf');");
*/
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        start = new LatLng(1.351011, 103.679701); // ntu
        end = new LatLng(1.297285, 103.778429); // nus
        LatLng changi = new LatLng(1.348841, 103.986002);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.addMarker(new MarkerOptions().position(ntu).title("Marker in ntu"));
        // mMap.addMarker(new MarkerOptions().position(nus).title("Marker in nus"));
        System.out.println("SEBELUM ANJENGG COEG !");

        MyGoogleAPI myGoogleAPI = new MyGoogleAPI(this, mMap);
        myGoogleAPI.displayRoute(start, changi, end);
        myGoogleAPI.displayRoute(start, end, changi);

        /*
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .key("AIzaSyDbDFLbD2IPeK4V0SxBD3NH1mcqoq9UQzE")
                .waypoints(start, changi, end)
                u.build();
        */

        // routing.execute();
        System.out.println("CLEAR ANJENGG COEG !");
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(star));

    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);


        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
    //    for (int i = 0; i <route.size(); i++) {

        //In case of more than 5 alternative routes
        int i = shortestRouteIndex;
        int colorIndex = i % 10;

        PolylineOptions polyOptions = new PolylineOptions();
        //polyOptions.color(getResources().getColor(COLORS[colorIndex]));
        polyOptions.width(10 + i * 3);
        polyOptions.addAll(route.get(i).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);

        for (Segment segment: route.get(i).getSegments()) {
            System.out.println("INSTRUKSI ANJENG " + segment.getInstruction() );
            // System.out.println("MANUVER ANJING " + segment.getManeuver() );
        }

        Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
//        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        mMap.addMarker(options);

        System.out.println("ANJENGG SUCCESS COEG !");

    }

    public void onRoutingFailure(RouteException e){
        System.out.println(e + "ANJENG !");
        System.out.println("PRAB ANJENG , FAIL COEG !");
    }

    public void onRoutingStart(){
        System.out.println("START WOE PRAB ANJENG !");
    }

    public void onRoutingCancelled(){
        System.out.println("CANCELED ANJENG !");
    }
}
