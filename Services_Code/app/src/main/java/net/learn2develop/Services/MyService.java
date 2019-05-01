package net.learn2develop.Services;

import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {	
	int counter = 0;
	public String[] uris;
	
	static final int UPDATE_INTERVAL = 1000;
	private Timer timer = new Timer();

	private final IBinder binder = new MyBinder();
	
	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		//return null;
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        Object[] objUrls = (Object[]) intent.getExtras().get("URLs");
		Log.d("check", "onStartCommand: "+objUrls.length);
		String[] urls = new String[objUrls.length];
        for (int i=0; i<objUrls.length; i++) {
        	urls[i] = (String) objUrls[i];
			Log.d("check", "onStartCommand: "+urls[i]);
		}
    	new DoBackgroundTask().execute(urls);
        
		return START_STICKY;
	}	


		
	@Override
    public void onDestroy() {
        super.onDestroy();     
        if (timer != null){
        	timer.cancel();
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
	
	private int DownloadFile(String url,int i) {
		try {
			//---simulate taking some time to download a file---
			int j=i+1;
			DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(url);
			DownloadManager.Request request = new DownloadManager.Request(uri);
			request.setTitle("File"+j);
			request.setDescription("Downloading");
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setVisibleInDownloadsUi(false);
			request.setDestinationUri(Uri.parse("files://Downloads/"));
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/"+"File"+(j)+".pdf");

			downloadmanager.enqueue(request);

			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 100;
	}	

	private class DoBackgroundTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... uris) {
            int count = uris.length;
			Log.d("Check", "doInBackground: "+uris.length);
			long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(uris[i],i);
                //---calculate precentage downloaded and 
                // report its progress---
                publishProgress((int) (((i+1) / (float) count) * 100));                
            }
            return totalBytesDownloaded;
        }

        protected void onProgressUpdate(Integer... progress) {        	            
        	Log.d("Downloading files", 
        			String.valueOf(progress[0]) + "% downloaded");
        	Toast.makeText(getBaseContext(), 
        			String.valueOf(progress[0]) + "% downloaded", 
        			Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(Long result) {
        	Toast.makeText(getBaseContext(), 
        			"Downloaded " + result + " bytes", 
        			Toast.LENGTH_LONG).show();
        	stopSelf();
        }        
	}
}