package com.tgc.texttune;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TextTuneActivity extends Activity {
	/** Called when the activity is first created. */

	RelativeLayout rlayout;
	ToggleButton toggleButton;
	Button addSongButton;
	Button newPlaylistButton;
	Button myPlaylistsButton;
	Button browsePlaylistsButton;

	private static TextTuneActivity ttActivityInst;
	
	Drawable notesBackground = null;
	Drawable toggleOn = null;
	Drawable note = null;
	Drawable playlist = null;
	Drawable playlists = null;
	Drawable browsePlaylists = null;
	Drawable notesBackgroundDk = null;
	Drawable toggleOff = null;
	Drawable noteDk = null;
	Drawable playlistDk = null;
	Drawable playlistsDk = null;
	Drawable browsePlaylistsDk = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ttActivityInst = this;
		final Context context = this;

		// layout and buttons
		rlayout = (RelativeLayout) findViewById(R.id.rellayout);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		addSongButton = (Button) findViewById(R.id.addsong);
		newPlaylistButton = (Button) findViewById(R.id.newplaylist);
		myPlaylistsButton = (Button) findViewById(R.id.myplaylists);
		browsePlaylistsButton = (Button) findViewById(R.id.browseplaylists);

		// EditTextPreference a =
		// (EditTextPreference)findViewById(R.id.browseplaylists);

		// resources
		notesBackground = getResources().getDrawable(
				R.drawable.notes_background);
		toggleOn = getResources().getDrawable(R.drawable.on);
		note = getResources().getDrawable(R.drawable.note);
		playlist = getResources().getDrawable(
				R.drawable.playlist);
		playlists = getResources().getDrawable(
				R.drawable.playlists);
		browsePlaylists = getResources().getDrawable(
				R.drawable.search);
		notesBackgroundDk = getResources().getDrawable(
				R.drawable.notes_background_dk);
		toggleOff = getResources().getDrawable(R.drawable.off);
		noteDk = getResources().getDrawable(R.drawable.note_dk);
		playlistDk = getResources().getDrawable(
				R.drawable.playlist_dk);
		playlistsDk = getResources().getDrawable(
				R.drawable.playlists_dk);
		browsePlaylistsDk = getResources().getDrawable(
				R.drawable.search_dk);

		// toasts
		final Toast toastOn = Toast.makeText(TextTuneActivity.this,
				"TextTune Service is on!", Toast.LENGTH_SHORT);
		toastOn.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
		final Toast toastOff = Toast.makeText(TextTuneActivity.this,
				"TextTune Service is off", Toast.LENGTH_SHORT);
		toastOff.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
		final Toast toastMustTurnOn = Toast.makeText(TextTuneActivity.this,
				"Turn TextTune Service on! Tap the Lock!", Toast.LENGTH_SHORT);
		toastMustTurnOn.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);

		// get settings
		// SharedPreferences settings =
		// getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);

		toggleButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					addSongButton.setBackgroundDrawable(note);
					toggleButton.setBackgroundDrawable(toggleOff);
					newPlaylistButton.setBackgroundDrawable(playlist);
					myPlaylistsButton.setBackgroundDrawable(playlists);
					browsePlaylistsButton
							.setBackgroundDrawable(browsePlaylists);
					rlayout.setBackgroundDrawable(notesBackground);

					toastOn.show();
				} else {
					toggleButton.setBackgroundDrawable(toggleOn);
					addSongButton.setBackgroundDrawable(noteDk);
					newPlaylistButton.setBackgroundDrawable(playlistDk);
					myPlaylistsButton.setBackgroundDrawable(playlistsDk);
					browsePlaylistsButton
							.setBackgroundDrawable(browsePlaylistsDk);
					rlayout.setBackgroundDrawable(notesBackgroundDk);

					toastOff.show();
				}
			}
		});

		addSongButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					FrameLayout fl = new FrameLayout(context);
					Builder b = new AlertDialog.Builder(context).setView(fl)
							.setTitle("Enter Song");
					final EditText input = new EditText(context);
					b.setView(input);
					b.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface d, int which) {
									d.dismiss();
									String song = input.getText().toString();
									Log.v("Song Entered:", song.toString());
									SharedPreferences sharedPreferences = context.getSharedPreferences(
											SettingsActivity.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
									String tag = sharedPreferences.getString("TextTag", "song");
									song = tag + song;
									Intent newSongIntent = new Intent();
									newSongIntent.setClassName(
											"com.tgc.texttune",
											"com.tgc.texttune.SongService")
									// .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
											.putExtra("song", song);
									Log.d("SMSReciever",
											"about to send intent to SongService from TTActivity");
									startServiceHelper(newSongIntent);
									// bindService(service, conn, flags)
									d.dismiss();
								}
							})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface d,
												int which) {
											d.dismiss();
										}
									}).create().show();
					// add song
				} else {
					toastMustTurnOn.show();
				}
			}
		});

		newPlaylistButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					FrameLayout fl = new FrameLayout(context);
					Builder b = new AlertDialog.Builder(context).setView(fl)
							.setTitle("Enter Playlist Name");
					final EditText input = new EditText(context);
					b.setView(input);
					b.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface d, int which) {
									d.dismiss();
									String playlist = input.getText()
											.toString();
									Log.v("New Playlist Name:",
											playlist.toString());
									Intent newPlaylistIntent = new Intent();
									newPlaylistIntent
											.setClassName("com.tgc.texttune",
													"com.tgc.texttune.SongService")
											.setFlags(
													Intent.FLAG_ACTIVITY_NEW_TASK)
											.putExtra("playlistname", playlist)
											.putExtra("newplaylist", 1);
									Log.d("SMSReciever",
											"about to send newPlaylistIntent to SongService from TTActivity");
									startServiceHelper(newPlaylistIntent);
									// bindService(service, conn, flags)
									d.dismiss();
								}
							})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface d,
												int which) {
											d.dismiss();
										}
									}).create().show();
				} else {
					toastMustTurnOn.show();
				}
			}
		});
		myPlaylistsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					String url = "http://www.youtube.com/view_all_playlists";
					Intent i = new Intent(Intent.ACTION_VIEW);
					//i.setClassName("com.google.android.youtube", "com.google.android.youtube.app.froyo.phone.PlaylistActivity");
					i.setData(Uri.parse(url));
					startActivity(i);
				} else {
					toastMustTurnOn.show();
				}
			}
		});
		browsePlaylistsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					FrameLayout fl = new FrameLayout(context);
					Builder b = new AlertDialog.Builder(context).setView(fl)
							.setTitle("Search for Public Playlist:");
					final EditText input = new EditText(context);
					b.setView(input);
					b.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface d, int which) {
									String playlist = input.getText()
											.toString();
									Log.v("Browse Playlist entered:",
											playlist.toString());
									String url = "http://www.youtube.com/results?search_query="
											+ format(playlist) + "%2C+playlist";
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(url));
									startActivity(i);
									d.dismiss();
								}
							})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface d,
												int which) {
											d.dismiss();
										}
									}).create().show();
					// add song
				} else {
					toastMustTurnOn.show();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ttmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v("menu id:", Integer.toString(item.getItemId()));
		switch (item.getItemId()) {

		case R.id.about:
			showAbout();
			return true;
		case R.id.help:
			showHelp();
			return true;
		case R.id.block:
			showBlockDialog();
			return true;
		case R.id.settings:
			showSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showAbout() {
		final TextView message = new TextView(this);
		  message.setText(R.string.about_message);
		  message.setTextColor(Color.WHITE);
		  message.setMovementMethod(LinkMovementMethod.getInstance());
	
		  new AlertDialog.Builder(this)
		   .setTitle(R.string.about_title)
		   .setCancelable(true)
		   .setIcon(android.R.drawable.ic_dialog_info)
		   .setPositiveButton(R.string.ok, null)
		   .setView(message)
		   .create().show();
	}

	public void showBlockDialog() {
		Intent intent = new Intent();
		intent.setClass(this, BlockedActivity.class);
		startActivity(intent);
	}

	public void showHelp() {
		  final TextView message = new TextView(this);
		  message.setText(R.string.help_message);
		  message.setTextColor(Color.WHITE);
		  message.setMovementMethod(LinkMovementMethod.getInstance());
	
		  new AlertDialog.Builder(this)
		   .setTitle(R.string.help_title)
		   .setCancelable(true)
		   .setIcon(android.R.drawable.ic_dialog_info)
		   .setPositiveButton(R.string.ok, null)
		   .setView(message)
		   .create().show();
	}

	public void showSettings() {
		Intent settingsActivityIntent = new Intent();
		settingsActivityIntent.setClass(this, SettingsActivity.class);
		Log.d("TTA", "about to send intent to SettingsActivity from TTActivity");
		startActivity(settingsActivityIntent);
	}

	public String format(String s) {
		if (s == null) {
			return null;
		}
		return s.trim().replace(" ", "+");
	}

	public void startServiceHelper(Intent intent) {
		this.startService(intent);
	}

	public static TextTuneActivity instance() {
		return ttActivityInst;
	}
	
	@Override
	public void onResume()
	{
	    Log.v("Resuming", "Resuming tta");
	    super.onResume();
	    loadChecked();
	    
	    if (toggleButton.isChecked()) {
			addSongButton.setBackgroundDrawable(note);
			toggleButton.setBackgroundDrawable(toggleOff);
			newPlaylistButton.setBackgroundDrawable(playlist);
			myPlaylistsButton.setBackgroundDrawable(playlists);
			browsePlaylistsButton
					.setBackgroundDrawable(browsePlaylists);
			rlayout.setBackgroundDrawable(notesBackground);
		} else {
			toggleButton.setBackgroundDrawable(toggleOn);
			addSongButton.setBackgroundDrawable(noteDk);
			newPlaylistButton.setBackgroundDrawable(playlistDk);
			myPlaylistsButton.setBackgroundDrawable(playlistsDk);
			browsePlaylistsButton
					.setBackgroundDrawable(browsePlaylistsDk);
			rlayout.setBackgroundDrawable(notesBackgroundDk);
		}
	}
	
	@Override
	public void onPause()
	{
	    super.onPause();
	    Log.v("Pausing", "Pausing tta");
	    saveChecked(toggleButton.isChecked());
	}
	
	private void loadChecked() { 
	    SharedPreferences sharedPreferences = this.getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
//	    Toast.makeText(this, "load " + sharedPreferences.getBoolean("checked", false),Toast.LENGTH_SHORT ).show();
	    toggleButton.setChecked(sharedPreferences.getBoolean("checked", false));
	    
	}
	
	private void saveChecked(boolean isChecked) {
	    SharedPreferences sharedPreferences = this.getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putBoolean("checked", isChecked);
	    editor.commit();
//	    Toast.makeText(this, "saved " + isChecked,Toast.LENGTH_SHORT ).show();
	}
}
