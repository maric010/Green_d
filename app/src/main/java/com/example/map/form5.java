package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class form5 extends AppCompatActivity {
static form5 th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form5);
    }

    public void back(View view) {
        finish();
    }

    public void reg_onclick(View view) {
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String surname = ((EditText)findViewById(R.id.surname)).getText().toString();
        String phone = ((EditText)findViewById(R.id.phone)).getText().toString();
        String city = ((EditText)findViewById(R.id.city)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String password_again = ((EditText)findViewById(R.id.password_again)).getText().toString();
        if(!password.equals(password_again)){
            return;
        }
        th=this;
        System.out.println("send");
        my.phone=phone;
        my.password=password;
        server.send("send_code_for_driver|"+name+"|"+surname+"|"+phone+"|"+city+"|"+password,true);

    }
}