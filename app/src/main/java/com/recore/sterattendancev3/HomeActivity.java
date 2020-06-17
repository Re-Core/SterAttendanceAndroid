package com.recore.sterattendancev3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.recore.sterattendancev3.Common.Common;
import com.recore.sterattendancev3.Model.Attendance;
import com.recore.sterattendancev3.Volley.VolleySingleton;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;

    private Dialog checkDialog;
    private Button btnCheckIn, btnCheckOut;
    private TextView txtCurrentTime, txtLocation;

    private String appURL;

    private Attendance attendance;


    // lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appURL = "http://10.0.2.2/SterTech/api/registerAttendance.php";

        initUI();
        setFusedLocationClient();
        updateTime();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    //Ui VIEW
    private void initUI() {
        btnCheckIn = (Button) findViewById(R.id.btn_check_in);
        btnCheckOut = (Button) findViewById(R.id.btn_check_out);
        txtCurrentTime = (TextView) findViewById(R.id.txt_time);
        txtLocation = (TextView) findViewById(R.id.txt_location);

        btnCheckIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniPopUp(true);
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniPopUp(false);
            }
        });
    }

    private void iniPopUp(final boolean state) {
        checkDialog = new Dialog(this);
        checkDialog.setContentView(R.layout.dialog_check);
        checkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button checkState = (Button) checkDialog.findViewById(R.id.dialog_check_btn);
        final EditText edtEmpAccID = (EditText) checkDialog.findViewById(R.id.edt_emp_acc_id);

        if (state) {
            Log.d("DEBUG:", String.valueOf(state));
            checkState.setBackground(getDrawable(R.drawable.checkin_btn_bg));
            checkState.setText("Check In");

        } else {
            checkState.setBackground(getDrawable(R.drawable.check_out_dialog_btn_bg));
            checkState.setText("Check Out");
        }

        checkState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAttendance(state, edtEmpAccID.getText().toString());

            }
        });
        checkDialog.show();
    }

    private void registerAttendance(boolean state, String employee_account_id) {

        attendance = new Attendance();
        attendance.setGps_longitude(String.valueOf(lastLocation.getLongitude()));
        attendance.setGps_latitude(String.valueOf(lastLocation.getLatitude()));
        attendance.setEmployees_id(employee_account_id);
        if (state) {
            attendance.setCheck_type("CheckedIn");
        } else {
            attendance.setCheck_type("CheckedOut");
        }

        if (attendance.getEmployees_id().isEmpty()) {
            Toast.makeText(this, "Must Enter Your ID", Toast.LENGTH_SHORT).show();
            checkDialog.dismiss();
        } else {

            final boolean checkState = state;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, appURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("true")) {
                        if (checkState) {
                            Toast.makeText(HomeActivity.this, "Successfully Checked in", Toast.LENGTH_SHORT).show();
                            checkDialog.dismiss();
                        } else {
                            Toast.makeText(HomeActivity.this, "Successfully Checked Out", Toast.LENGTH_SHORT).show();
                            checkDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your ID", Toast.LENGTH_SHORT).show();
                        checkDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder alert;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {

                            case 400:
                                alert = new AlertDialog.Builder(HomeActivity.this);
                                alert.setTitle("Error");
                                alert.setMessage("The request could not be understood by the server due to malformed syntax");
                                alert.setCancelable(false);
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                                break;
                            case 404:
                                alert = new AlertDialog.Builder(HomeActivity.this);
                                alert.setTitle("Error");
                                alert.setMessage("The server has not found anything matching the Request-URI");
                                alert.setCancelable(false);
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                                break;
                            case 403:
                                alert = new AlertDialog.Builder(HomeActivity.this);
                                alert.setTitle("Error");
                                alert.setMessage("The server understood the request, but is refusing to fulfill it");
                                alert.setCancelable(false);
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                                break;
                        }
                    } else {
                        alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setTitle("Error");
                        alert.setMessage(error.toString());
                        alert.setCancelable(false);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Accept", "Application/json:charset=UTF-8");
                    return super.getHeaders();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("gps_longitude", attendance.getGps_longitude());
                    params.put("gps_latitude", attendance.getGps_latitude());
                    params.put("employees_id", attendance.getEmployees_id());
                    params.put("check_type", attendance.getCheck_type());

                    return params;
                }
            };
            VolleySingleton.getInstance().addRequestQueue(stringRequest);
        }


    }

    // UTIL
    private void updateTime() {
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtCurrentTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                someHandler.postDelayed(this, 1000);

            }
        }, 10);
    }

    private void setFusedLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            createLocationRequest();
            createLocationCallback();

        } else {
            Log.d("DEBUG:", "Please grant permission");
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                lastLocation = task.getResult();

                if (lastLocation != null) {
                    try {
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                        String lat = String.valueOf(lastLocation.getLatitude());
                        String lng = String.valueOf(lastLocation.getLongitude());

                        Log.d("DEBUG", lat + " " + lng);

                        txtLocation.setText(addresses.get(0).getAddressLine(0));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                getLocation();
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

}
