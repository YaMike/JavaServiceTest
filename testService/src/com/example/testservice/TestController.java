package com.example.testservice;

import android.util.Log;

public class TestController implements TestListener {
	private static final String TAG = "TestController";
	private TestNative mTestNative = null;
	
	TestController() {
    	Log.d(TAG, "Starting test native");
    	mTestNative = new TestNative(this);
	}

	@Override
	public void stringJavaMethod(String regStr) {
		Log.d(TAG, "Callback called!!!!\n");
		Log.e(TAG, regStr);
	}
}
