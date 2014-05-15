package com.example.mywapp;

import java.io.Serializable;

public class Response implements Serializable {
    public final ResponseType type;
    public final String sendToImei;
    public final String sendFromImei;
    
    public Response (ResponseType type, String toImei, String fromImei) {
        this.type = type;
        this.sendToImei = toImei;
        this.sendFromImei = fromImei;
    }
}