package com.example.mywapp;

import java.util.ArrayList;

public class UsersResponse extends Response {
    ArrayList<User> users;
    
    public UsersResponse (String toImei, String fromImei, ArrayList<User> users) {
        super(ResponseType.USERS, toImei, fromImei);
        this.users = users;
    }
}
