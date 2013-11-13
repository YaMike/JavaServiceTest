package com.example.testservice;

import android.os.Handler;
import android.util.Log;

public class TestNative implements TestListener {
	static {
		System.loadLibrary("log");
		System.loadLibrary("layer-jni");
	}
	
	private static final String TAG = "TestNative";
	private Handler mHandler;
	private TestListener mDelegate;

	TestNative(TestListener t) {
		mDelegate = t;
		mHandler = new Handler();
		startAthread();
	}

	@Override
	public void stringJavaMethod(final String regStr) {
		mHandler.post(new Runnable() {
			public void run() {
				Log.e(TAG, "CALLED!\n");
				mDelegate.stringJavaMethod(regStr);
			}
		});
	}

	/* native interface */
  public native void startAthread();
}
