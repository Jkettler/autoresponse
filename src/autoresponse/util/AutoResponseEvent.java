package autoresponse.util;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AutoResponseEvent implements Parcelable {

	public static final String EVENT_KEY = "AUTORESPONSEEVENT";
	private static final String TAG = "AutoResponseEvent";

	// I ignored the mName convention since this isn't strictly an android class

	private String name;

	// Context instance variables
	private boolean ifDriving;
	private boolean ifTime;
	private int startMinuteOfDay;
	private int endMinuteOfDay;
	private boolean ifDay;
	private String days;
	private boolean ifLocation;
	private String location;
	private boolean ifRecieveText;

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
		name = parcel.readString();

		ifDriving = parcel.readInt() == 0;
		ifTime = parcel.readInt() == 0;
		startMinuteOfDay = parcel.readInt();
		endMinuteOfDay = parcel.readInt();
		ifDay = parcel.readInt() == 0;
		days = parcel.readString();
		ifLocation = parcel.readInt() == 0;
		location = parcel.readString();
		ifRecieveText = parcel.readInt() == 0;

		changePhoneMode = parcel.readInt() == 0;
		phoneMode = parcel.readInt();
		displayReminder = parcel.readInt() == 0;
		reminderTime = parcel.readInt();
		sendTextResponse = parcel.readInt() == 0;
		textResponse = parcel.readString();
	}

	public AutoResponseEvent() {

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

		dest.writeString(name);
		
		// For reasons that escape me, there is no writeBoolean function, so
		// I'm cheating and using a ternary here and a comparison in the
		// constructor
		dest.writeInt((ifDriving ? 0 : 1));
		dest.writeInt((ifTime ? 0 : 1));
		dest.writeInt(startMinuteOfDay);
		dest.writeInt(endMinuteOfDay);
		dest.writeInt((ifDay ? 0 : 1));
		dest.writeString(days);
		dest.writeInt((ifLocation ? 0 : 1));
		dest.writeString(location);
		dest.writeInt((ifRecieveText ? 0 : 1));

		dest.writeInt((changePhoneMode ? 0 : 1));
		dest.writeInt(phoneMode);
		dest.writeInt((displayReminder ? 0 : 1));
		dest.writeInt(reminderTime);
		dest.writeInt((sendTextResponse ? 0 : 1));
		dest.writeString(textResponse);

	}

	// This field is needed for Android to be able to create new objects,
	// individually or as arrays.
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public AutoResponseEvent createFromParcel(Parcel in) {
			return new AutoResponseEvent(in);
		}

		public AutoResponseEvent[] newArray(int size) {
			return new AutoResponseEvent[size];
		}
	};

	// End parcelable code

	public boolean isValidContext() {
		return ifDriving || ifTime || (ifDay && days != null)
				|| (ifLocation && location != null) && name != null;
	}

	// Getters and Setters

	public boolean isIfRecieveText() {
		return ifRecieveText;
	}

	public void setIfRecieveText(boolean ifRecieveText) {
		this.ifRecieveText = ifRecieveText;
	}

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

	public int getStartMinuteOfDay() {
		return startMinuteOfDay;
	}

	public void setStartMinuteOfDay(int startMinuteOfDay) {
		this.startMinuteOfDay = startMinuteOfDay;
	}

	public int getEndMinuteOfDay() {
		return endMinuteOfDay;
	}

	public void setEndMinuteOfDay(int endMinuteOfDay) {
		this.endMinuteOfDay = endMinuteOfDay;
	}

	public boolean isIfDay() {
		return ifDay;
	}

	public void setIfDay(boolean ifDay) {
		this.ifDay = ifDay;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
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

	@Override
	public boolean equals(Object obj) {
		boolean match = true;

		if (obj instanceof AutoResponseEvent) {
			AutoResponseEvent a = (AutoResponseEvent) obj;
			match &= this.changePhoneMode == a.changePhoneMode;
			match &= this.days == a.days;
			match &= this.displayReminder == a.displayReminder;
			match &= this.endMinuteOfDay == a.endMinuteOfDay;
			match &= this.ifDay == a.ifDay;
			match &= this.ifDriving == a.ifDriving;
			match &= this.ifLocation == a.ifLocation;
			match &= this.ifRecieveText == a.ifRecieveText;
			match &= this.ifTime == a.ifTime;
			match &= this.location == a.location;
			match &= this.name == a.name;
			match &= this.phoneMode == a.phoneMode;
			match &= this.reminderTime == a.reminderTime;
			match &= this.sendTextResponse == a.sendTextResponse;
			match &= this.startMinuteOfDay == a.startMinuteOfDay;
			match &= this.textResponse == a.textResponse;

			Log.d(TAG, "phonemode " + this.changePhoneMode + ": "
					+ a.changePhoneMode);
			Log.d(TAG, "days " + this.days + ": " + a.days);
			Log.d(TAG, "dispremndr " + this.displayReminder + ": "
					+ a.displayReminder);
			Log.d(TAG, "endminute " + this.endMinuteOfDay + ": "
					+ a.endMinuteOfDay);
			Log.d(TAG, "day " + this.ifDay + ": " + a.ifDay);
			Log.d(TAG, "driving " + this.ifDriving + ": " + a.ifDriving);
			Log.d(TAG, "iflocation " + this.ifLocation + ": " + a.ifLocation);
			Log.d(TAG, "recieive " + this.ifRecieveText + ": "
					+ a.ifRecieveText);
			Log.d(TAG, "iftime " + this.ifTime + ": " + a.ifTime);
			Log.d(TAG, "location " + this.location + ": " + a.location);
			Log.d(TAG, "name " + this.name + ": " + a.name);
			Log.d(TAG, "mode " + this.phoneMode + ": " + a.phoneMode);
			Log.d(TAG, "remndr time " + this.reminderTime + ": "
					+ a.reminderTime);
			Log.d(TAG, "sentext " + this.sendTextResponse + ": "
					+ a.sendTextResponse);
			Log.d(TAG, "start " + this.startMinuteOfDay + ": "
					+ a.startMinuteOfDay);
			Log.d(TAG, "textresp " + this.textResponse + ": " + a.textResponse);
		}
		return match;
	}

	public String getTimeString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSendTextResponseString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDayString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocationString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRecieveTextString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getChangePhoneModeString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayReminderString() {
		// TODO Auto-generated method stub
		return null;
	}
}
