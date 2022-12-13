package com.example.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.Map;

public class Form8 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form8);
    }

    public void textcl(View view) {
        if(ActivityCompat.checkSelfPermission(Form8.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Form8.this, Form3.class);
            startActivity(intent);
        }
        else if(ActivityCompat.checkSelfPermission(Form8.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)){
            Intent intent = new Intent(Form8.this, Form4.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(Form8.this, MapsActivity.class);
            startActivity(intent);
        }
    }
}