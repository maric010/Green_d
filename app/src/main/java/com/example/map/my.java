package com.example.map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class my {

    public static String id="",phone,city,password;
    public static String status="band";
    public static Order order;
    public static ArrayList<Order> orders=new ArrayList<>();
    public static String data;
    static Location location;
    static boolean service_started=false;
    static boolean file_avatar=false, file_vu1 = false,file_vu2 = false,file_tex = false,file_a1 = false,file_a2 = false,file_a3 = false,file_a4 = false;

    static boolean login=false;
}
