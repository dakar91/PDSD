package com.example.mywapp;

class TextMessage extends Message {
    String whatToSend;
    
    TextMessage (String whatToSend, String toImei, String fromImei) {
        super(MessageType.TEXT, toImei, fromImei);
        this.whatToSend = whatToSend;
    }
}