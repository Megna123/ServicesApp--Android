package net.learn2develop.Services;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	IntentFilter intentFilter;
	private MyService serviceBinder;
	Intent i;
	EditText file1,file2,file3,file4,file5;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        file1=(EditText) findViewById(R.id.file1);
		file2=(EditText) findViewById(R.id.file2);
		file3=(EditText) findViewById(R.id.file3);
		file4=(EditText) findViewById(R.id.file4);
		file5=(EditText) findViewById(R.id.file5);

        
        //---intent to filter for file downloaded intent---
        intentFilter = new IntentFilter();
        intentFilter.addAction("FILE_DOWNLOADED_ACTION");

        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        
        Button btnStart = (Button) findViewById(R.id.btnStartService);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// DEMO 1
            	Intent intent = new Intent(getBaseContext(), MyService.class);
				try {					
					String[] Uris = new String[]{
							new String(file1.getText().toString()),
							new String(file2.getText().toString()),
							new String(file3.getText().toString()),
							new String(file4.getText().toString()),
							new String(file5.getText().toString())
							};
					intent.putExtra("URLs",Uris );
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            	
            	startService(intent);

            	            	            	

            }
        });
        

    }    
    
    private ServiceConnection connection = new ServiceConnection() {
    	public void onServiceConnected(ComponentName className, IBinder service) {
    		//---called when the connection is made---
    		serviceBinder = ((MyService.MyBinder)service).getService();

    		 try {
				 String[] uris = new String[]{
						 new String(file1.getText().toString()),
						 new String(file2.getText().toString()),
						 new String(file3.getText().toString()),
						 new String(file4.getText().toString()),
						 new String(file5.getText().toString())
				 };
				 	serviceBinder.uris = uris;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startService(i);
    	}
    	public void onServiceDisconnected(ComponentName className) {
    	    //---called when the service disconnects---
    		serviceBinder = null;    		
    	}
    };
    
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {  
	        Toast.makeText(getBaseContext(), "File downloaded!", 
	        		Toast.LENGTH_LONG).show();	  
	    }
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(intentReceiver);
	}
}