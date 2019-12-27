package com.example.smarthome;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;


public class homeFragment extends Fragment {

    public Handler msgHandler = null;
    public ImageButton bookshelfLightingButton = null;
    public ImageButton loungeRoomLightingButton = null;
    public ImageButton lampMotionButton = null;
    public ImageButton bookshelfMotionButton = null;
    public ImageView LoungeRoomShape = null;
    public ImageView BookshelfShape = null;
    public SeekBar leftBedroomBlindsSeekbar = null;
    public SeekBar rightBedroomBlindsSeekbar = null;
    public ImageView BedroomBlindsShape = null;
    ArrayList<String> list;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_home, container,false);
        if (list == null) this.list = getArguments().getStringArrayList("list");
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        initUI(v);
        //startPanel();
    }


    private void startPanel() {
        final int sleepTime = 100;
        loungeRoomLightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(0), "post", 1, msgHandler).start();
            }
        });
        bookshelfLightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(1), "post", 2, msgHandler).start();
            }
        });
        lampMotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(2), "post", 3, msgHandler).start();
            }
        });
        bookshelfMotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(3), "post", 4, msgHandler).start();
            }
        });
        leftBedroomBlindsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(4)+"/"+progressChangedValue, "post", 5, msgHandler).start();
            }
        });

        rightBedroomBlindsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                new httpRequest(list.get(5)+"/"+progressChangedValue, "post", 6, msgHandler).start();
            }
        });
    }

    private void initUI(View v){
//        if (lampMotionButton == null) lampMotionButton = v.findViewById(R.id.LoungeRoomSensor);
//        if (bookshelfMotionButton == null) bookshelfMotionButton = v.findViewById(R.id.BookshelfSensor);
//        if (bookshelfLightingButton == null) bookshelfLightingButton = v.findViewById(R.id.bookshelfLightButton);
//        if (loungeRoomLightingButton == null) loungeRoomLightingButton = v.findViewById(R.id.LoungeLightButton);
//        if (LoungeRoomShape == null) LoungeRoomShape = v.findViewById(R.id.LoungeRoomShape);
//        if (BookshelfShape == null) BookshelfShape = v.findViewById(R.id.BookshelfShape);
//        if (leftBedroomBlindsSeekbar == null) leftBedroomBlindsSeekbar = v.findViewById(R.id.LeftBedroomBlindsSeekbar);
//        if (rightBedroomBlindsSeekbar == null) rightBedroomBlindsSeekbar = v.findViewById(R.id.RightBedroomBlindsSeekbar);
//        if (BedroomBlindsShape == null) BedroomBlindsShape = v.findViewById(R.id.BedroomBlindsShape);
//        if (msgHandler == null) msgHandler = new msgHandler(loungeRoomLightingButton, bookshelfLightingButton, lampMotionButton, bookshelfMotionButton, LoungeRoomShape, BookshelfShape, BedroomBlindsShape, leftBedroomBlindsSeekbar, rightBedroomBlindsSeekbar);

        LinearLayout buttonHolder = v.findViewById(R.id.buttonHolder);
        lampDevice bedroomLamp = new lampDevice("Bedroom Lamp", buttonHolder);

//        new httpRequest(list.get(0), "get", 1, msgHandler).start();
//        new httpRequest(list.get(1), "get", 2, msgHandler).start();
//        new httpRequest(list.get(2), "get", 3, msgHandler).start();
//        new httpRequest(list.get(3), "get", 4, msgHandler).start();
//        new httpRequest(list.get(4), "get", 5, msgHandler).start();
//        new httpRequest(list.get(5), "get", 6, msgHandler).start();
    }
}
