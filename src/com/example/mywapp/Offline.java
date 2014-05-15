package com.example.mywapp;

public class Offline extends Request {

    public Offline (String fromId) {
        super(RequestType.OFFLINE, null, fromId);
    }
}

