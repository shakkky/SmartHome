package com.example.smarthome;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;
import org.json.*;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = MainActivity.class.getSimpleName();
    public String username;
    public String password;
    public String hasGlobal;
    public String admin;
    public Button signin= null;
    public EditText usernameInput = null;
    public EditText pinInput = null;
    public RelativeLayout loader = null;
    public Handler responseHandler;
    public Handler lambdaResponse;
    double current = BuildConfig.VERSION_CODE;
    String responseResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.signin_activity);
        UI();
    }

    @Override
    public void onPause() {
        super.onPause();
        signin = null;
        usernameInput = null;
        pinInput = null;
    }

    public void UI() {
        if (signin == null) signin = findViewById(R.id.signin);
        if (usernameInput == null) usernameInput = findViewById(R.id.username);
        if (pinInput == null) pinInput = findViewById(R.id.pin);
        if (loader == null) loader = findViewById(R.id.loadingPanel);

        responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    String responseText = bundle.getString("KEY_RESPONSE_TEXT");
                    try {
                        JSONObject object = new JSONObject(responseText);
                        responseResult = object.getString("response");
                    } catch (Exception e){
                        System.out.println("failed to parse JSON object");
                    }
                    double version = Double.parseDouble(responseResult);
                    if (version > current) {
                        Toast.makeText(getApplicationContext(),
                                "Current App Version: " + current + ", Latest App Version: " + version, Toast.LENGTH_LONG).show();
                        getUpdatedVersion();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "App Version: " + current, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        lambdaResponse = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    String responseText = bundle.getString("KEY_RESPONSE_TEXT");
                    try {
                        JSONObject object = new JSONObject(responseText);
                        responseResult = object.getString("response");
                        hasGlobal = object.getString("hasGlobal");
                        admin = object.getString("admin");
                    } catch (Exception e){
                        System.out.println("failed to parse JSON object");
                    }
                    if (responseResult.equals("pass")) {
                        loader.setVisibility(View.GONE);
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), Main2Activity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("admin", admin);
                        intent.putExtra("hasGlobal", hasGlobal);
                        startActivity(intent);
                    } else {
                        username = null;
                        password = null;
                        Toast.makeText(getApplicationContext(),"Incorrect username or password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        new httpRequest("https://nt93s0dqei.execute-api.ap-southeast-2.amazonaws.com/default/getVersion", "get", 0, responseHandler).start();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameInput.getText() != null && pinInput.getText() != null) {
                    loader.setVisibility(View.VISIBLE);
                    username = usernameInput.getText().toString();
                    password = pinInput.getText().toString();
                    //make http get request to lambda through API gateway.
                    new httpRequest("https://hyyhjxzd86.execute-api.ap-southeast-2.amazonaws.com/default/validateCredentials?username=" + username + "&password=" + password, "get", 0, lambdaResponse).start();
                } else {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Please enter a username and pin.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pinInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        });
    }

    public void getUpdatedVersion() {
        if (CheckForSDCard.isSDCardPresent()) {
            //check if app has permission to write to the external storage.
            if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new DownloadFile().execute("https://shaktestbucket.s3-ap-southeast-2.amazonaws.com/latest/app-debug.apk");
            } else {
                //If permission is not present request for the same.
                EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "SD Card not found", Toast.LENGTH_LONG).show();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MainActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        new DownloadFile().execute("https://shaktestbucket.s3-ap-southeast-2.amazonaws.com/latest/app-debug.apk");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    public class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MainActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        public String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 16k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 16384);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "downloaded/";

                //Create downloaded folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) directory.mkdir();

                Thread.sleep(2000);
                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);
                byte data[] = new byte[2048];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Oops, something went wrong";
        }

        public void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        public void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(folder + fileName)), "application/vnd.android.package-archive" );
            Log.d("Lofting", "About to install new .apk");
            startActivity(i);

            // Display File path after downloading
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }
}