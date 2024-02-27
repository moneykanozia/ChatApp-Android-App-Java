package com.mad.chitchat;

import android.widget.ImageView;

public class User {
    private ImageView userImage;
    private String uname,lastMessage;

    public User(String uname, String lastMessage){
        this.uname = uname;
        this.lastMessage = lastMessage;
    }

    public String getUname(){
        return uname;
    }

    public void setUname(String uname){
        this.uname = uname;
    }

    public String getLastMessage(){
        return lastMessage;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }

    public void getUserImage(){

    }

    public void setUserImage(){

    }

}
