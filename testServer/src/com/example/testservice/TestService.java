/**
 * 
 */
package com.example.testservice;

import java.lang.String;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * @author michael
 *
 */
public class TestService extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private TestController testCtrl = null;
	private static final String TAG = "TestService";
	
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
//			long endTime = System.currentTimeMillis() + 5*1000;
//			while (System.currentTimeMillis() < endTime) {
//				synchronized (this) {
//					try {
//						wait(endTime - System.currentTimeMillis());
//					} catch (Exception e) {
//					}
//				}
//			}
			Log.e(TAG, msg.toString());
			Log.e(TAG, "IT WORKS");
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			//stopSelf(msg.arg1);
		}
	}
	
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {
        Toast.makeText(this, "Test service stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Test has been stopped");
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.i(TAG, "onCreate");
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        
        // Get the HandlerThread's Looper and use it for our Handler 
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startid)
    { 	
    	if (testCtrl != null) {
    		Log.d(TAG, "Service already running.");
    		return;
    	}
        Log.d(TAG, "Starting test controller");
    	testCtrl = new TestController();
    	Log.d(TAG, "Test controller has started");
    } 
}
