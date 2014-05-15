package com.example.mywapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;


public class Connection {
    private static String TAG = "MyWhatsApp";
	private static String IP, DEVICE_ID;
	private static int PORT;
	private static Object monitor, start;
	private static Request whatToSend;
	private static Response recv;
	private static Connection singleton;
	private static boolean started = false;
	
	private Connection () {}
	
	public static Connection getInstance () {
		if (singleton == null)
			singleton = new Connection();
		
		return singleton;
	}
	
	public static synchronized void startConnection (String serverIp, String device, int port) {
		Connection.IP = serverIp;
		Connection.DEVICE_ID = device;
		Connection.PORT = port;
		monitor = new Object();
		start = new Object();
		
		//Log.d(TAG, "fac thread nou");
		new Thread(
				new Runnable() {	
					Socket clientSocket;
					OutputStream os;
					ObjectOutputStream oos;
					InputStream is;
					ObjectInputStream ois;
					boolean isDone = false;
					
					@Override
					public void run() {
						//Log.d(TAG, "am pornit threadul");
						
						try {						
							clientSocket = new Socket(IP, PORT);
							os = clientSocket.getOutputStream();
							oos = new ObjectOutputStream(os);
							is = clientSocket.getInputStream();
							ois = new ObjectInputStream(is);
							oos.writeObject(new Online(DEVICE_ID));
							//Log.d(TAG, "deschid socketi output");							
							
							
							while(!isDone) {
								//Log.d(TAG, "intru in while");
								synchronized (Connection.monitor) {
									if (!started) {
										synchronized (start) {
											start.notifyAll();
											//Log.d(TAG, "am pornit wait de la startConnection");
										}
										started = true;
									}
									//Log.d(TAG, "intru in wait");
									try{
										Connection.monitor.wait();
						            } catch(InterruptedException e){
										Log.d(TAG, e.toString());
						            }
									//Log.d(TAG, "incep sa scriu pe socket");
									oos.writeObject(whatToSend);
									if (whatToSend.type == RequestType.GET_MESSAGES || whatToSend.type == RequestType.GET_USERS)
										recv = (Response)ois.readObject();
									else
										recv = null;
									Connection.monitor.notifyAll();

									//Log.d(TAG, "am scris pe socket si dat notify");
									if (whatToSend.type == RequestType.OFFLINE) 
										isDone = true;
								}
							}
							
						} catch (java.net.ConnectException e) {
							// TODO mesaj nu se poate realiza conextiunea catre server 
							Log.d(TAG, e.toString());
						} catch (Exception e) {
							Log.d(TAG, e.toString());
							
							try {
								if (ois != null) ois.close();
								if (is != null) is.close();
								if (oos != null) oos.close();
								if (os != null) os.close();
								if (clientSocket != null) clientSocket.close();	
							} catch (IOException ioe) {
								Log.d(TAG, "Error closing streams.");
							}
						} finally {
							try {
								if (ois != null) ois.close();
								if (is != null) is.close();
								if (oos != null) oos.close();
								if (os != null) os.close();
								if (clientSocket != null) clientSocket.close();	
							} catch (IOException ioe) {
								Log.d(TAG, "Error closing streams.");
							}
							Log.d(TAG, "Succes closing streams.");
						}
					}			
			}).start();
		
		synchronized (start) {
			try {
				//Log.d(TAG, "intru din wait de la startConnection");
				start.wait();
				//Log.d(TAG, "ies din wait de la startConnection");
			} catch (InterruptedException e) {
				Log.d(TAG, "intrerupt din wait de la startConnection");
			}
		}
	}
	
	public static synchronized Response sendThis (Request r) {
		synchronized (Connection.monitor) {
			whatToSend = r;
			Connection.monitor.notifyAll();

			//Log.d(TAG, "trecut de notify la sendthis");
			try {
				Connection.monitor.wait();
			} catch (InterruptedException e) {
				Log.d(TAG, "intrerupt din wait de la sendThis");
			}
		}
		//Log.d(TAG, "trecut de sendthis");
		return recv;
	}

	public static void stopConnection () {
		sendThis(new Offline(DEVICE_ID));
		Log.d(TAG, "stop connection");
	}
	
	
}
