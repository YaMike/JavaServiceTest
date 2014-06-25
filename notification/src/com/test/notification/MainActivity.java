package com.test.notification;

import com.test.notification.notification.NotiController;
import com.test.notification.notification.NotiController.ActionProcessor;
import com.test.notification.notification.iNotiController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	public final String TAG = this.getClass().getName();
	
	private iNotiController notiController;
	
	class EventPrinter implements ActionProcessor {

		@Override
		public void OnDisconnect() {
			final String text = "Disconnect has happened.\n";
			Log.d(TAG, text);
			m_tview.append(text);
		}

		@Override
		public void OnLaunch() {
			final String text = "Launch has been clicked.\n"; 
			Log.d(TAG, text);
			m_tview.append(text);
		}

		@Override
		public void OnClick() {
			final String text = "Notification click has been done.\n"; 
			Log.d(TAG, text);
			m_tview.append(text);
		}
	}
	
	private EventPrinter m_ep = new EventPrinter();
	private TextView m_tview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        if (null == notiController) {
        	notiController = new NotiController(this, m_ep);
        }
    }

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		setupButtons();
        if (null == notiController) {
        	notiController = new NotiController(this, m_ep);
        }
    	notiController.processIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		notiController.processIntent(intent);
	}
	
	private void setupButtons() {
		
		Button startServButton		= (Button)findViewById(R.id.normalState	);
		Button incomingCallButton	= (Button)findViewById(R.id.incomingCall	);
		Button incomingCallTwoButton= (Button)findViewById(R.id.incomingCallTwo	);
		Button errorButton			= (Button)findViewById(R.id.errorBtn		);
		Button showButton			= (Button)findViewById(R.id.show			);
		Button hideButton 			= (Button)findViewById(R.id.hide			);
		
		m_tview 				= (TextView)findViewById(R.id.textView1		);
		
		startServButton.setOnClickListener		(this);
		incomingCallButton.setOnClickListener	(this);
		incomingCallTwoButton.setOnClickListener(this);
		errorButton.setOnClickListener			(this);
		showButton.setOnClickListener			(this);
		hideButton.setOnClickListener			(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.normalState:
		{
			Log.d(TAG, "Set to normal state.");
			notiController.updateState(NotiController.State.SS_NORMAL, "this is a normal state text.");
		}	break;
		case R.id.incomingCall:
		{
			Log.d(TAG, "Set to incoming state with client \"door phone\".");
			notiController.updateState(NotiController.State.SS_CALL, "\"Door phone\" is calling.");			
		}	break;
		case R.id.incomingCallTwo:
		{
			Log.d(TAG, "Set to incoming state with client \"John Smith\".");
			notiController.updateState(NotiController.State.SS_CALL, "\"John Smith\" is calling.");
		}	break;
		case R.id.errorBtn:
		{
			Log.d(TAG, "Set to error state.");
			notiController.updateState(NotiController.State.SS_ERROR, "OMG! We have a problems.");
		}	break;
		case R.id.show:
		{
			notiController.show();
		}	break;
		case R.id.hide:
		{
			notiController.hide();
		}	break;
		default:
		{
			
		}	break;
		}
	}
}
