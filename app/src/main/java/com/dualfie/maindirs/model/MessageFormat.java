package com.dualfie.maindirs.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.util.Date;

public class MessageFormat {
    private String messageText;
    private long messageTime;
    private String messageUser;
    private String key;
    private boolean by;
    private Bitmap imageBitmap;

    public MessageFormat(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        by = true;
        messageTime = new Date().getTime();
        imageBitmap = null;
    }

    public MessageFormat(String message, String from, String image) {
        messageText = message;
        messageUser = from;
        by = true;
        messageTime = new Date().getTime();
        if (image != null && image != "") {
            byte[] decodedString = Base64.decode(image, Base64.URL_SAFE);
            this.imageBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
    }

    public boolean getBy() { return by; }

    public void setBy(boolean by) {this.by = by;}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public Bitmap getImageBitmap() { return imageBitmap; }

    public void setImageBitmap(Bitmap imageBitmap) { this.imageBitmap = imageBitmap; }

    public boolean isBy() { return by; }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

}
