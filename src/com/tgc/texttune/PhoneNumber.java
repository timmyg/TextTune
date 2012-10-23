package com.tgc.texttune;

public class PhoneNumber {
	private long id;
	private String phoneNumber;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String p) {
		this.phoneNumber = p;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return phoneNumber;
	}
}
