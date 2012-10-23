package com.tgc.texttune;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * The class is called when SMS is received.
 */
public class SMSReceiver extends BroadcastReceiver {

	PhoneNumberDataSource datasource = null;
	ListView lv = null;
	ListAdapter adapter = null;
	ToggleButton toggleButton;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ---get the SMS message passed in---
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		TextTuneActivity tta = TextTuneActivity.instance();
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		String tag = sharedPreferences.getString("TextTag", "song");
		tag = "#" + tag;
		final Toast toastMustTurnOnNull = Toast.makeText(context,
				"TextTune service is not on! (Null)", Toast.LENGTH_LONG);
		final Toast toastMustTurnOnFalse = Toast.makeText(context,
				"TextTune service is not on! (False)", Toast.LENGTH_LONG);
		datasource = new PhoneNumberDataSource(context);
		Log.d("tag ", tag);

		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				String fromNumber = msgs[i].getOriginatingAddress().toString();
				String messageString = msgs[i].getMessageBody().toString();
				Log.d("fromNumber", fromNumber);
				List<PhoneNumber> numbers = datasource.getAllPhoneNumbers();
				List<String> blockedNumbers = new ArrayList<String>();
				for (PhoneNumber num : numbers) {
					blockedNumbers.add(num.getPhoneNumber().toString());
				}
				if (messageString.toLowerCase().contains(tag.toLowerCase())) {
					if (tta == null) {
						Log.d("smsreciever", "service not on... null");
						toastMustTurnOnNull.show();
						return;
					} else {
						if (tta.toggleButton.isChecked()) {
							if (blockedNumbers == null || fromNumber == null) {
								createSongService(messageString, context);
							}
							if (blockedNumbers.contains(fromNumber)) {
								final Toast toastBlockedNumber = Toast
										.makeText(context, fromNumber
												+ " is blocked!",
												Toast.LENGTH_LONG);
								toastBlockedNumber.show();
								Log.d("blocked", fromNumber);
								continue;
							} else {
								createSongService(messageString, context);
							}

						} else {
							Log.d("smsreciever", "service not on... false");
							toastMustTurnOnFalse.show();
						}
					}
				}else{
					Log.d("1", "1");					
				}
			}
		}
	}

	protected ListAdapter createAdapter(Context c) {
		List<PhoneNumber> values = datasource.getAllPhoneNumbers();
		Log.v("values:", Integer.toString(values.size()));
		ArrayAdapter<PhoneNumber> adapter = new ArrayAdapter<PhoneNumber>(c,
				android.R.layout.simple_list_item_1, values);
		return adapter;
	}

	void createSongService(String messageString, Context context) {
		Log.d("smsreceiver", "not blocked");
		String songStr = messageString;
		Intent songServiceIntent = new Intent();
		songServiceIntent.setClassName("com.tgc.texttune",
				"com.tgc.texttune.SongService");
		songServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		songServiceIntent.putExtra("song", songStr);
		Log.d("SMSReciever",
				"about to send intent to SongService from SMSReceiver");
		context.startService(songServiceIntent);
	}
}
