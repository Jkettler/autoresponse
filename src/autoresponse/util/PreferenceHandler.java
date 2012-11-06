package autoresponse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHandler {
	
	public static final int LATITUDE = 0;
	public static final int LONGITUDE = 1;
	public static final int RADIUS = 2;
	
	public static ArrayList<AutoResponseEvent> getEventList(Context context) {
		ArrayList<AutoResponseEvent> result = new ArrayList<AutoResponseEvent>();
		SharedPreferences allEventPrefs = context.getSharedPreferences(MyService.EVENT_INDEX_FILE, MyService.MODE_PRIVATE);
		Map<String, ?> eventMap = allEventPrefs.getAll();
		for(String key : eventMap.keySet()) {
			String eventFileName = (String)eventMap.get(key);
			// get a file with info for each event
			SharedPreferences eventPrefs = context.getSharedPreferences(eventFileName, context.MODE_PRIVATE);
			AutoResponseEvent event = new AutoResponseEvent();

			// context variables
			event.setIfDriving(eventPrefs.getBoolean("ifDriving", false));
			event.setIfTime(eventPrefs.getBoolean("ifTime", false));
			event.setStartMinuteOfDay(eventPrefs.getInt("startMinuteOfDay", -1));
			event.setEndMinuteOfDay(eventPrefs.getInt("endMinuteOfDay", -1));
			event.setIfDay(eventPrefs.getBoolean("ifDay", false));
			event.setDays(eventPrefs.getString("day", "0000000"));
			event.setIfLocation(eventPrefs.getBoolean("ifLocation", false));
			event.setLocation(eventPrefs.getString("location", ""));
			
			// response variables
			event.setChangePhoneMode(eventPrefs.getBoolean("changePhoneMode", false));
			event.setPhoneMode(eventPrefs.getInt("phoneMode", -1));
			event.setDisplayReminder(eventPrefs.getBoolean("displayReminder", false));
			event.setReminderTime(eventPrefs.getInt("reminderTime", -1));
			event.setSendTextResponse(eventPrefs.getBoolean("sendTextResponse", false));
			event.setTextResponse(eventPrefs.getString("textResponse", ""));
			result.add(event);
		}
		return result;
	}
	
	public static HashMap<String, double[]> getLocationList(Context context) {
		HashMap<String, double[]> result = new HashMap<String, double[]>();
		SharedPreferences allLocationPrefs = context.getSharedPreferences(MyService.LOCATION_INDEX_FILE, MyService.MODE_PRIVATE);
		Map<String, ?> locationMap = allLocationPrefs.getAll();
		for(String key : locationMap.keySet()) {
			String locationFileName = (String)locationMap.get(key);
			// get a file with info for each event
			SharedPreferences locationPrefs = context.getSharedPreferences(locationFileName, context.MODE_PRIVATE);
			double[] data = new double[] {
					locationPrefs.getFloat("latitude", 0),
					locationPrefs.getFloat("longitude", 0),
					locationPrefs.getFloat("radius", 0)
			};
			result.put(locationFileName, data);
		}
		return result;
	}
	
}
