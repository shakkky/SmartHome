package com.example.smarthome;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class httpRequest extends Thread {
    public static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";
    public static final String KEY_RESPONSE_TEXT = "KEY_RESPONSE_TEXT";
    String reqUrl;
    String method;
    int device;
    Handler msgHandler;

    public httpRequest(String url, String method, int device, Handler handler) {
        this.reqUrl = url;
        this.method = method;
        this.device = device;
        this.msgHandler = handler;
    }

    @Override
    public void run() {
        HttpURLConnection httpConn = null;
        InputStreamReader isReader = null;
        BufferedReader bufReader = null;
        StringBuilder readTextBuf = new StringBuilder();

        try {
            URL url = new URL(reqUrl);
            httpConn = (HttpURLConnection)url.openConnection();

            if (method.equals("get")){
                httpConn.setRequestMethod("GET");
            } else {
                httpConn.setRequestMethod("POST");
            }

            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            InputStream inputStream = httpConn.getInputStream();
            isReader = new InputStreamReader(inputStream);
            bufReader = new BufferedReader(isReader);
            String line = bufReader.readLine();

            // Loop while return line is not null.
            while(line != null) {
                // Append the text to string buffer.
                readTextBuf.append(line);

                // Continue to read text line.
                line = bufReader.readLine();
            }

            Message message = new Message();
            message.what = device;

            // Create a bundle object.
            Bundle bundle = new Bundle();
            // Put response text in the bundle with the special key.
            bundle.putString(KEY_RESPONSE_TEXT, readTextBuf.toString());

            // Set bundle data in message.
            message.setData(bundle);
            // Send message to main thread Handler to process.
            msgHandler.sendMessage(message);
        }catch(MalformedURLException ex) {
            Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
        }catch(IOException ex) {
            Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
        }finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }
                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }
            }catch (IOException ex) {
                Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
            }
        }
    }
}
