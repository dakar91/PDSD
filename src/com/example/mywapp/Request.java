package com.example.mywapp;

import java.io.Serializable;

public class Request implements Serializable {
    public final RequestType type;
    public final String toId;
    public final String fromId;

    public Request (RequestType type, String toId, String fromId) {
        this.toId = toId;
        this.fromId = fromId;		
        this.type = type;
    }
}
