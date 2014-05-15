package com.example.mywapp;

import java.io.Serializable;

public class Message implements Serializable{
    String fromImei, toImei;
    MessageType type;
    
    public Message (MessageType type, String toImei, String fromImei) {
        this.fromImei = fromImei;
        this.toImei = toImei;
        this.type = type;
    }
}
