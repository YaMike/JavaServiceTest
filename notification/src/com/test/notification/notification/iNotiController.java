package com.test.notification.notification;

import com.test.notification.notification.NotiController.State;

import android.content.Intent;

public interface iNotiController {
	public void show();
	public void hide();
	
	public void processIntent(Intent intent);
	public void updateState(State state, String subtitle);
}
