package autoresponse.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	
	// Global variables
	private static final String TAG = "AUTO_RESPONSE";
	private BroadcastReceiver timeReceiver = null;
	private BroadcastReceiver smsReceiver = null;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	
	// ints used to index array for time of day
	private final int HOUR = 0;
	private final int MINUTE = 1;
	private final int SECOND = 2;
	
	private final int MODE_SILENT = 0;
	private final int MODE_VIBRATE = 1;
	private final int MODE_NORMAL = 2;
	
	// an array of events that are being monitored 
	private ArrayList<AutoResponseEvent> events;
	
	// last known latitude and longitude
	private static double latitude;
	private static double longitude;
	
	// last phone number that sent an SMS to this device
	private static String lastReceivedSMSAddress;
	
	// End of Global variables
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Service: onCreate");
		
		// TODO: read in data from files and create AutoResponseEvent objects
		//   with the data, and add these objects to events (a global ArrayList)
		
		// register for updates if needed
		// TODO: only make these method calls if events require.
		registerTimeReceiver();
		registerSMSReceiver();
		registerLocationManager();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "Service: onStart");
		// nothing to do
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// nothing to do
		Log.d(TAG, "Service: onBind");
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service: onDestroy");
		
		// Unregister receivers and location manager
		if(timeReceiver != null) {
			unregisterReceiver(timeReceiver);
			timeReceiver = null;
		}
		if(smsReceiver != null) {
			unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
		if(locationManager != null) {
			locationManager.removeUpdates(locationListener);
			locationManager = null;
		}
	}
	
	private void registerLocationManager() {
		/*
		 * get LocationManager (a system service) and set up a listener to respond when
		 * the location has changed.
		 * 
		 * Note: the appropriate permissions need to be listed in the manifest
		 */
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// This is the method that will be called when the location has changed
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				makeToast("Location changed");
			}
			// Other methods are note used, but must be implemented.
			public void onProviderDisabled(String provider) {}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status,Bundle extras) {}
		};
		
		int millisec = 10*1000; // 10 seconds
		int meters = 10;
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, millisec, meters, locationListener);
		// Location updates could be fine tuned to use less power. LocationManager.PASSIVE_PROVIDER 
		// could be helpful. Also, Android documentation suggests only checking the location every 
		// 5 minutes in Services that are constantly running.
	}
	
	private void registerTimeReceiver() {
		/*
		 * Register to receive updates when the time changes
		 */
		timeReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				// This is the function that will be called when the time changes
				int[] arr = getTimeOfDay();
				makeToast("Time Receiver: "+arr[0]+":"+arr[1]+":"+arr[2]);
			}
		};
		
		IntentFilter timeIntent = new IntentFilter();
		timeIntent.addAction(Intent.ACTION_TIME_TICK);
		// Note: Intent.ACTION_TIME_TICK receives the notification that the system clock
		// sends out every minute.
		registerReceiver(timeReceiver, timeIntent);
	}
	
	private void registerSMSReceiver() {
		/*
		 * Register to receive updates when an SMS message is received
		 * NOTE: The Appropriate permissions need to be in the manifest
		 */
		smsReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				// This is the method that is called whenever an SMS message is received
				makeToast("SMS Receiver");
				
				// get objects from bundle and save the phone number that sent the sms
				if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
					Bundle bundle = intent.getExtras();
					if(bundle != null) {
						Object[] pdus = (Object[])bundle.get("pdus");
						SmsMessage[] messages = new SmsMessage[pdus.length];
						for(int i=0; i<pdus.length; i++) {
							messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
						}
						for(SmsMessage message: messages) {
							// save the phone number that sent the sms
							lastReceivedSMSAddress = message.getDisplayOriginatingAddress();
						}
					}
				}
			}
		};
		
		IntentFilter smsIntent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, smsIntent);
	}
	
	public void notificationReceived() {
		// If a notification is received, check to see if it corresponds to the
		// conditions outlined in the events.
		
		for(AutoResponseEvent event : events) {
			// Check to see if event conditions are matched. If so, execute the response.
			if(conditionsMet(event)) {
				executeResponse(event);
			}
		}
	}
	
	public boolean conditionsMet(AutoResponseEvent event) {
		// if any of the conditions are false, return false
		boolean result = true;
		if(event.isIfDriving()) {
			// TODO: Implement this for Beta release?
			return false;
		}
		if(event.isIfTime()) {
			// assuming event.getTimeOfDay() is the time of day in minutes
			int[] time = getTimeOfDay();
			int currentTime = (time[HOUR]*60 + time[MINUTE]);
			if(event.getStartMinuteOfDay() < currentTime && currentTime <  event.getEndMinuteOfDay() ) {
				return false;
			}
		}
		if(event.isIfDay()) {
			// assuming event.getDays() returns a list of days where
			// Sunday = 1, Monday = 2, ...
			int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			String days = event.getDays();
			if(days.charAt(dayOfWeek-1) == 0) {
				return false;
			}
		}
		if(event.isIfLocation()) {
			// TODO: change AutoResponseEvent to store latitude and longitude values
			// instead of a string location. Then compare these values to the current
			// latitude and longitude (Stored in global variables here) to see if they
			// are some arbitrary distance from the current location. Maybe .5 miles?
			
			//Riley's comment: If we store locations in a preference file we can refer to them
			// using their name like a primary key
			
			// Note: distance between a and b = sqrt((a.x - b.x)^2 + (a.y - b.y)^2)
		}
		
		return result;
	}
	
	public int[] getTimeOfDay() {
		/*
		 * returns [hour, min, second]
		 */
		String dateTime = DateFormat.getDateTimeInstance().format(new Date());
		// e.g., Nov 5th, 2012 10:44:00 PM 
		String[] split = dateTime.split(" ");
		String[] time = split[3].split(":");
		int hour = Integer.parseInt(time[0]);
		int min = Integer.parseInt(time[1]);
		int sec = Integer.parseInt(time[2]);
		return new int[]{hour, min, sec};
	}
	
	public void executeResponse(AutoResponseEvent event) {
		if(event.isChangePhoneMode()) {
			int mode = event.getPhoneMode();
			switch (mode) {
			case MODE_SILENT:
				setPhoneToSilent();
				break;
			case MODE_VIBRATE:
				setPhoneToVibrate();
				break;
			case MODE_NORMAL:
				setPhoneToNormal();
				break;
			}
		}
		if(event.isDisplayReminder()) {
			setReminder(event.getReminderTime());
		}
		if(event.isSendTextResponse()) {
			// TODO: Need a way to get the phone number to respond to.
			sendTextMessage(event.getTextResponse(), 0);
		}
	}
	
	
	public void setReminder(int time) {
		// TODO: Set a reminder to go off at the specified time.
	}
	
	public void sendTextMessage(String text, int phoneNumber) {
		// Send a text message to a specified phone number.
		Toast.makeText(this, "Send a text message", Toast.LENGTH_SHORT).show();
		// TODO Create a method that sends the text provided to the phone number provided.
	}
	
	public void setPhoneToNormal() {
		// Sets phone to normal ring mode
		makeToast("Setting phone to normal.");
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
	
	public void setPhoneToVibrate() {
		// Sets phone to vibrate mode
		makeToast("Setting phone to silent.");
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}
	
	public void setPhoneToSilent() {
		// Sets pheon to silent mode
		makeToast("Setting phone to silent.");
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}
	
	private void makeToast(String s) {
		/*
		 * A simple method to display a Toast
		 */
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
}
