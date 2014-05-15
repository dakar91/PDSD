package com.example.mywapp;


import com.example.mywapp.*;
import java.util.*;


public class User implements java.io.Serializable {
    private final String IMEI;
    private String name;
    private transient String password;
    private transient HashMap<String, ArrayList<Message>> leftMessages;
    
    public User (String imei, String password, String name) {
        this.IMEI = imei;
        this.password = password;
        this.name = name;
        this.leftMessages = new HashMap<String, ArrayList<Message>>();
    }
    
    public synchronized void setPassword (String passwd) {
        this.password = passwd;
    }
    
    public synchronized boolean checkPassword (String passwd) {
        //return password.equals(passwd);
        return true;
    }
    
    public synchronized String getPassword () {
        return password;
    }
    
    public synchronized ArrayList<Message> getMessages (String imei) {
        ArrayList<Message> tmp = leftMessages.get(imei);
        if (tmp == null || tmp.isEmpty()) {
            return null;
        } else {
            ArrayList<Message> tmpCloned = (ArrayList<Message>)tmp.clone();
            tmp.clear();
            return tmpCloned;
        }
    }
    
    public synchronized boolean addMessage (Message m) {
        ArrayList<Message> tmp = leftMessages.get(m.toImei);
        if (tmp == null)
            tmp = new ArrayList<Message>();

        tmp.add(m);
        leftMessages.put(m.toImei, tmp);
        return true;
    }
    
    public synchronized boolean hasLeftMessages (String imei) {
        return !(leftMessages.get(imei).isEmpty());
    }
    
    public synchronized void setName (String name) {
        this.name = name;
    }
    
    public synchronized String getImei () {
        return this.IMEI;
    }
    
    public synchronized String getName () {
        return this.name;
    }
}
