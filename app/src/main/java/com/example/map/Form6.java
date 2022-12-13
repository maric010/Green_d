package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Form6 extends AppCompatActivity {
    static Form6 th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form6);
    }

    public void back(View view) {
        finish();
    }

    public void reg_onclick(View view) {
        th=this;
        server.send("sign_in_with_code_for_driver|"+ (((EditText)findViewById(R.id.name)).getText().toString()),true);

    }
}