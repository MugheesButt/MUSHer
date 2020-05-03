package com.mugheesnadeem.i160029_i160068;

import com.google.firebase.database.FirebaseDatabase;

public class MessageProfile {
    String sender , reciever , message ;
    int flag ; //flag is 0 for text while 1 for image

    MessageProfile()
    {
        this.sender = "default";
        this.reciever = "default";
        this.message = "default";
        this.flag = 0 ;
    }

    MessageProfile(String s , String r , String m ,int f)
    {
        this.sender = s;
        this.reciever = r;
        this.message = m;
        this.flag = f ;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
