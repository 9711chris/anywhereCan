package com.example.ervin.google_map_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.Segment;
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
    LatLng start, end;
    Context c;

    public MyGoogleAPI(Context context, GoogleMap mMap) {
        c = context;
        this.mMap = mMap;
    }

    public void clear() {
        for (Polyline line: polylines) {
            line.remove();
        }

        polylines = new ArrayList<>();
    }

    public void displayRoute(LatLng... waypoints) {
        this.start = waypoints[0]; this.end = waypoints[waypoints.length - 1];

        progressDialog = ProgressDialog.show(c, "Please wait.",
                "Fetching route information.", true);
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(false)
                .key("AIzaSyDbDFLbD2IPeK4V0SxBD3NH1mcqoq9UQzE")
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
            System.out.println("INSTRUKSI ANJING " + segment.getInstruction() );
            // System.out.println("MANUVER ANJING " + segment.getManeuver() );
        }

        Toast.makeText(c.getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
//        }



        System.out.println("ANJENGG SUCCESS COEG MyGoogleAPI!");

    }

    public void onRoutingFailure(RouteException e){
        System.out.println(e + "ANJENG !");
        System.out.println("PRAB ANJENG , FAIL COEG !");
    }

    public void onRoutingStart(){
        System.out.println("START WOE PRAB ANJENG MyGoogleAPI!");
    }

    public void onRoutingCancelled(){
        System.out.println("CANCELED ANJENG !");
    }

}
