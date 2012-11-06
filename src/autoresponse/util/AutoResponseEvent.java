package autoresponse.util;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AutoResponseEvent implements Parcelable {

	// I ignored the mName convention since this isn't strictly an android class

	private String name;
	
	// Context instance variables
	private boolean ifDriving;
	private boolean ifTime;
	private int timeOfDay;
	private boolean ifDay;
	private List<Integer> days;
	private boolean ifLocation;
	private String location;

	// Response instance variables
	private boolean changePhoneMode;
	private int phoneMode;
	private boolean displayReminder;
	private int reminderTime;
	private boolean sendTextResponse;
	private String textResponse;

	// Parcelable Code -for a good explanation of how this works, go to
	// http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/

	public AutoResponseEvent(Parcel parcel) {
		// Read values from parcel in the order that writeToParcel puts them in
		// ORDER MATTERS

		ifDriving = parcel.readInt() == 0;
		ifTime = parcel.readInt() == 0;
		timeOfDay = parcel.readInt();
		ifDay = parcel.readInt() == 0;
		parcel.readList(days, null);
		ifLocation = parcel.readInt() == 0;
		location = parcel.readString();

		changePhoneMode = parcel.readInt() == 0;
		phoneMode = parcel.readInt();
		displayReminder = parcel.readInt() == 0;
		reminderTime = parcel.readInt();
		sendTextResponse = parcel.readInt() == 0;
		textResponse = parcel.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// Given dest, make it a duplicate of this object
		// by writing values to it, ie do dest.writeString(variable)

		// You can call writeString (and the like) multiple times with
		// different variables, so long as you use readString to pull them out
		// in the same order inside the constructor

		// For reasons that escape me, there is no writeBoolean function, so
		// I'm cheating and using a ternary here and a comparison in the
		// constructor
		dest.writeInt((ifDriving ? 0 : 1));
		dest.writeInt((ifTime ? 0 : 1));
		dest.writeInt(timeOfDay);
		dest.writeInt((ifDay ? 0 : 1));
		dest.writeList(days);
		dest.writeInt((ifLocation ? 0 : 1));
		dest.writeString(location);

		dest.writeInt((changePhoneMode ? 0 : 1));
		dest.writeInt(phoneMode);
		dest.writeInt((displayReminder ? 0 : 1));
		dest.writeInt(reminderTime);
		dest.writeInt((sendTextResponse ? 0 : 1));
		dest.writeString(textResponse);

	}

//	  This field is needed for Android to be able to create new objects,
//	  individually or as arrays.
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public AutoResponseEvent createFromParcel(Parcel in) {
			return new AutoResponseEvent(in);
		}

		public AutoResponseEvent[] newArray(int size) {
			return new AutoResponseEvent[size];
		}
	};

	// End parcelable code

	public boolean isValidContext(){
		return ifDriving || ifTime || (ifDay && days != null) || (ifLocation && location!= null) && name !=null ;
	}
	
	// Getters and Setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIfDriving() {
		return ifDriving;
	}

	public void setIfDriving(boolean ifDriving) {
		this.ifDriving = ifDriving;
	}

	public boolean isIfTime() {
		return ifTime;
	}

	public void setIfTime(boolean ifTime) {
		this.ifTime = ifTime;
	}

	public int getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public boolean isIfDay() {
		return ifDay;
	}

	public void setIfDay(boolean ifDay) {
		this.ifDay = ifDay;
	}

	public List<Integer> getDays() {
		return days;
	}

	public void setDays(List<Integer> days) {
		this.days = days;
	}

	public boolean isIfLocation() {
		return ifLocation;
	}

	public void setIfLocation(boolean ifLocation) {
		this.ifLocation = ifLocation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isChangePhoneMode() {
		return changePhoneMode;
	}

	public void setChangePhoneMode(boolean changePhoneMode) {
		this.changePhoneMode = changePhoneMode;
	}

	public int getPhoneMode() {
		return phoneMode;
	}

	public void setPhoneMode(int phoneMode) {
		this.phoneMode = phoneMode;
	}

	public boolean isDisplayReminder() {
		return displayReminder;
	}

	public void setDisplayReminder(boolean displayReminder) {
		this.displayReminder = displayReminder;
	}

	public int getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(int reminderTime) {
		this.reminderTime = reminderTime;
	}

	public boolean isSendTextResponse() {
		return sendTextResponse;
	}

	public void setSendTextResponse(boolean sendTextResponse) {
		this.sendTextResponse = sendTextResponse;
	}

	public String getTextResponse() {
		return textResponse;
	}

	public void setTextResponse(String textResponse) {
		this.textResponse = textResponse;
	}
}
