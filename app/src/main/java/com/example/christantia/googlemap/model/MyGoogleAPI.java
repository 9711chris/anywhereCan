package com.example.christantia.googlemap.model;

/**
 * Created by Christantia on 4/12/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.Segment;
import com.example.christantia.googlemap.MapsActivity;
import com.example.christantia.googlemap.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ervin on 09/04/2017.
 */

public class MyGoogleAPI implements com.directions.route.RoutingListener{
    private List<Polyline> polylines = new ArrayList<>();
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    Destination to;
    Location now;
    LatLng start;
    LatLng end;
    Context c;
    ArrayList<Route> route;
    int shortestIndex;
    Activity ac;

    public MyGoogleAPI(Context context,Activity activity, GoogleMap mMap) {
        c = context;
        this.ac = activity;
        this.mMap = mMap;
    }

    public void clear() {
        for (Polyline line: polylines) {
            line.remove();
        }

        polylines = new ArrayList<>();
    }

    public void displayRoute(Destination to,
                             Location now,
                             AbstractRouting.TravelMode travelMode,
                             LatLng... waypoints) {
        this.to = to;
        this.now = now;
        this.start = waypoints[0];
        this.end = waypoints[waypoints.length - 1];

        progressDialog = ProgressDialog.show(c, "Please wait.",
                "Fetching route information.", true);

        Routing routing = new Routing.Builder()
                .travelMode(travelMode)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(false)
                .key("AIzaSyAkybgNh0YVABRYGmg6jNrrXs5lTsIH800")
                .waypoints(waypoints)
                .build();
        routing.execute();
        //while(!done);
        progressDialog.dismiss();
        System.out.println("CLEAR ANJENGG COEG MyGoogleAPI!");
        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        mMap.addMarker(options);

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        this.route = route;
        this.shortestIndex = shortestRouteIndex;
        System.out.println("DISMISS ANJENGGG");
        //progressDialog.dismiss();
        System.out.println("KENAPA GA KEDISMISSS :'(((( ANJENGGG");
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
            // System.out.println("MANUVER ANJENG " + segment.getManeuver() );
        }

        Toast.makeText(c.getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
//        }


        showDirectionOnMap(to,now,route.get(i).getSegments());
        System.out.println("ANJENGG SUCCESS COEG MyGoogleAPI!");

    }

    private void showDirectionOnMap(final Destination tobeShownonMap,
                                    Location mLastLocation,
                                    List<Segment> segments) {
        //process address string through geolocation + show onmap
        final RelativeLayout directions = (RelativeLayout) ac.findViewById(R.id.directions);


        //list out directions
        final LinearLayout directionList = (LinearLayout) ac.findViewById(R.id.directionList);

        //create 3 arrays, each for
        //iterate through direction list create textviews in array
        final TextView tv = new TextView(ac); //dummy content
        tv.setTextSize(20);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        layout.setMargins(10, 10, 0, 0);
        tv.setLayoutParams(layout);
        tv.setMovementMethod(new ScrollingMovementMethod());
        directionList.removeAllViews();
        String tempo = "";
        for(Segment segment:segments){

            tempo += segment.getInstruction()+"\n";

        }
        tv.setText(tempo);
        directionList.addView(tv);

    }

    public void onRoutingFailure(RouteException e){
        System.out.println(e + " ANJENG !");
        System.out.println("PRAB ANJENG , FAIL COEG !");
    }

    public void onRoutingStart(){
        System.out.println("START WOE PRAB ANJENG MyGoogleAPI!");
    }

    public void onRoutingCancelled(){
        System.out.println("CANCELED ANJENG !");
    }

}