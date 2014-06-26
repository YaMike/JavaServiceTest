package com.test.notification.notification;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;

import com.test.notification.MainActivity;
import com.test.notification.R;

public class NotiController implements iNotiController {
	
	private static final int NOTIFICATION_ID = 1;
	private MediaPlayer mp;
	private Ringtone r;
	
	public enum State {
		SS_CALL 		(1),
		SS_NORMAL	(2),
		SS_ERROR		(3);
		
		private int num;
		
		State(int num) {
			this.num = num;
		}
		
		public int intValue() {
			return num;
		}
	}

	public interface ActionProcessor {
		public void OnDisconnect();
		public void OnLaunch();
		public void OnClick();
	}
	
	enum Type {
		ST_DISCONNECT	(1),
		ST_LAUNCH		(2),
		ST_CLICK		(3);
		
		private int num;
		private static SparseArray<Type> sa = new SparseArray<Type>();
		
		Type(int i) {
			this.num = i;
		}
		
		public int intValue() {
			return num;
		}
		
		public static Type getTypeById(int id) {
			return sa.get(id);
		}
		
		static {
			for (Type t: Type.values()) {
				sa.append(t.intValue(), t);
			}
		}
	}

	private final String TAG = "TronService::NotificationController";
	private Context m_context;
	private ActionProcessor m_processor;
	
	private String m_title = "THRONE";
	private String m_subtitle = "";
	private State m_state = State.SS_NORMAL;

	public NotiController(Context context, ActionProcessor actProcessor) {
		m_context = context;
		m_processor = actProcessor;
	}

	@Override
	public synchronized void show() {
		Log.d(TAG,"Create notification");
		NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(m_context)
		.setSmallIcon(R.drawable.throne_normal)
		.setContentTitle(m_title)
		.setContentText(m_subtitle);
		
		switch (m_state) {
		case SS_NORMAL:
		{
			notiBuilder.setSmallIcon(R.drawable.throne_normal);
		}	break;
		case SS_CALL:
		{
			notiBuilder.setSmallIcon(R.drawable.throne_call);
		}	break;
		case SS_ERROR:
		{
			notiBuilder.setSmallIcon(R.drawable.throne_error);
		}	break;
		}

		/* setup pending intent for static message click */
		//			Intent onNotiClickIntent = m_context.getPackageManager().getLaunchIntentForPackage(appStr);
		Intent onNotiClickIntent = new Intent(m_context, m_context.getClass());

		onNotiClickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		onNotiClickIntent.setPackage(null);
		onNotiClickIntent.putExtra("Notification", Type.ST_CLICK.intValue());

		PendingIntent pendingContentIntent = PendingIntent.getActivity(m_context, 1, onNotiClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notiBuilder.setContentIntent(pendingContentIntent);

		/* setup pending intent for "Clear" button */
		Intent onClearIntent = new Intent(m_context, m_context.getClass());

		onClearIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		onClearIntent.setPackage(null);
		onClearIntent.putExtra("Notification", Type.ST_DISCONNECT.intValue());

		PendingIntent pendingOnClearIntent = PendingIntent.getActivity(m_context,  2, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notiBuilder.addAction(R.drawable.throne_diconnect,"disconnect", pendingOnClearIntent);

		/* setup pending intent for "Launch" button */
		Intent onLaunchIntent = new Intent(m_context, m_context.getClass());
		onLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		onLaunchIntent.setPackage(null);
		onLaunchIntent.putExtra("Notification", Type.ST_LAUNCH.intValue());

		PendingIntent pendingOnLaunchIntent = PendingIntent.getActivity(m_context, 3, onLaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notiBuilder.addAction(R.drawable.throne_open, "launch Throne", pendingOnLaunchIntent);

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notiBuilder.setSound(alarmSound);
		
		stopSound();
		
		if (m_state == State.SS_CALL) {

			try {
				mp = MediaPlayer.create(m_context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

				AudioManager audioManager = (AudioManager) m_context.getSystemService(Context.AUDIO_SERVICE);
				mp.setVolume(	Float.parseFloat(Double.toString(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0)),
								Float.parseFloat(Double.toString(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0)));
				
				mp.setLooping(true);
				mp.start();
				
			} catch (IllegalStateException is) {
				is.printStackTrace();
			} catch (NumberFormatException nf) {
				nf.printStackTrace();
			}
		}
		
		Notification noti = notiBuilder.build();
		noti.flags |= Notification.FLAG_NO_CLEAR;
		
		NotificationManager noMan = (NotificationManager)m_context.getSystemService(Context.NOTIFICATION_SERVICE);
		noMan.notify(NOTIFICATION_ID, noti);
	}
	
	public void stopSound() {
		if (mp != null) {
			mp.stop();
			mp.reset();
			mp.release();
			mp = null;
		}
	}

	public synchronized void hide() {
		NotificationManager noMan = (NotificationManager)m_context.getSystemService(Context.NOTIFICATION_SERVICE);
		noMan.cancel(NOTIFICATION_ID);
	}

	public synchronized void updateState(State state, String subtitle) {
		m_state = state;
		m_subtitle = subtitle;
		show();
	}

	@Override
	public void processIntent(Intent intent) {
		
        Bundle bundle = intent.getExtras();
        
        if (null == bundle) {
        	Log.d(TAG, "Intent's bundle is null. No processing.");
        	return;
        }
        
        Object value = bundle.get("Notification");
        
        if (null == value) {
        	Log.d(TAG, "No value");
        } else {
        	Type t = Type.getTypeById((Integer)value);
        	Log.d(TAG, "Value is: " + t);
        	switch (t) {
        	case ST_DISCONNECT:
        	{
        		m_processor.OnDisconnect();
        	}	break;
        	case ST_LAUNCH:
        	{
        		m_processor.OnLaunch();
        	}	break;
        	case ST_CLICK:
        	{
        		m_processor.OnClick();
        	}	break;
        	}
        }		
	}
}
