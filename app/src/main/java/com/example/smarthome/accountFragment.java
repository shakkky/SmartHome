package com.example.smarthome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;


public class accountFragment extends Fragment {

    public TextView hasGlobalDisplay;
    public TextView userName;
    public TextView adminDisplay;
    public String hasGlobal;
    public String username;
    public String Username;
    public String admin;
    public ImageView dp;
    public RelativeLayout loader = null;
    public static final int PICK_IMAGE = 1;
    public Handler responseHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        if (hasGlobal == null) this.hasGlobal = getArguments().getString("hasGlobal");
        if (username == null) this.username = getArguments().getString("username");
        if (admin == null) this.admin = getArguments().getString("admin");
        if (loader == null) loader = v.findViewById(R.id.loadingPanel);
        if (dp == null) {
            this.dp = v.findViewById(R.id.displayPicture);
            new DownloadImageTask(dp).execute("https://shaktestbucket.s3-ap-southeast-2.amazonaws.com/users/"+ username +"/dp.png");
            dp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    System.out.println("change display picture");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    return true;
                }
            });
        }
        hasGlobalDisplay = v.findViewById(R.id.hasGlobal);
        adminDisplay = v.findViewById(R.id.adminAccess);
        Username = username.substring(0, 1).toUpperCase() + username.substring(1);
        admin = admin.substring(0, 1).toUpperCase() + admin.substring(1);
        userName = v.findViewById(R.id.userName);
        userName.setText(Username);
        adminDisplay.setText(admin);
        if (hasGlobal.equals("true")) {
            hasGlobalDisplay.setText("Global");
        } else {
            hasGlobalDisplay.setText("Local");
        }

        responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getActivity(),
                        "File uploaded", Toast.LENGTH_LONG).show();
            }
        };

        return v;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            loader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                InputStream IS = getActivity().getContentResolver().openInputStream(data.getData());
                File tempFile = createFile(inputStream, username);
                uploadtos3(getActivity(), tempFile, username);
                Bitmap ndp = BitmapFactory.decodeStream(IS);
                dp.setImageBitmap(ndp);
            } catch (Exception e) {
                System.out.println("failed to parse image");
            }
        }
    }

    public static File createFile(InputStream in, String username){
        try {
            File tempFile = File.createTempFile(username, "png");
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            IOUtils.copy(in, out);
            return tempFile;
        } catch (Exception e){
            System.out.println("failed to create file");
            return null;
        }
    }

    public static void uploadtos3 (final Context context, final File file, String username) {
        if(file !=null){
            CognitoCachingCredentialsProvider credentialsProvider;
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "ap-southeast-2:ba52b212-735e-4636-9037-f6e66c69ed10", // Identity Pool ID
                    Regions.AP_SOUTHEAST_2 // Region
            );
            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
            TransferUtility transferUtility = new TransferUtility(s3, context);
            final TransferObserver observer = transferUtility.upload(
                    "shaktestbucket",  //this is the bucket name on S3
                    "users/" + username + "/dp.png",
                    file,
                    CannedAccessControlList.PublicRead //to make the file public
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        Toast.makeText(context,"Successfully uploaded",Toast.LENGTH_LONG).show();
                    } else if (state.equals(TransferState.FAILED)) {
                        Toast.makeText(context,"Failed to upload",Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }
    }
}


