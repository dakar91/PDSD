package com.example.mywapp;

import android.support.v7.app.ActionBarActivity;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket; 
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyWhatsapp extends Activity {
	private DiscussArrayAdapter adapter;
	private Button sendMessage;
	private ListView messageList;
	private EditText messageContent;
    private final static String TAG = "MyWhatsApp";
	private String fromImei, toImei, toName;
	private Request whatToSend;
	private boolean isDone;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int PICKFILE_RESULT_CODE = 2;
	private String mCurrentPhotoPath;
 
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_whatsapp);
		Intent i = getIntent();
		fromImei = i.getStringExtra("from_imei");
		toImei = i.getStringExtra("to_imei");
		toName = i.getStringExtra("to_name");		
		this.setTitle(toName);
		mCurrentPhotoPath = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES).getAbsolutePath();
		
		messageList = (ListView)findViewById(R.id.message_list);
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
		messageList.setAdapter(adapter);
		sendMessage = (Button)findViewById(R.id.send_button);
		messageContent = (EditText)findViewById(R.id.message_content);
		
		messageList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
            	try {
            		if (adapter.getType(position) == MessageType.FILE) {
            			Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
            			File file = new File(Environment.getExternalStorageDirectory()
				            	+ File.separator + adapter.getItem(position).comment);
            			String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            			String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            			myIntent.setDataAndType(Uri.fromFile(file),mimetype);
            			startActivity(myIntent);
            		}
            	} catch (Exception e) {
                    Log.d(TAG, e.toString()); 
            	}
             }
    	});
		
		sendMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
            public void onClick(View v) {
				String tmp;
                Connection.getInstance().sendThis(new SendMessage(toImei, fromImei, 
                		new TextMessage(messageContent.getText().toString(), toImei, fromImei)));
				if (!(tmp = messageContent.getText().toString()).equals("")) {
					adapter.add(new Comment(false, tmp, MessageType.TEXT));
					messageContent.setText(null);
				}
            }
        });
		

        
        
		
	}
	
	@Override
    public void onStart() {
		Log.d(TAG, "on start");
    	super.onStart();
    }
 
    @Override
    public void onResume() {
		Log.d(TAG, "on resume");
    	super.onResume();
    	new Thread(new Runnable() {
        	ArrayList<Message> tmp;
        	@Override
        	public void run () {
        		isDone = false;
        		
        		while(!isDone) {
        			try {
						Thread.sleep(1700);
	        			if (!isDone) {
		        			MessagesResponse r = (MessagesResponse)Connection.getInstance().sendThis(new GetMessages(toImei, fromImei));
		        			tmp = r.messages;
		        			if (tmp != null) {		        				
	        					messageList.post(new Runnable() {
	    							@Override
	    							public void run() {
	    								for (Message m : tmp) {	
	    									switch (m.type) {
	    										case TEXT:
	    				        					TextMessage tm = (TextMessage) m;
	    				        					adapter.add(new Comment(true, tm.whatToSend, tm.type));
	    				        					break;
	    										case FILE:
	    											FileMessage fm = (FileMessage) m;
	    											adapter.add(new Comment(true, fm.fileName, fm.type));
	    											File file = new File(Environment.getExternalStorageDirectory()
	    										            	+ File.separator + fm.fileName);
	    											try {
	    												file.createNewFile();
	    												FileOutputStream fos = new FileOutputStream(file);
	    												fos.write(fm.fileData);
	    												fos.close();
	    											} catch (Exception e) {
	    												Log.d(TAG, e.toString());
	    											}
	    											break;
	    									}
	    								}
	    								messageList.smoothScrollToPosition(adapter.getCount() - 1);
	    							}	
	    						});
		        			}
	        			}
					} catch (Exception e) {
						Log.d(TAG, e.toString());
					}
        		}
        	}
        }).start();
    }
 
    @Override
    public void onPause() {
		Log.d(TAG, "on pause");
    	super.onPause();
    }
	
	@Override
	public void onStop() {
		isDone = true;
		Log.d(TAG, "on stop");
    	super.onStop();
    }

	@Override
	public void onDestroy () {
		isDone = true;
		finish();
		super.onDestroy();
	}

	@Override
    public void onRestart() {
		Log.d(TAG, "on restart");
    	super.onRestart();
    } 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.my_whatsapp, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.camera:
	        	dispatchTakePictureIntent();
	            return true;
	        case R.id.attach:
	        	dispatchOpenFileIntent();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    
	    return image;
	}

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	private void dispatchOpenFileIntent () {
		Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("gagt/sdf");
        try {
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String FilePath = new String();
		File transferFile;
		byte fileData[];
		
        if (data == null)
            return;
        switch (requestCode) {
	        case PICKFILE_RESULT_CODE:
	            if (resultCode == RESULT_OK) {
	                FilePath = data.getData().getPath();
	            }
	            break;
	        case REQUEST_IMAGE_CAPTURE:
	        	break;
        }
        try {
	        transferFile = new File(FilePath);
	        fileData = new byte[(int)transferFile.length()];
	        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(transferFile));
	        bin.read(fileData, 0, fileData.length);
	        Connection.getInstance().sendThis(new SendMessage(toImei, fromImei, new FileMessage(fileData, transferFile.getName(), toImei, fromImei)));
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        }
	}
	
    @Override
    public void onSaveInstanceState(Bundle state) {
        // apelarea metodei din activitatea parinte este recomandata, dar nu obligatorie
    	super.onSaveInstanceState(state);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        // apelarea metodei din activitatea parinte este recomandata, dar nu obligatorie
    	super.onRestoreInstanceState(state);
    }






}


