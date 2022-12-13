package com.example.map;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;
import static com.example.map.MapsActivity.getUrl;
import static java.lang.Thread.sleep;

public class command {
    private static final String PREFS_FILE = "Account";
    private static final String PREF_phone = "phone";
    private static final String PREF_password = "password";
    static void command(String text){
        String[] sp=text.split("\\|");
        switch (sp[0]){
            case "login_success":
                if(!my.login) {
                    if (Form1.th != null) {
                        Form1.th.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ActivityCompat.checkSelfPermission(Form1.th, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Form1.th, Form3.class);
                                    Form1.th.startActivity(intent);
                                    Form1.th.finish();
                                } else if (ActivityCompat.checkSelfPermission(Form1.th, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                                    Intent intent = new Intent(Form1.th, Form4.class);
                                    Form1.th.startActivity(intent);
                                    Form1.th.finish();
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(Form1.th)) {
                                    Intent intent = new Intent(Form1.th, Form9.class);
                                    Form1.th.startActivity(intent);
                                    Form1.th.finish();
                                } else {
                                    Intent intent = new Intent(Form1.th, MapsActivity.class);
                                    Form1.th.startActivity(intent);
                                    Form1.th.finish();
                                }
                            }
                        });
                    } else if (Form2.th != null) {
                        Form2.th.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor prefEditor;
                                SharedPreferences settings = Form2.th.getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
                                prefEditor = settings.edit();
                                prefEditor.putString(PREF_phone, my.phone);
                                prefEditor.putString(PREF_password, my.password);
                                prefEditor.apply();
                                if (ActivityCompat.checkSelfPermission(Form2.th, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Form2.th, Form3.class);
                                    Form2.th.startActivity(intent);
                                } else if (ActivityCompat.checkSelfPermission(Form2.th, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                                    Intent intent = new Intent(Form2.th, Form4.class);
                                    Form2.th.startActivity(intent);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(Form2.th)) {
                                    Intent intent = new Intent(Form2.th, Form9.class);
                                    Form2.th.startActivity(intent);
                                    Form2.th.finish();
                                } else {
                                    Intent intent = new Intent(Form2.th, MapsActivity.class);
                                    Form2.th.startActivity(intent);
                                }

                                Form2.th.finish();
                            }
                        });
                    }

                }
                my.login=true;
                break;
            case "code_sended_successfully":


                form5.th.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(form5.th, Form6.class);
                        form5.th.startActivity(intent);
                    }
                });
                break;
            case "login_failed":
                if(Form2.th!=null){
                    Form2.th.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(Form2.th).create();
                            alertDialog.setTitle("ERROR");
                            alertDialog.setMessage(sp[1]);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                }


                break;
            case "order_accepted":
                for(Order order : my.orders){
                    if(order.id.equals(sp[1])){
                        System.out.println(order.otkuda);
                        my.order=order;
                        MapsActivity.thisactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View view = MapsActivity.thisactivity.findViewById(R.id.main_order);
                                view.setVisibility(View.VISIBLE);
                                TextView view_order_km_min = MapsActivity.thisactivity.findViewById(R.id.order_km_min);
                                view_order_km_min.setText(order.km+" км - 24 мин");
                                TextView view_order_otkuda = MapsActivity.thisactivity.findViewById(R.id.order_otkuda);
                                view_order_otkuda.setText(order.otkuda);
                                TextView view_order_kuda = MapsActivity.thisactivity.findViewById(R.id.order_kuda);
                                view_order_kuda.setText(order.kuda);
                                TextView view_order_summa = MapsActivity.thisactivity.findViewById(R.id.order_summa);
                                view_order_summa.setText(order.summa);
                                String[] ll = order.latlng1.split(":");
                                new FetchURL(MapsActivity.thisactivity).execute(getUrl(new LatLng(my.location.getLatitude(),my.location.getLongitude()), new LatLng(Double.parseDouble(ll[0]),Double.parseDouble(ll[1])), "driving"), "driving");

                            }
                        });


                        break;
                    }
                }
                break;
            case "order":
                String id = sp[1];
                String id_user = sp[2];
                String otkuda = sp[3];
                String kuda = sp[4];
                String summa = sp[5];
                String tariff=sp[6];
                String clist = sp[7];
                String[] s = sp[8].split("\\.");
                String latlng1 = sp[9];
                String latlng2 = sp[10];
                String km = s[0]+"."+s[1].substring(0,3);
                Order order = new Order();
                order.id = id;
                order.id_user = id_user;
                order.otkuda = otkuda;
                order.kuda = kuda;
                order.summa = summa;
                order.tariff = tariff;
                order.clist = clist;
                order.latlng1 = latlng1;
                order.latlng2 = latlng2;
                order.km = km;
                my.orders.add(order);

                MapsActivity.thisactivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int LAYOUT_FLAG;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                        } else {
                            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                        }
                        WindowManager manager = (WindowManager) MapsActivity.thisactivity.getSystemService(WINDOW_SERVICE);
                        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, // Ширина экрана
                                ViewGroup.LayoutParams.WRAP_CONTENT, // Высота экрана
                                LAYOUT_FLAG, // Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                                PixelFormat.TRANSLUCENT); // Само окно прозрачное

                        // Задаем позиции для нашего Layout
                        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
                        params.x = 0;
                        params.y = 0;
                        // Отображаем наш Layout
                        LinearLayout rootView = (LinearLayout) LayoutInflater.from(MapsActivity.thisactivity).inflate(R.layout.auto_order, null);

                        TextView km_view = rootView.findViewById(R.id.order_km);
                        km_view.setText(km);
                        TextView otkuda_view = rootView.findViewById(R.id.order_otkuda);
                        otkuda_view.setText(otkuda);
                        TextView kuda_view = rootView.findViewById(R.id.order_kuda);
                        kuda_view.setText(kuda);

                        TextView summa_view = rootView.findViewById(R.id.order_summa);
                        summa_view.setText(summa);
                        final boolean[][] ok = {{false}};
                        final boolean[] closed_show = {false};
                        /*
                        MediaPlayer r = MediaPlayer.create(MapsActivity.thisactivity, R.raw.auto_order3);
                        r.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                r.stop();
                                try {
                                    r.prepare();
                                    r.seekTo(0);
                                }
                                catch (Throwable t) {
                                }
                            }
                        });

                        AudioManager mAudioManager = (AudioManager) MapsActivity.thisactivity.getSystemService(Context.AUDIO_SERVICE);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
                        r.start();
                        */
                        final boolean[] clicked = {false};
                        rootView.findViewById(R.id.accept_order).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(clicked[0])
                                    return;
                                clicked[0] =true;
                                server.send("accept_order|"+order.id,true);
                                ok[0][0]=true;
                                closed_show[0] =true;

                            }
                        });

                        manager.addView(rootView, params);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
                                for (int i = 100; i > -1; i -= 1) {
                                    if (closed_show[0]) {
                                        //r.stop();
                                        manager.removeView(rootView);
                                        break;
                                    }
                                    progressBar.setProgress(i);
                                    try {
                                        sleep(120);
                                    } catch (InterruptedException e) {
                                        //e.printStackTrace();
                                    }
                                    if(i==0){
                                        //r.stop();
                                        manager.removeView(rootView);
                                        server.send("decline_order",true);
                                    }
                                }
                            }}).start();

                    }
                });
                break;
            case "sign_in_successfully":
                my.id=sp[1];
                my.login=true;
                Intent intent = new Intent(Form6.th, Form7.class);
                Form6.th.startActivity(intent);
                break;
            case "info_accepted_successfully":
                intent = new Intent(Form7.th, Form8.class);
                Form7.th.startActivity(intent);
                break;
            case "file_loaded":
                break;
        }
    }
}
