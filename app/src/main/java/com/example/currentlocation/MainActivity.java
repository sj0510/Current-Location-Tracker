package com.example.currentlocation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Button GetLocationBTN;
    private EditText et_InputNumb;
    private TextView CountryName , StateName, CityName, Pincode, Address;

    LinearLayout locattionDetails;

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetLocationBTN = findViewById(R.id.btn_GetLoaction);
        et_InputNumb = findViewById(R.id.et_InputNumber);
        CountryName = findViewById(R.id.tv_Country);
        StateName = findViewById(R.id.tv_State);
        CityName = findViewById(R.id.tv_CityName);
        Pincode = findViewById(R.id.tv_Pincode);
        Address = findViewById(R.id.tv_Address);

        locattionDetails =(LinearLayout)findViewById(R.id.LocationDetailsLayout);
        
        grantPermission();

        CheckLocationIsEnableOrNot(); // redirect to location permission page


        GetLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_InputNumb.getText().toString().isEmpty()){
                    et_InputNumb.setError("Please enter any number (1-5)");
                }
                else if (et_InputNumb.getText().toString().length() != 1){
                    et_InputNumb.setError("Wrong input..!");
                }
                else {
                    locattionDetails.setVisibility(View.VISIBLE);
                    getLocation();
                }
            }
        });
    }

    private void getLocation() {
        try {
            locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,5,(LocationListener)this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void CheckLocationIsEnableOrNot() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!gpsEnabled && !networkEnabled){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // this intent redirect us to location settings, IF gps is diabled this dialogue , this dialogue will show
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    }).setNegativeButton("Cancel", null).show();


        }

    }

    private void grantPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            CountryName.setText(addresses.get(0).getCountryName());
            StateName.setText(addresses.get(0).getAdminArea());
            CityName.setText(addresses.get(0).getLocality());
            Pincode.setText(addresses.get(0).getPostalCode());
            Address.setText(addresses.get(0).getAddressLine(0));



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}