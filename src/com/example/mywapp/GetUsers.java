package com.example.mywapp;


public class GetUsers extends Request {

    public GetUsers (String fromId) {
        super(RequestType.GET_USERS, null, fromId);
    }
}