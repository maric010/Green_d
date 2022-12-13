package com.example.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import static com.example.map.connection.mSocket;
import static java.lang.Thread.sleep;

public class server {
    static boolean is_busy=false;
    static connection  mConnect  = null;
    public static void connect(boolean wait)
    {
        // Создание подключения
        mConnect = new connection("185.217.131.138", 7777);
        // Открытие сокета в отдельном потоке
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnect.openConnection();
                    // Разблокирование кнопок в UI потоке
                    Log.d(connection.LOG_TAG, "Соединение установлено");
                    Log.d(connection.LOG_TAG, "(mConnect != null) = " + (mConnect != null));
                } catch (Exception e) {
                    Log.e(connection.LOG_TAG, e.getMessage());
                    mConnect = null;
                }
            }
        });
                thread.start();
                if(wait){
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

    }
    static long last_send=0;
    static void send(String data,boolean wait){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        OutputStream sender = mSocket.getOutputStream();
                        sender.write((data+"\n").getBytes());
                        sender.flush();
                        last_send=Calendar.getInstance().getTimeInMillis();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        server.connect(true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        if(my.login) {

                            try {
                                OutputStream sender = mSocket.getOutputStream();
                                sender.write(("dlogin|" + my.phone + "|" + my.password+"\n").getBytes());
                                sender.flush();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                        continue;
                    }
                }
            }
        });
        thread.start();
        if(wait){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public static boolean is_connected=false;
    public static void get()
    {
        Thread get_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BReader in = null;
                try {
                    in = new BReader(new InputStreamReader(connection.mSocket.getInputStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("СЛУШАЮ");
                long update_connection = 0;
                while (server.is_connected){
                    try {
                        String st = null;
                        if (in != null) {
                            st = in.readLine();
                        }
                        System.out.println("Ответ: "+st);
                        if(st!=null)
                            command.command(st);
                        else{
                            server.connect(true);
                            if(my.login) {
                                server.send("dlogin|" + my.phone + "|" + my.password, true);
                            }
                            Thread.sleep(1000);
                            if(mSocket!=null)
                            try {
                                in = new BReader(new InputStreamReader(connection.mSocket.getInputStream(), StandardCharsets.UTF_8));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        server.connect(true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        if(mSocket!=null){
                            try {
                                in = new BReader(new InputStreamReader(connection.mSocket.getInputStream(), StandardCharsets.UTF_8));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();

                            }
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }


                    }
                }
            }
        });

        get_thread.start();



    }
    public static void close()
    {
        server.is_connected=false;
        // Закрытие соединения
        mConnect.closeConnection();
        Log.d(connection.LOG_TAG, "Соединение закрыто");
    }
    static final String FTP_HOST= "ftp://185.217.131.138/";

    static final String FTP_USER = "myftpadmin";
    static final String FTP_PASS  ="00000000";
    static boolean directory=false;
    public static boolean send_file(String name,InputStream in){
        boolean upload_result = false;
        try{
            FTPClient con = new FTPClient();
            con.connect("185.217.131.138",21);
            if (con.login("myftpadmin", "00000000"))
            {
                    con.changeWorkingDirectory("accounts");
                    con.enterLocalPassiveMode(); // important!
                if(!directory)
                    con.makeDirectory(my.id);
                con.changeWorkingDirectory(my.id);
                con.setFileType(FTP.BINARY_FILE_TYPE);
                con.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                    //
                    upload_result =con.storeFile(name,in);

                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return upload_result;

    }
}
