package com.example.mywapp;

public class SendMessage extends Request{
    public Message msg;
    
    public SendMessage (String toId, String fromId, Message msg) {
        super(RequestType.MESSAGE, toId, fromId);
        this.msg = msg;
    }
}
