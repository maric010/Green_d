package com.example.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Form2 extends AppCompatActivity {


static Form2 th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);
th=this;
    }

    public void reset_password_onClick(View view) {
    }

    public void auth_onClick(View view) {
        String phone = ((EditText)findViewById(R.id.phone)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        if(phone.equals("") || password.equals(""))
            return;
        my.phone=phone;
        my.password=password;
       server.send("dlogin|"+phone+"|"+password,true);
    }

    public void reg_onclick(View view) {
        Intent intent = new Intent(Form2.this, form5.class);
        startActivity(intent);
    }
}