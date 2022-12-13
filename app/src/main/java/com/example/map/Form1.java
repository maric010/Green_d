package com.example.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public class Form1 extends AppCompatActivity {
static Form1 th;
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;
    private static final String PREFS_FILE = "Account";
    private static final String PREF_phone = "phone";
    private static final String PREF_password = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);

        th=this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!server.is_connected){
                        server.connect(true);
                        server.is_connected=true;
                        server.get();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ActivityCompat.checkSelfPermission(Form1.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        MyService.th = new Intent(Form1.this, MyService.class);
                        startService(MyService.th);
                    }
                    settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
                    my.phone = settings.getString(PREF_phone,"");
                    my.password = settings.getString(PREF_password,"");
                    Thread.sleep(1000);
                    if(my.phone.equals("")){

                        Intent intent = new Intent(Form1.this, Form2.class);
                        startActivity(intent);
                        finish();
                    }else{
                        server.send("dlogin|"+my.phone+"|"+my.password,true);
                    }
                } catch (Exception e) {
                    Log.e(connection.LOG_TAG, e.getMessage());
                }
            }
        });
        thread.start();



    }

}