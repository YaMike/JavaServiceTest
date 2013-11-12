package com.example.testservstarter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.testservstarter.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button starter = (Button)findViewById(R.id.ButtonStart);
		starter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent testServIntent = new Intent();
				testServIntent.setAction("com.example.testservice.TestService");
				getApplicationContext().startService(testServIntent);
				Log.i("Autostart", "Starting service...");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
