package autoresponse.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import autoresponse.app.R;

public class MyService extends Service {
	
	// Global variables
	public static final String LOCATION_INDEX_FILE = "location_index_prefs";
	public static final String EVENT_INDEX_FILE = "event_index_prefs";
	
	private static final String TAG = "MyService";
	private BroadcastReceiver timeReceiver = null;
	private BroadcastReceiver smsReceiver = null;
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	
	// ints used to index array for time of day
	private final int HOUR = 0;
	private final int MINUTE = 1;
	private final int SECOND = 2;
	
	private final int MODE_SILENT = AudioManager.RINGER_MODE_SILENT;
	private final int MODE_VIBRATE = AudioManager.RINGER_MODE_VIBRATE;
	private final int MODE_NORMAL = AudioManager.RINGER_MODE_NORMAL;
	
	// an array of events that are being monitored 
	private ArrayList<AutoResponseEvent> events;
	private HashMap<String, double[]> locations;
	private static ArrayList<Object[]> pendingReminders;
	private static ArrayList<int[]> pendingRingerModeChanges;
	
	private static boolean respondToSMS;
	private static String smsText;
	
	private static int reminderID;
	
	// last known latitude and longitude
	private static double latitude;
	private static double longitude;
	
	// last phone number that sent an SMS to this device
	private static String lastReceivedSMSAddress;
	
	private static int previousRingerMode;
	// End of Global variables
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Service: onCreate");
		
		events = PreferenceHandler.getEventList(getApplicationContext());
		locations = PreferenceHandler.getLocationList(getApplicationContext());
		pendingReminders = new ArrayList<Object[]>();
		pendingRingerModeChanges = new ArrayList<int[]>();
		reminderID = 0;
		
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		previousRingerMode = am.getRingerMode();
		
		respondToSMS = false;
		smsText = "";
		
		boolean timeReceiverNeeded = false;
		boolean smsReceiverNeeded = false;
		boolean locationManagerNeeded = false;
		for(AutoResponseEvent event : events) {
			if(event.isIfDay() || event.isIfTime()) {
				timeReceiverNeeded = true;
			}
			if(event.isIfLocation()) {
				locationManagerNeeded = true;
			}
			if(event.isIfRecieveText()) {
				smsReceiverNeeded = true;
				if(!event.isIfDay() || !event.isIfTime()) {
					// no time is specified. Always respond to sms messages
					respondToSMS = true;
				}
			}
		}

		
		if(timeReceiverNeeded) {
			Log.d(TAG, "Registering time receiver");
			registerTimeReceiver();
		}
		if(smsReceiverNeeded) {
			Log.d(TAG, "Registering SMS receiver");
			registerSMSReceiver();
		}	
		if(locationManagerNeeded) {
			Log.d(TAG, "Registering LocationManager");
			registerLocationManager();
		}

		triggerReminder("TESTING!!!");
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
				notificationReceived();
			}
			// Other methods are note used, but must be implemented.
			public void onProviderDisabled(String provider) {}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status,Bundle extras) {}
		};
		
		int millisec = 10*1000; // 10 seconds
		int meters = 30;
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
				notificationReceived();
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
					
					if(respondToSMS) {
						Log.d(TAG, "sending text message");
						sendTextMessage(smsText, lastReceivedSMSAddress);
					}
				}
				notificationReceived();
			}
		};
		
		IntentFilter smsIntent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, smsIntent);
	}
	
	public void notificationReceived() {
		// If a notification is received, check to see if it corresponds to the
		// conditions outlined in the events.
		Log.d(TAG, "entering notificationReceived");
		
		for(AutoResponseEvent event : events) {
			// Check to see if event conditions are matched. If so, execute the response.
			if(conditionsMet(event)) {
				executeResponse(event);
			}
		}
		int currentTimeInMinutes = getTimeOfDay()[MINUTE]+getTimeOfDay()[HOUR]*60;
		
		for(int i=0; i < pendingReminders.size(); i++) {
			Object[] message = pendingReminders.get(i);
			if(currentTimeInMinutes == (Integer)message[0]) {
				triggerReminder((String)message[1]);
				pendingReminders.remove(i);
				i--;
			}
		}
		for(int i=0; i <  pendingRingerModeChanges.size(); i++) {
			int[] change = pendingRingerModeChanges.get(i);
			if(currentTimeInMinutes == change[0]) {
				changeRingerMode(change[1]);
				pendingRingerModeChanges.remove(i);
				i--;
			}
		}
	}
	
	public boolean conditionsMet(AutoResponseEvent event) {
		// if any of the conditions are false, return false
		Log.d(TAG, "entering conditionsMet");

		boolean ifDriving = false;
		boolean ifTime = false;
		boolean ifDay = false;
		boolean ifLocation = false;
		boolean ifDisplayReminder = false;
		boolean ifReceiveText = false;
		
		
		if(event.isIfTime()) {
			// assuming event.getTimeOfDay() is the time of day in minutes
			int[] time = getTimeOfDay();
			int currentTime = (time[HOUR]*60 + time[MINUTE]);
			if(event.getStartMinuteOfDay() <= currentTime && event.getEndMinuteOfDay() >= currentTime) {
				ifTime = true;
			}
		}
		if(event.isIfDay()) {
			// assuming event.getDays() returns a list of days where
			// Sunday = 0, Monday = 1, Tuesday = 2, ...
			int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			String days = event.getDays();
			if(days.charAt(dayOfWeek-1) == '1') {
				ifDay = true;
			}
		}
		if(event.isIfDriving()) {
			// TODO: Implement this for Beta release?
			ifDriving = true;
		}
		if(event.isIfLocation()) {
			// Note: distance between a and b = sqrt((a.x - b.x)^2 + (a.y - b.y)^2)
			String locationName = event.getLocation();
			double[] location = locations.get(locationName);
			double savedLat = location[PreferenceHandler.LATITUDE];
			double savedLong = location[PreferenceHandler.LONGITUDE];
			double savedRadius = location[PreferenceHandler.RADIUS];
			
			//double distance = Math.sqrt(Math.pow(savedLat-latitude, 2)+ Math.pow(savedLong-longitude, 2));
			float[] result = new float[1]; 
			Location.distanceBetween(latitude, longitude, savedLat, savedLong, result);
			float distance = result[0];
			
			savedRadius *= 1609.34;
			if(savedRadius > distance) {
				//makeToast("Location within target radius. ("+distance+")");
				ifLocation = true;
			} else {
				//makeToast("Location outside target radius. ("+distance+")");
				ifLocation = false;
			}
		}
		if(event.isDisplayReminder()) {
			ifDisplayReminder = true;
		}
		if(event.isIfRecieveText()) {
			ifReceiveText = true;
			smsText = event.getTextResponse();
		}
		
		
		return 	(event.isIfDriving() == ifDriving) &&
				(event.isIfTime() == ifTime) &&
				(event.isIfDay() == ifDay) &&
				(event.isIfLocation() == ifLocation) &&
				(event.isDisplayReminder() == ifDisplayReminder) &&
				(event.isIfRecieveText() == ifReceiveText);
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
		if(split[4].equals("PM")) {
			hour += 12;
		}
		return new int[]{hour, min, sec};
	}
	
	public void executeResponse(AutoResponseEvent event) {
		Log.d(TAG, "entering executeResponse");
		
		
		// for some responses, actions will only be allowed if it is the start time.
		// for others, it only requires being between the start and finish time.
		int[] time = getTimeOfDay();
		int currentTime = (time[HOUR]*60 + time[MINUTE]);
//		boolean isInTimeWindow = (event.getStartMinuteOfDay() <= currentTime && 
//								event.getEndMinuteOfDay() >= currentTime);
		boolean isStartTime = ((event.getStartMinuteOfDay() == currentTime) && event.isIfTime()) ||
								!event.isIfTime();
		boolean isEndTime = ((event.getEndMinuteOfDay() == currentTime) && event.isIfTime()) ||
								!event.isIfTime();
			
		if(event.isChangePhoneMode() && isStartTime) {
			Log.d(TAG, "changing phone mode");
			changeRingerMode(event.getPhoneMode());
			pendingRingerModeChanges.add(new int[]{event.getEndMinuteOfDay(), previousRingerMode});
		}
		if(event.isDisplayReminder() && isStartTime) {
			Log.d(TAG, "setting a reminder");
			setReminder(event.getReminderTime());
		}
		if(event.isSendTextResponse() && isStartTime) {
			Log.d(TAG, "setting respondToSMS = true");
			smsText = event.getTextResponse();
			respondToSMS = true;
		}
		if(event.isSendTextResponse() && isEndTime) {
			Log.d(TAG, "setting respondToSMS = false");
			respondToSMS = false;
		}
			
	}
	
	
	public void setReminder(int time) {
		// Note: time is an int in minutes since midnight. e.g. 1:01AM = 61
		// TODO Beta: make the reminder text customizable.
		pendingReminders.add(new Object[]{time, "Hey, I'm reminding you to do something!"});
	}
	
	@SuppressWarnings("deprecation")
	public void triggerReminder(String message) {
		NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification note=new Notification(R.drawable.ic_launcher,"Status message!",System.currentTimeMillis());
		PendingIntent i=PendingIntent.getActivity(this, 0, new Intent(this, NotificationMessage.class),0);
		note.setLatestEventInfo(this, "Auto Response", message, i);
		note.vibrate=new long[] {500L, 200L, 200L, 500L}; 
		note.flags|=Notification.FLAG_AUTO_CANCEL;
		
		mgr.notify(reminderID, note);
		
		reminderID++;
	}
	
	public void sendTextMessage(String text, String phoneNumber) {
		// Send a text message to a specified phone number.
		Toast.makeText(this, "Send a text message", Toast.LENGTH_SHORT).show();
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, text, null, null);
	}
	
	public void changeRingerMode(int ringerMode) {
		// Sets phone to normal ring mode
		Log.d(TAG, "entering changeRingerMode");
		assert ringerMode == MODE_SILENT ||ringerMode == MODE_VIBRATE || ringerMode == MODE_NORMAL;
		
		if(ringerMode == MODE_SILENT) { Log.d(TAG, "entering changeRingerMode silent");} 
		if(ringerMode == MODE_VIBRATE) { Log.d(TAG, "entering changeRingerMode vibrate");} 
		if(ringerMode == MODE_NORMAL) {Log.d(TAG, "entering changeRingerMode normal");}
		
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		previousRingerMode = am.getRingerMode();
		am.setRingerMode(ringerMode);
	}
	
	private void makeToast(String s) {
		/*
		 * A simple method to display a Toast
		 */
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
}
