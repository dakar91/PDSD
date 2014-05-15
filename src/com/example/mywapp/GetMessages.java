package com.example.mywapp;


public class GetMessages extends Request {

    public GetMessages (String toId, String fromId) {
        super(RequestType.GET_MESSAGES, toId, fromId);
    }
}
