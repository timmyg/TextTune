package com.tgc.texttune;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PhoneNumberDataSource {
		// Database fields
		private SQLiteDatabase database;
		private DatabaseHelper dbHelper;
		private String[] allColumns = { DatabaseHelper.COLUMN_ID,
				DatabaseHelper.COLUMN_PHONENUMBER};

		public PhoneNumberDataSource(Context context) {
			dbHelper = new DatabaseHelper(context);
		}

		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}

		public void close() {
			dbHelper.close();
		}

		public PhoneNumber createPhoneNumber(String phoneNumber) {
			open();
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_PHONENUMBER, phoneNumber);
			long insertId = database.insert(DatabaseHelper.TABLE_PHONENUMBER, null,
					values);
			Cursor cursor = database.query(DatabaseHelper.TABLE_PHONENUMBER,
					allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			PhoneNumber newPhoneNumber = cursorToPhoneNumber(cursor);
			cursor.close();
			Log.v("inserting", DatabaseHelper.TABLE_PHONENUMBER + " " +insertId + " " + newPhoneNumber.getPhoneNumber());
			close();
			return newPhoneNumber;
		}

		public void deletePhoneNumber(PhoneNumber number) {
			open();
			long id = number.getId();
			System.out.println("PhoneNumber deleted with id: " + id);
			database.delete(DatabaseHelper.TABLE_PHONENUMBER, DatabaseHelper.COLUMN_ID
					+ " = " + id, null);
			close();
		}

		public List<PhoneNumber> getAllPhoneNumbers() {
			List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
			open();
			Cursor cursor = database.query(DatabaseHelper.TABLE_PHONENUMBER,
					allColumns, null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				PhoneNumber phoneNumber = cursorToPhoneNumber(cursor);
				phoneNumbers.add(phoneNumber);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			close();
			return phoneNumbers;
		}

		private PhoneNumber cursorToPhoneNumber(Cursor cursor) {
			PhoneNumber number = new PhoneNumber();
			number.setId(cursor.getLong(0));
			number.setPhoneNumber(cursor.getString(1));
			return number;
		}
	}
