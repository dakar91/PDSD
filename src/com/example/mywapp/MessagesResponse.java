package com.example.mywapp;

import java.util.ArrayList;

public class MessagesResponse extends Response {
    ArrayList<Message> messages;
    
    public MessagesResponse (String toImei, String fromImei, ArrayList<Message> messages) {
        super(ResponseType.MESSAGES, toImei, fromImei);
        this.messages = messages;
    }
}
