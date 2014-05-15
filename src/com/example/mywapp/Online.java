package com.example.mywapp;

public class Online extends Request {

    public Online (String fromId) {
        super(RequestType.ONLINE, null, fromId);
    }
}
