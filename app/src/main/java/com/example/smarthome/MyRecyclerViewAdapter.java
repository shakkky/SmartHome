package com.example.smarthome;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            String user = mData.get(position).getString("username");
            String pin = mData.get(position).getString("password");
            String admin = mData.get(position).getString("admin");
            String hasGlobal = mData.get(position).getString("hasGlobal");
            holder.usernameTextView.setText(user);
            holder.pinTextView.setText(pin);
            holder.adminTextView.setText(admin);
            holder.hasGlobalTextView.setText(hasGlobal);
            new DownloadImageTask(holder.dp).execute("https://shaktestbucket.s3-ap-southeast-2.amazonaws.com/users/"+ user +"/dp.png");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
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
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView usernameTextView;
        public TextView pinTextView;
        public TextView adminTextView;
        public TextView hasGlobalTextView;
        public ImageView dp;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user);
            pinTextView = itemView.findViewById(R.id.pin);
            adminTextView = itemView.findViewById(R.id.admin);
            hasGlobalTextView = itemView.findViewById(R.id.hasGlobal);
            dp = itemView.findViewById(R.id.displayPicture);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).toString();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}