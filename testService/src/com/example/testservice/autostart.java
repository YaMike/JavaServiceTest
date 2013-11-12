package com.example.testservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class autostart extends BroadcastReceiver {
	
	@Override
	public void onReceive(final Context context, Intent arg1) {
		Intent testServIntent = new Intent();
		testServIntent.setAction("com.example.testservice.TestService");
		context.startService(testServIntent);
		Log.i("Autostart", "started on boot..");
	}
}
