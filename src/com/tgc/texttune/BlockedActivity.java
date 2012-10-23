package com.tgc.texttune;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlockedActivity extends ListActivity {

	PhoneNumberDataSource datasource = null;
	ListAdapter adapter = null;
	ListView lv = null;
	LinearLayout r  = null;
	Context c = this;
	Button addNumber = null;
	Toast tenDigitsToast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blocked);
		datasource = new PhoneNumberDataSource(this);
		r = (LinearLayout) findViewById(R.id.linlayout);
		tenDigitsToast = Toast.makeText(this, "Phone Number must be exactly 10 digits", Toast.LENGTH_LONG);
		
		lv = getListView();
		addNumber = (Button) r.findViewById(R.id.addnumber1);
		//lv.set
		// Log.v("lv:", lv.toString());
		adapter = createAdapter();
		lv.setAdapter(adapter);
		
		addNumber.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					FrameLayout fl = new FrameLayout(c);
					Builder b = new AlertDialog.Builder(c).setView(fl)
							.setTitle("Enter Phone Number:");
					final EditText input = new EditText(c);
					input.setInputType(InputType.TYPE_CLASS_PHONE);
					InputFilter[] maxLength = new InputFilter[1];
					maxLength[0] = new InputFilter.LengthFilter(10);
					input.setFilters(maxLength);
					b.setView(input);
					b.setPositiveButton("Add",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface d, int which) {
									String p = input.getText()
											.toString();
									if(p.length() != 10){
										tenDigitsToast.show();
										return;
									}
									datasource.open();
									datasource.createPhoneNumber(p);
									datasource.close();
									adapter = createAdapter();
									lv.setAdapter(adapter);
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
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.v("arg3:", String.valueOf(arg3));
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("id:", String.valueOf(id));
				FrameLayout fl = new FrameLayout(c);
				final Builder b = new AlertDialog.Builder(c).setView(fl);
				final PhoneNumber p = (PhoneNumber) lv.getItemAtPosition(position);
				Log.v("p.getPhoneNumber();:", p.getPhoneNumber());
				p.getPhoneNumber();
				b.setTitle("Delete " + p.getPhoneNumber() + "?");
				b.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d,
							int which) {
						datasource.deletePhoneNumber(p);
						adapter = createAdapter();
						lv.setAdapter(adapter);
					}
				});
				b.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d,
							int which) {
						d.dismiss();
					}
				})
				.create()
				.show();
				return false;
			}
		});
	}

	/**
	 * Creates and returns a list adapter for the current list activity
	 * 
	 * @return
	 */
	protected ListAdapter createAdapter() {
		List<PhoneNumber> values = datasource.getAllPhoneNumbers();
		Log.v("values:", Integer.toString(values.size()));
		ArrayAdapter<PhoneNumber> adapter = new ArrayAdapter<PhoneNumber>(this,
				android.R.layout.simple_list_item_1, values);
		return adapter;
	}

	// void updateNumbers() {
	// List<PhoneNumber> values = datasource.getAllPhoneNumbers();
	// ArrayAdapter<PhoneNumber> adapter = new ArrayAdapter<PhoneNumber>(this,
	// android.R.layout.simple_list_item_1, values);
	// setListAdapter(adapter);
	// Log.v("values:", Integer.toString(values.size()));
	// ListPreference a = null;// ListPreference)
	// // findPreference("BLOCKED_NUMBERS");
	// CharSequence[] entries = new CharSequence[values.size()];
	// int i = 0;
	// for (PhoneNumber p : values) {
	// entries[i] = p.getPhoneNumber();
	// i++;
	// }
	// a.setEntries(entries);
	// a.setEntryValues(entries);
	// }

	void refreshUI() { 
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged(); 
    } 
}
