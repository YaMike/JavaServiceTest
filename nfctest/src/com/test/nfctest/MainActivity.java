package com.test.nfctest;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "NFCTEST";
	private static final String MIME_TEXT_PLAIN = "text/plain";
	private TextView	m_textView;
	private NfcAdapter	m_nfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_textView = (TextView)findViewById(R.id.editText);
		m_nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (m_nfcAdapter == null) {
			Toast.makeText(this, "NFC is not supported", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (!m_nfcAdapter.isEnabled()) {
			m_textView.setText("NFC is disabled");
		}
		handleIntent(getIntent());
	}
	
	@Override
	protected void onResume() {
		Log.w(TAG, "onResume()");
		startProcessing();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.w(TAG, "onPause()");
		stopProcessing();
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		Log.d(TAG, "Got new intent.");
		String action = intent.getAction();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	        String type = intent.getType();
	        if (MIME_TEXT_PLAIN.equals(type)) {
	            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	            new NdefReaderTask().execute(tag);
	        } else {
	            Log.d(TAG, "Wrong mime type: " + type);
	        }
	    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
	        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        String[] techList = tag.getTechList();
	        String searchedTech = Ndef.class.getName();
	        for (String tech : techList) {
	            if (searchedTech.equals(tech)) {
	                new NdefReaderTask().execute(tag);
	                break;
	            }
	        }
	    } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
	    	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    	String[] techList = tag.getTechList();
	    	String techNdef = Ndef.class.getName();
	    	String techMifareUl = MifareUltralight.class.getName();
	    	m_textView.setText("Id: " + bytesToHex(tag.getId()) + "\n"); // big endian?
	    	for (String tech: techList) {
	    		System.out.println(tech);
	    		if (techNdef.equals(tech)) {
	    			new NdefReaderTask().execute(tag);
	    		} else if (techMifareUl.equals(tech)) {
	    			new MifareReaderTask().execute(tag);
	    		}
	    		
	    	}
	    } else {
	    	Log.e(TAG, "Action: " + action + "? O_o");
	    }
	}
	
	private void startProcessing() {
		Log.d(TAG, "Starting foregroung processing.");
		final Intent intent = new Intent(this.getApplicationContext(), this.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
		IntentFilter[] filters = new IntentFilter[1];
		String [][] techLists = new String[][]{};
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		m_nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techLists);
	}
	
	private void stopProcessing() {
		Log.d(TAG,"Stop foreground processing");
		m_nfcAdapter.disableForegroundDispatch(this);
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 3];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 3] = hexArray[v >>> 4];
	        hexChars[j * 3 + 1] = hexArray[v & 0x0F];
	        hexChars[j * 3 + 2] = ' ';
	    }
	    return new String(hexChars);
	}
	
	private abstract class ReaderTask extends AsyncTask<Tag, Void, String> {
		@Override
		protected void onPostExecute(String result) {
	        if (result != null) {
	            m_textView.append(this.getClass().getName() + ": read content: " + result);
	        }
		}
	}
	
	private class MifareReaderTask extends ReaderTask {
		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];
			MifareUltralight mifare = MifareUltralight.get(tag);
			String text = new String();
			try {
				mifare.connect();
				for (int i = 0; i < 16; i++) {
					byte[] bytes = mifare.readPages(i);
					text += bytesToHex(bytes) + "\n";
				}
				return text;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class NdefReaderTask extends ReaderTask {
	    @Override
	    protected String doInBackground(Tag... params) {
	        Tag tag = params[0];
	        Ndef ndef = Ndef.get(tag);
	        if (ndef == null) {
	        	Log.e(TAG, "Not supported tag by ndef");
	            return null;
	        }
	        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
	        NdefRecord[] records = ndefMessage.getRecords();
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
	                try {
	                    return readText(ndefRecord);
	                } catch (UnsupportedEncodingException e) {
	                    Log.e(TAG, "Unsupported Encoding", e);
	                }
	            }
	        }
	        return null;
	    }
	    private String readText(NdefRecord record) throws UnsupportedEncodingException {
	        /*
	         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
	         *
	         * http://www.nfc-forum.org/specs/
	         *
	         * bit_7 defines encoding
	         * bit_6 reserved for future use, must be 0
	         * bit_5..0 length of IANA language code
	         */
	        byte[] payload = record.getPayload();
	        // Get the Text Encoding
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	        // Get the Language Code
	        int languageCodeLength = payload[0] & 0063;
	        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
	        // e.g. "en"
	        // Get the Text
	        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	    }
	}
}
