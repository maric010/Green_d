package com.example.map;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class connection {


    static Socket mSocket = null;
    private  String  mHost   = null;
    private  int     mPort   = 0;

    public static final String LOG_TAG = "SOCKET";

    public connection(String s, int i) {
        this.mHost = s;
        this.mPort = i;
    }


    /**
     * Метод открытия сокета
     */
    public void openConnection() throws Exception
    {
        // Если сокет уже открыт, то он закрывается
        closeConnection();
        try {
            // Создание сокета
            mSocket = new Socket(mHost, mPort);
        } catch (IOException e) {
            throw new Exception("Невозможно создать сокет: "+e.getMessage());
        }
    }
    /**
     * Метод закрытия сокета
     */
    public void closeConnection()
    {
        /* Проверяем сокет. Если он не зарыт, то закрываем его и освобдождаем соединение.*/
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Невозможно закрыть сокет: " + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }
    /**
     * Метод отправки данных
     */
    public String sendData(byte[] data,boolean accept) throws Exception {
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
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
            if(accept){
                InputStream input = mSocket.getInputStream();
                data = new byte[2048];
                input.read(data);
                server.is_busy=false;
                return new String(data, StandardCharsets.UTF_8).replaceAll("\u0000.*", "");
            }


        } catch (IOException e) {
            throw new Exception("Невозможно отправить данные: "+e.getMessage());
        }
        server.is_busy=false;
        return "";
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        closeConnection();
    }




}

