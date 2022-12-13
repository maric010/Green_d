package com.example.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.net.ConnectivityManager;
import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.ContentValues.TAG;
import static com.example.map.connection.LOG_TAG;
import static com.example.map.connection.mSocket;

public class voicechat extends AppCompatActivity {
    int myBufferSize = 2000;
    AudioRecord audioRecord;
    boolean udp=true;

    //test
    private boolean status = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicechat);

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnected();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi)
        {
            Toast.makeText(getApplicationContext(),"Please make sure your Network Connection is ON ", Toast.LENGTH_LONG).show();
        }
        else if(isWifi)
        {
            udp=false;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1234);
        }
        else
            startStreaming();


    }
    String room="0";
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1234: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startStreaming();

                } else {
                    Log.d("TAG", "permission denied by user");
                }
                return;
            }
        }
    }
    public void startStreaming() {
        createAudioRecorder();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9987);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramSocket finalSocket = socket;


        AudioTrack audio = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 10000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 2000,
                AudioTrack.MODE_STREAM);

        audio.play();
        if(udp){
            System.out.println("VOICE UDP");
            Thread udp_streamThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("VOICE UDP1");
                        DatagramPacket packet;
                        final InetAddress destination = InetAddress.getByName("83.220.174.210");
                        audioRecord.startRecording();
                        byte[] myBuffer = new byte[myBufferSize];
                        while (status) {
                            System.out.println("VOICE UDP2");
                            audioRecord.read(myBuffer, 0, myBufferSize);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                            outputStream.write((room+"|").getBytes());
                            outputStream.write( myBuffer);
                            packet = new DatagramPacket (outputStream.toByteArray(),outputStream.toByteArray().length,destination,9987);
                            finalSocket.send(packet);
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }});
            udp_streamThread.start();
            DatagramSocket finalSocket1 = socket;
            DatagramSocket finalSocket2 = socket;

            Thread udp_recieveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (status) {
                        try {
                            byte[] message = new byte[2000];
                            DatagramPacket packet = new DatagramPacket(message,message.length);
                            Log.i("UDP client: ", "about to wait to receive");
                            finalSocket1.receive(packet);
                            audio.write(packet.getData(), 0, packet.getData().length);
                        }catch (IOException e) {
                            //status = false;
                        }
                    }

                    finalSocket2.disconnect();
                    finalSocket2.close();
                    audio.stop();
                    audio.flush();
                    audio.release();
                }
            });
            udp_recieveThread.start();
        }
        else{

            Thread tcp_streamThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true){
                            // Проверка открытия сокета
                            if (mSocket == null || mSocket.isClosed()) {
                                System.out.println("Невозможно отправить данные. Сокет не создан или закрыт");
                                Thread.sleep(50);
                                continue;
                            }
                            if(server.is_busy){
                                System.out.println("pzdts");
                                Thread.sleep(50);
                                continue;
                            }
                            server.is_busy=true;
                            break;
                        }
                        // Отправка данных
                        try {
                            mSocket.getOutputStream().write(("voice|"+room).getBytes());
                            mSocket.getOutputStream().flush();
                            Thread.sleep(500);
                            byte[] myBuffer = new byte[myBufferSize];
                            while (true){
                                audioRecord.read(myBuffer, 0, myBufferSize);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                                outputStream.write((room+"|").getBytes());
                                outputStream.write( myBuffer);
                                System.out.println("buffersize "+myBuffer.length);
                                mSocket.getOutputStream().write(outputStream.toByteArray());
                                mSocket.getOutputStream().flush();
                            }
                        } catch (IOException e) {
                            throw new Exception("Невозможно отправить данные: "+e.getMessage());
                        }


                    } catch (Exception e) {
                        Log.e(connection.LOG_TAG, e.getMessage());
                    }
                    server.is_busy=false;
                }
            });
            tcp_streamThread.start();
            Thread tcp_recieveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream input = mSocket.getInputStream();

                    while (status) {
                        try {
                            byte[] data = new byte[2000];
                            input.read(data);
                            audio.write(data, 0, data.length);
                        }catch (IOException e) {
                        }
                    }
                    input.close();
                    audio.stop();
                    audio.flush();
                    audio.release();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            tcp_recieveThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        aec.release();
        aec=null;
        super.onDestroy();
    }

    AcousticEchoCanceler aec;
    private void createAudioRecorder() {
        int sampleRate = 10000;
        int channelConfig = AudioFormat.CHANNEL_IN_DEFAULT;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int internalBufferSize = myBufferSize;

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRate, channelConfig, audioFormat, internalBufferSize);
        if(AcousticEchoCanceler.isAvailable()){
            aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
            aec.setEnabled(true);
        }
    }
}
