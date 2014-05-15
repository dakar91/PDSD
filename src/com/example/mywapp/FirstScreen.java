package com.example.mywapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
 
public class FirstScreen extends Activity{

    private final static String TAG = "MyWhatsApp";
	private final String SERVER_IP = "10.22.3.117";
	private final int PORT = 9000;	
	private String DEVICE_ID;
	private Connection con;
	private Request whatToSend;
	private ArrayList<String> userNames;
	private ArrayList<User> users;
	private ArrayAdapter<String> arrayAdapter;
	private ListView userList;
	private Button btn;
	private boolean isDone = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
		userNames = new ArrayList<String>();
        Log.d(TAG, "onCreate first screen.");
		DEVICE_ID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		Connection.getInstance().startConnection(SERVER_IP, DEVICE_ID, PORT);
		users = new ArrayList<User>();

		userList = (ListView)findViewById(R.id.users);		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userNames);
        userList.setAdapter(arrayAdapter);
        
        userList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                	try {
                		//if (!users.get(position).getImei().equals(DEVICE_ID)) {
		                    Intent nextScreen = new Intent(getApplicationContext(), MyWhatsapp.class);
		                    nextScreen.putExtra("from_imei", DEVICE_ID);
		                    nextScreen.putExtra("to_imei", users.get(position).getImei());
		                    nextScreen.putExtra("to_name", users.get(position).getName());
		                    startActivity(nextScreen); 
                		//}
                	} catch (Exception e) {
                        Log.d(TAG, e.toString()); 
                	}
                 }
        	});
        
		
        
    }
    
    

    @Override
    public void onStart() {
        Log.d(TAG, "onStart first screen."); 
    	super.onStart();
    }
 
    @Override
    public void onResume() {
    	new Thread(new Runnable() {
        	
        	@Override
        	public void run () {
        		isDone = false;
        		
        		while(!isDone) {
        			try {
	        			if (!isDone) {
	        				UsersResponse resp = (UsersResponse)Connection.getInstance().sendThis(new GetUsers(DEVICE_ID));
	        				users = resp.users;
							Log.d(TAG, DEVICE_ID + " sunt dupa cerere imediat ");
	        				
	        				
	        				userList.post(new Runnable() {
    							@Override
    							public void run() {
    								try {
	    								userNames.clear();

    									Log.d(TAG, DEVICE_ID + " momentan am " + users.size());
	    		        				for (User u : users) {
	    		        					//if (!u.getImei().equals(DEVICE_ID)) {
	    		        						userNames.add(u.getName());
	    		        					//}
        									Log.d(TAG, DEVICE_ID + " " + u.getImei());
	    		        				}
	    		        				arrayAdapter.notifyDataSetChanged();

    								} catch (Exception e) {
    									Log.d(TAG, e.toString());
    								}
    							}	
    						});
	        			}
						Thread.sleep(5000);
					} catch (Exception e) {
						Log.d(TAG, e.toString());
					}
        		}
        	}
        }).start();
    	
        Log.d(TAG, "onResume first screen."); 
    	super.onResume();
    }
 
    @Override
    public void onPause() {
    	isDone = true;
        Log.d(TAG, "onPause first screen."); 
    	super.onPause();
    }
 
    @Override
    public void onStop() {
        Log.d(TAG, "onStop first screen."); 
    	super.onStop();
    }
 
    @Override
    public void onDestroy() {
		Connection.getInstance().stopConnection();
        Log.d(TAG, "onDestroy first screen."); 
    	super.onDestroy();
    }
 
    @Override
    public void onRestart() {
    	super.onRestart();
    }
 
    @Override
    public void onSaveInstanceState(Bundle state) {
        // apelarea metodei din activitatea parinte este recomandata, dar nu obligatorie
    	super.onSaveInstanceState(state);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle state) {
        // apelarea metodei din activitatea parinte este recomandata, dar nu obligatorie
    	super.onRestoreInstanceState(state);
    }

}
