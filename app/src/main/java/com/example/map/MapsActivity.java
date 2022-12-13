package com.example.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Map;

import static com.example.map.my.location;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,TaskLoadedCallback {

    LinearLayout main_menu;
    LinearLayout main_menu_help;
    static GoogleMap mMap;
    static MapsActivity thisactivity;

    static Marker gl_marker;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        main_menu = findViewById(R.id.main_menu);
        main_menu_help = findViewById(R.id.main_menu_help);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    List<LatLng> ll_list;
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

        currentPolyline.setColor(Color.GREEN);
        currentPolyline.setWidth(20);

        double km = 0;
        double km_out = 0;
        ll_list = currentPolyline.getPoints();

        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap bigMarker = Bitmap.createScaledBitmap(b, 90, 180, false);
        gl_marker.setIcon(BitmapDescriptorFactory.fromBitmap(bigMarker));
        gl_marker.setFlat(true);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(gl_marker.getPosition())
                .zoom(15)
                .tilt(75).bearing(gl_marker.getRotation())

                .build()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ll_list.size() - 1; i++) {
                    Location l1 = new Location("l1");
                    Location l2 = new Location("l2");

                    l1.setLatitude(ll_list.get(i).latitude);
                    l1.setLongitude(ll_list.get(i).longitude);

                    l2.setLatitude(ll_list.get(i + 1).latitude);
                    l2.setLongitude(ll_list.get(i + 1).longitude);


                 //   km += l1.distanceTo(l2) / 1000;

                    long dur = 0;
                    float km = l1.distanceTo(l2);
                    if(km<10){
                        dur = 500;
                    }
                    else if(km>10 && km<30){
                        dur = 1000;
                    }
                    else{
                        dur = 3000;
                    }
                    long finalDur = dur;
                    MapsActivity.thisactivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float r = (float)bearingBetweenLocations(new LatLng(l1.getLatitude(), l1.getLongitude()),new LatLng(l2.getLatitude(),l2.getLongitude()));
                            if(r<0)
                                r+=360;

                            MapsActivity.thisactivity.animateMarker(l2,r, finalDur);
                        }
                    });
                    try {
                        Thread.sleep(dur+175);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MapsActivity.thisactivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<LatLng> a = currentPolyline.getPoints();
                            a.remove(0);
                            currentPolyline.setPoints(a);
                        }
                    });

                }
            }
        }).start();

    }
    
    static String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBg7y7QGcrn2D4Q-D5hZfRJ7GOztM3cplQ";
        return url;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        //mMap.setMinZoomPreference(15f);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);
        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 45, 90, false);
        if (location != null){
            gl_marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            MapsActivity.gl_marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            MapsActivity.mMap.animateCamera(cameraUpdate);
        }
        else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                my.location =location;
                                // Logic to handle location object
                                gl_marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                                    MapsActivity.gl_marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
                                    MapsActivity.gl_marker.setVisible(true);
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                                    MapsActivity.mMap.animateCamera(cameraUpdate);

                            }
                        }
                    });
        }



        thisactivity = this;
        mMap.setTrafficEnabled(true);

    }

    public void animateMarker(Location location,float toRotation,long dur) {
        final Marker marker = gl_marker;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);



        final long duration = dur;

        final Interpolator interpolator = new LinearInterpolator();
        //marker.setRotation(toRotation);

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed /
                        duration);
                double lng = t * location.getLongitude() + (1 - t) *
                        startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t) *
                        startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));

                final float startRotation = marker.getRotation();
                float rot = t * toRotation + (1 - t) * startRotation;

                float bearing =  -rot > 180 ? rot / 2 : rot;

                marker.setRotation(bearing);
                if(my.order!=null){
                    Projection projection = mMap.getProjection();
                    LatLng markerPosition = marker.getPosition();
                    Point markerPoint = projection.toScreenLocation(markerPosition);
                    Point targetPoint = new Point(markerPoint.x, markerPoint.y+ 400- findViewById(R.id.map).getHeight() / 2);
                    LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(targetPosition)
                            .zoom(18)
                            .tilt(45).bearing(bearing)

                            .build()), 50,null);
                }


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 25);
                }
                //else
                    //marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void main_menu_on_click(View view) {
        main_menu.setVisibility(View.VISIBLE);
        main_menu_help.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(main_menu.getVisibility()==View.VISIBLE){
            main_menu.setVisibility(View.GONE);
            main_menu_help.setVisibility(View.GONE);
            ImageView imageView = findViewById(R.id.main_menu_icon);
            imageView.setVisibility(View.VISIBLE);
        }
    }


    public void hide_menu(View view) {
        onBackPressed();
    }


    public void okno_2(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_2.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_3(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_3.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_4(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_4.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_5(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_5.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_6(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_6.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_7(View view) {
        Intent intent = new Intent(MapsActivity.this, okno_7.class);
        startActivity(intent);
        onBackPressed();
    }

    public void okno_8(View view) {
        server.close();
        stopService(MyService.th);

        finish();
    }

    public void faol_click(View view) {
        LinearLayout linearLayout = findViewById(R.id.faol_linear);
        linearLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
        linearLayout = findViewById(R.id.band_linear);
        linearLayout.setBackgroundColor(Color.WHITE);
        TextView textView = findViewById(R.id.faol_text);
        textView.setTextColor(Color.WHITE);
        textView = findViewById(R.id.band_text);
        textView.setTextColor(Color.BLACK);
        my.status="faol";
        server.send("update|"+my.status+"|"+ location.getLatitude()+"|"+ location.getLongitude(),true);
    }

    public void band_click(View view) {
        LinearLayout linearLayout = findViewById(R.id.faol_linear);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout = findViewById(R.id.band_linear);
        linearLayout.setBackgroundColor(Color.RED);
        TextView textView = findViewById(R.id.faol_text);
        textView.setTextColor(Color.BLACK);
        textView = findViewById(R.id.band_text);
        textView.setTextColor(Color.WHITE);
        my.status="band";
        server.send("update|"+my.status+"|"+ location.getLatitude()+"|"+ location.getLongitude(),true);
    }

    public void daromad_menu(View view) {

    }

    public void zoom_plus(View view) {
        mMap.animateCamera( CameraUpdateFactory.zoomTo( mMap.getCameraPosition().zoom+1f) );
    }

    public void zoom_minus(View view) {
        mMap.animateCamera( CameraUpdateFactory.zoomTo( mMap.getCameraPosition().zoom-1f) );
        /*
        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap bigMarker = Bitmap.createScaledBitmap(b, 90, 180, false);
        gl_marker.setIcon(BitmapDescriptorFactory.fromBitmap(bigMarker));
        gl_marker.setFlat(true);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
        .target(gl_marker.getPosition())
.zoom(17)
                .tilt(45).bearing(gl_marker.getRotation())

                .build()));
         */

    }

    public void where_i_am(View view) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

    }


    public void get_info_order(View view) {
        View v = findViewById(R.id.info);
        if(v.getVisibility()==View.VISIBLE)
            v.setVisibility(View.GONE);
        else
            v.setVisibility(View.VISIBLE);
    }

    public void poexali(View view) {
        //Поехали к клиенту

    }
}