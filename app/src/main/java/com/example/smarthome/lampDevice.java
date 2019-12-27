package com.example.smarthome;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class lampDevice extends Activity {
    public String name;
    public ImageButton lightButton;
    public ImageButton motionButton;
    public ImageView shape;
    public LinearLayout layout;

    public lampDevice(String name, LinearLayout layout){
        this.name = name;
        this.layout = layout;
        initLightButton();
    }

    public void initLightButton(){
        if (lightButton == null) lightButton = new ImageButton(getApplicationContext());
        lightButton.setImageDrawable(Drawable.createFromPath("@drawable/button_icon"));
        lightButton.setBackground(Drawable.createFromPath("@android:color/transparent"));
        lightButton.setLayoutParams(new LinearLayout.LayoutParams(160, 160));
        lightButton.setPadding(10, 10, 10, 10);
        layout.addView(lightButton);
    }


}
