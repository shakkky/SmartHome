package com.example.smarthome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import static java.lang.Integer.parseInt;

public class msgHandler extends Handler{
    public static final String KEY_RESPONSE_TEXT = "KEY_RESPONSE_TEXT";
    public ImageButton bookshelfLightingButton;
    public ImageButton loungeRoomLightingButton;
    public ImageButton lampMotionButton;
    public ImageButton bookshelfMotionButton;
    public ImageView LoungeRoomShape;
    public ImageView BookshelfShape;
    public ImageView BedroomBlindsShape;
    public SeekBar leftBedroomBlindsSeekbar;
    public SeekBar rightBedroomBlindsSeekbar;

    public msgHandler(ImageButton loungeRoomLightingButton, ImageButton bookshelfLightingButton, ImageButton lampMotionButton, ImageButton bookshelfMotionButton, ImageView LoungeRoomShape, ImageView BookshelfShape, ImageView BedroomBlindsShape, SeekBar leftBedroomBlindsSeekbar, SeekBar rightBedroomBlindsSeekbar){
        this.loungeRoomLightingButton = loungeRoomLightingButton;
        this.bookshelfLightingButton = bookshelfLightingButton;
        this.lampMotionButton = lampMotionButton;
        this.bookshelfMotionButton = bookshelfMotionButton;
        this.LoungeRoomShape = LoungeRoomShape;
        this.BookshelfShape = BookshelfShape;
        this.BedroomBlindsShape = BedroomBlindsShape;
        this.leftBedroomBlindsSeekbar = leftBedroomBlindsSeekbar;
        this.rightBedroomBlindsSeekbar = rightBedroomBlindsSeekbar;
    }

    public boolean checkProgressBars(int progress){
        if (progress > 1 && progress < 5){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        if (bundle != null){
            String responseText = bundle.getString(KEY_RESPONSE_TEXT);
            if (msg.what == 1) {
                if (responseText.equals("On")) { //Lounge room lamp on
                    loungeRoomLightingButton.setPressed(true);
                    LoungeRoomShape.setPressed(true);
                } else {
                    loungeRoomLightingButton.setPressed(false);
                    LoungeRoomShape.setPressed(false);
                }
            } else if (msg.what == 2){ //Bookshelf lamp on
                if (responseText.equals("On")){
                    bookshelfLightingButton.setPressed(true);
                    BookshelfShape.setPressed(true);
                } else {
                    bookshelfLightingButton.setPressed(false);
                    BookshelfShape.setPressed(false);
                }
            } else if (msg.what == 3){ //Lounge room motion on
                if (responseText.equals("On")){
                    loungeRoomLightingButton.setEnabled(false);
                    lampMotionButton.setPressed(true);
                    LoungeRoomShape.setEnabled(false);
                } else {
                    lampMotionButton.setPressed(false);
                    loungeRoomLightingButton.setEnabled(true);
                    LoungeRoomShape.setEnabled(true);
                }
            } else if (msg.what == 4){ // Bookshelf motion on
                if (responseText.equals("On")){
                    bookshelfMotionButton.setPressed(true);
                    bookshelfLightingButton.setEnabled(false);
                    BookshelfShape.setEnabled(false);
                } else {
                    bookshelfMotionButton.setPressed(false);
                    bookshelfLightingButton.setEnabled(true);
                    BookshelfShape.setEnabled(true);
                }
            } else if (msg.what == 5){ // Left Bedroom blinds available
                try {
                    int res = parseInt(responseText);
                    leftBedroomBlindsSeekbar.setProgress(res);
                    if (!checkProgressBars(res) && !checkProgressBars(rightBedroomBlindsSeekbar.getProgress())){
                        BedroomBlindsShape.setPressed(false);
                    } else {
                        BedroomBlindsShape.setPressed(true);
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            } else if (msg.what == 6){ // Right Bedroom blinds available
                try {
                    int res = parseInt(responseText);
                    rightBedroomBlindsSeekbar.setProgress(res);
                    if (!checkProgressBars(res) && !checkProgressBars(leftBedroomBlindsSeekbar.getProgress())){
                        BedroomBlindsShape.setPressed(false);
                    } else {
                        BedroomBlindsShape.setPressed(true);
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }
    }
}
