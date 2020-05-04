package com.example.wificar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mgoogleMap;
    private SearchView searchView;
    private TextView Latitude, Longitude;
    private RelativeLayout relativeLayout;
    Button btn_ok;
    private Location currentlocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng current_latlng;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchView = findViewById(R.id.search_location);
        Latitude = findViewById(R.id.lat);
        Longitude = findViewById(R.id.log);
        btn_ok= findViewById(R.id.ok);
        relativeLayout= findViewById(R.id.bottom);
        relativeLayout.setVisibility(View.GONE);
        imageView = findViewById(R.id.mylocation);


        Places.initialize(getApplicationContext(),"AIzaSyAb2hrgcqm9wXbuIDAT6zhMsHm2zRjw3CU");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this,AutoModeActivity.class);
                startActivity(i);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_latlng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(current_latlng).title("Your Location").snippet("Start Point");
                mgoogleMap.setMyLocationEnabled(true);

                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLng(current_latlng));
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_latlng,15));
                mgoogleMap.addMarker(markerOptions);
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLashLocation();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String st_location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(st_location != null || !st_location.isEmpty() || !st_location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList =geocoder.getFromLocationName(st_location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("T", "onQueryTextSubmit: "+e.getMessage());
                    }
                    if(!addressList.isEmpty()){
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        mgoogleMap.addMarker(new MarkerOptions().position(latLng).title(st_location).snippet("Destination")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }else {
                        Toast.makeText(MapsActivity.this, "Please Right The Correct Place Name", Toast.LENGTH_SHORT).show();
                    }


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }//oncreate View
    private void fetchLashLocation(){
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
           if(location != null){
               currentlocation = location;
               // Obtain the SupportMapFragment and get notified when the map is ready to be used.
               SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                       .findFragmentById(R.id.map);
               mapFragment.getMapAsync(MapsActivity.this);
           }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;

        mgoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                relativeLayout.setVisibility(View.VISIBLE);
                double lat = point.latitude;
                double lng = point.longitude;
                Latitude.setText(String.valueOf(lat));
                Longitude.setText(String.valueOf(lng));

                mgoogleMap.clear();

                mgoogleMap.addMarker(new MarkerOptions().position(point).title(point.toString()).snippet("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                MarkerOptions markerOptions = new MarkerOptions().position(current_latlng).title("Your Location").snippet("Start Point");
                mgoogleMap.addMarker(markerOptions);
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,15));
            }
        });

        current_latlng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(current_latlng).title("Your Location").snippet("Start Point");
        mgoogleMap.setMyLocationEnabled(true);


        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLng(current_latlng));
        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_latlng,15));
        mgoogleMap.addMarker(markerOptions);
    }





}
