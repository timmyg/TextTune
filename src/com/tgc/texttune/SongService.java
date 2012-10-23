package com.tgc.texttune;

import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SongService extends Service {

	boolean isSent = false;
	Long savedInterval = 30L;
	String savedPlaylistName = "";
	SQLiteDatabase db;
	Toast toastSending = null;
	Toast toastPlaylist = null;
	Toast toastPlaylistSong = null;
	Toast toastSong = null;
	Toast toastWrong = null;
	Toast toastBadToken = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Service binded ...", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initToasts();
	}
	
	public void onResume() {
		super.onCreate();
		initToasts();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		String bundleSong = "";
		if ((String) bundle.get("song") != null)
			bundleSong = (String) bundle.get("song");
		TextTuneActivity tta = TextTuneActivity.instance();
		Log.v("in Song Service ", "");
		if (bundle != null && tta != null) {
			if (tta.toggleButton.isChecked()) {
				if (bundleSong != null && bundleSong.length() > 0) {
					bundleSong = format((String) bundle.get("song"));
					Log.v("song after format ", bundleSong);
				}
				String playlistName = "";
				if (bundle.get("playlistname") != null) {
					playlistName = (String) bundle.get("playlistname");
					playlistName = format(playlistName);
				}
				String newPlaylist = "";
				if (bundle.get("newplaylist") != null) {
					newPlaylist = String.valueOf(bundle.get("newplaylist"));
				}
				createThread(bundleSong, playlistName, newPlaylist);
			}
		}
		return START_NOT_STICKY;
	}

	public boolean sendToYouTubeapi(String song, String playlistName,
			String newPlaylist, String tkn) {
		Log.v("prefs playlistname before", savedPlaylistName);
		LoadPreferences();
		Log.v("playlistName passed in", playlistName);
		Log.v("prefs playlistname after", savedPlaylistName);
		SharedPreferences sp = getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, 0);
		String host = getResources().getString(R.string.prod); // prod
		//String host = getResources().getString(R.string.local); // localhost
		String mins = "60";
		mins = Long.toString(savedInterval);
		if (playlistName == null || playlistName.length() == 0) {
			playlistName = savedPlaylistName;
		}
		Log.v("playlistName", playlistName);
		if(tkn == null || tkn.length() < 10){
			toastBadToken.show();
			return false;
		}
		StringBuilder url = new StringBuilder(host + "song=" + song
				+ "&playlistname=" + playlistName.replace(" ", "+") + "&newplaylist="
				+ newPlaylist + "&mins=" + mins + "&token=" + tkn);
		Log.v("request url", url.toString());
		HttpGet get = new HttpGet(url.toString());
		HttpClient client = new DefaultHttpClient();
		try {
			toastSending.show();
			HttpResponse r = client.execute(get);
			int status = r.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity e = r.getEntity();
				String data = EntityUtils.toString(e);
				Log.v("response ", data);
				if(data.contains("song") && data.contains("playlist")){
					toastPlaylistSong.show();
				}
				else if(data.contains("song")){
					toastSong.show();
				}
				else if(data.contains("playlist")){
					toastPlaylist.show();
				}
				else if(data.contains("token")){
					toastBadToken.show();
				}
				else{
					toastWrong.show();
					Log.v("200wrong ", data);
				}
				return true;
			}
			else{
				HttpEntity e = r.getEntity();
				String data = EntityUtils.toString(e);
				Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
				Log.v("Status not 200 ", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String format(String s) {
		if (s == null) {
			return null;
		}
		s = s.toLowerCase();
		Log.v("s ", s.toString());
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		String tag = sharedPreferences.getString("TextTag", "song").toLowerCase();
		String tag2 = "#" + tag.toLowerCase();
		Log.v("tag ", tag);
		return s.replace(tag2, "").replace(tag, "").trim()
				.replace("\n", "").replace("\"", "").replace(" ", "+");
	}

	public boolean createThread(String song, String playlistName,
			String newPlaylist) {
		try {
			CredentialsThread cThread = new CredentialsThread(this);
			cThread.start();

			// Wait for the thread to finish but don't wait
			// longer than 5 seconds
			long delayMillis = 5000; // 5 seconds
			cThread.join(delayMillis);
			if (cThread.isAlive()) {
				// Timeout occurred; thread has not
				// finished
				isSent = false;
			} else {
				// Thread finished
				isSent = sendToYouTubeapi(song, playlistName, newPlaylist,
						cThread.getToken());
			}
		} catch (InterruptedException e) {
			isSent = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void LoadPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				SettingsActivity.PREFERENCE_FILENAME, MODE_PRIVATE);
		savedPlaylistName = sharedPreferences.getString("PlaylistName",
				"TextTune+Playlist");
		savedInterval = sharedPreferences.getLong("Interval", 60L);
		Log.v("Preferences Loaded",
				savedPlaylistName + " " + Long.toString(savedInterval));
	}

	private void initToasts(){
		if(toastSong==null){
			toastSong = Toast.makeText(this, "Song Successfully Added!", Toast.LENGTH_LONG);
			toastPlaylist = Toast.makeText(this, "Playlist Created!", Toast.LENGTH_LONG);
			toastPlaylistSong = Toast.makeText(this, "Playlist Created and Song Successfully Added!", Toast.LENGTH_LONG);
			toastBadToken = Toast.makeText(this, "Something went wrong with your YouTube token!", Toast.LENGTH_LONG);
			toastWrong = Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG);
			toastSending = Toast.makeText(this, "TextTune: Processing...", Toast.LENGTH_LONG);
		}
	}
}
