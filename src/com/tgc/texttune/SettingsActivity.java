package com.tgc.texttune;

import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	/** Called when the activity is first created. */

	private static SettingsActivity ttSettingsInst;
	String minutesInterval = "30";
	String playlistName = "TextTune Playlist";
	public static final String PREFERENCE_FILENAME = "AppGamePrefs";
	private PhoneNumberDataSource datasource;
	Context c = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		c = this;
		addPreferencesFromResource(R.layout.settings);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		sp.registerOnSharedPreferenceChangeListener(this);
		datasource = new PhoneNumberDataSource(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		datasource = new PhoneNumberDataSource(this);
		// Get a link to your preferences:
		SharedPreferences s = getSharedPreferences(PREFERENCE_FILENAME, 0);

		// Create a editor to edit the preferences:
		SharedPreferences.Editor editor = s.edit();
		Log.v("key11:", key);
		if (key.contains("RESTORE")) { //not working
			editor.putLong("Interval", 60L);
			editor.putString("PlaylistName", "TextTune Playlist");
			editor.putString("TextTag", "song");
			Log.v("restored defaults:", "");
		}
		if (key.equals("INTERVAL_VARIABLE")) {
			editor.putLong("Interval", Long.valueOf(sp.getString(key, null)));
		}
		if (key.equals("PLAYLIST_NAME_VARIABLE")) {
			editor.putString("PlaylistName", sp.getString(key, null));
		}
		if (key.equals("TAG_VARIABLE")) {
			editor.putString("TextTag", sp.getString(key, null));
			Log.v("tag:", "");
		}
		editor.commit();
	}

	void createDialog() {
		Log.v("hurr", "");
		FrameLayout fl = new FrameLayout(c);
		AlertDialog.Builder b = new AlertDialog.Builder(c).setView(fl);
		b.setTitle("Restore Defaults?");
				b.setPositiveButton("Restore",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d,
									int which) {
								Log.v("restore clicked:", "");
							}
						});
				b.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d,
									int which) {
								Log.v("cancel clicked:", "");
								d.dismiss();
							}
						});
				b.create();
				b.show();
		
	}
	

}