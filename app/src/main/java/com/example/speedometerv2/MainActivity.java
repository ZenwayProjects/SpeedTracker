package com.example.speedometerv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import in.unicodelabs.kdgaugeview.KdGaugeView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



public class MainActivity extends AppCompatActivity {
    KdGaugeView speedoMeterView;
    TextView txtLat;
    TextView txtWar;
    TextView txtAct;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    float p1,p2,p3,p4;
    private static final long INTERVAL = 300 * 2;
    private static final long FASTEST_INTERVAL = 300 * 1;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedoMeterView = (KdGaugeView) findViewById(R.id.speedMeter);
        txtLat = (TextView) findViewById(R.id.editText);
        txtAct = (TextView) findViewById(R.id.activeText);
        txtWar = (TextView) findViewById((R.id.warningText));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        alert2("PhoneSave no se puede desinstalar");

    }
    private void openAlarm(){
        startService(new Intent(this, AlarmService.class));
    }


    private void closeAlarm(){
        stopService(new Intent(this, AlarmService.class));
    }

    private void alert(String msg){
        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Cuidado, esta ventana se abrirá hasta que bajes la velocidad")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void alert2(String msg){
        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle("PhoneSave activo")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            p1=(float)location.getLongitude();
                            p2= (float) location.getLatitude();
                            double dSpeed = location.getSpeed();
                            double a = 3.6 * (dSpeed);
                            int kmhSpeed = (int) (Math.round(a));
                            txtAct.setText("PhoneSave Activo");
                            txtLat.setText("Longitude:" + location.getLongitude() + " Latitude:" + location.getLatitude()+"  SPEED="+kmhSpeed);
                            speedoMeterView.setSpeed(kmhSpeed);
                            requestNewLocationData();


                            if (kmhSpeed >= 15 ){
                                txtWar.setText("Cuidao pa, ya pasaste los 15km/h");
                                openAlarm();
                                alert("Por favor baja la velocidad");





                            }else{
                                txtWar.setText("Debajo de los 15km/h");
                                closeAlarm();


                            }

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();

            }
        } else {

            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            p3=(float)mLastLocation.getLongitude();
            p4= (float) mLastLocation.getLatitude();
            double dSpeed = mLastLocation.getSpeed();
            double a = 3.6 * (dSpeed);
            int kmhSpeed = (int) (Math.round(a));
            txtLat.setText("Longitude:" + mLastLocation.getLongitude() + " Latitude:" + mLastLocation.getLatitude()+"  SPEED="+kmhSpeed);
            speedoMeterView.setSpeed(kmhSpeed);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLastLocation();
                }
            }, 200);


        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED*/;

        // If we want background location
        // on Android 10.0 and higher,
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION/*,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION*/}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}