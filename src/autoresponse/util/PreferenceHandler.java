package autoresponse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class PreferenceHandler {
	
	
	public static final int LATITUDE = 0;
	public static final int LONGITUDE = 1;
	public static final int RADIUS = 2;
	private static final String TAG = "PreferenceHandler";
	
	public static void deleteEvent(Context context, String eventName) {
		Log.d(TAG, "entering deleteEvent");
		SharedPreferences indexFile = context.getSharedPreferences(MyService.EVENT_INDEX_FILE, Context.MODE_PRIVATE);
		String eventFileName = indexFile.getString(eventName, eventName);
		SharedPreferences eventFile = context.getSharedPreferences(eventFileName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor eventEdit = eventFile.edit();
		SharedPreferences.Editor indexEdit =  indexFile.edit();
		
		eventEdit.clear();
		indexEdit.remove(eventName);
		
		indexEdit.commit();
		eventEdit.commit();
	}
	
	public static ArrayList<AutoResponseEvent> getEventList(Context context) {
		Log.d(TAG, "entering getEventList");
		ArrayList<AutoResponseEvent> result = new ArrayList<AutoResponseEvent>();
		SharedPreferences allEventPrefs = context.getSharedPreferences(MyService.EVENT_INDEX_FILE, Context.MODE_PRIVATE);
		Map<String, ?> eventMap = allEventPrefs.getAll();
		for(String key : eventMap.keySet()) {
			String eventFileName = (String)eventMap.get(key);
			// get a file with info for each event
			SharedPreferences eventPrefs = context.getSharedPreferences(eventFileName, Context.MODE_PRIVATE);
			AutoResponseEvent event = new AutoResponseEvent();

			event.setName(eventPrefs.getString("name", "event"));
			
			// context variables
			event.setIfDriving(eventPrefs.getBoolean("ifDriving", false));
			event.setIfTime(eventPrefs.getBoolean("ifTime", false));
			event.setStartMinuteOfDay(eventPrefs.getInt("startMinuteOfDay", -1));
			event.setEndMinuteOfDay(eventPrefs.getInt("endMinuteOfDay", -1));
			event.setIfDay(eventPrefs.getBoolean("ifDay", false));
			event.setDays(eventPrefs.getString("day", "0000000"));
			event.setIfLocation(eventPrefs.getBoolean("ifLocation", false));
			event.setLocation(eventPrefs.getString("location", ""));
			event.setIfRecieveText(eventPrefs.getBoolean("ifReceiveText", false));
			
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
		Log.d(TAG, "entering getLocationList");
		HashMap<String, double[]> result = new HashMap<String, double[]>();
		SharedPreferences allLocationPrefs = context.getSharedPreferences(MyService.LOCATION_INDEX_FILE, Context.MODE_PRIVATE);
		Map<String, ?> locationMap = allLocationPrefs.getAll();
		for(String key : locationMap.keySet()) {
			String locationFileName = (String)locationMap.get(key);
			// get a file with info for each event
			SharedPreferences locationPrefs = context.getSharedPreferences(locationFileName, Context.MODE_PRIVATE);
			double[] data = new double[] {
					locationPrefs.getFloat("latitude", 0),
					locationPrefs.getFloat("longitude", 0),
					locationPrefs.getFloat("radius", 0)
			};
			result.put(locationFileName, data);
		}
		return result;
	}
	
	/**
	 * Writes the event given into a preference file. If an event with this name
	 * already existed it will be overwritten
	 * @param context
	 * @param event
	 */
	public static void writeEvent(Context context, AutoResponseEvent event){
		try {

			Log.d(TAG, "entering writeEvent");

//			// make sure all events have names
			if(event.getName() == null) {
				Log.e(TAG, "Event passed has no name");
				Toast.makeText(context, "Issue saving event details", Toast.LENGTH_SHORT).show();
				return; 
			}
			String eventFileName = event.getName().replace(' ', '_');
			SharedPreferences eventPrefs = context.getSharedPreferences(eventFileName, Context.MODE_PRIVATE);

			SharedPreferences.Editor ed = eventPrefs.edit();

			ed.clear();
			ed.putString("name", event.getName());

			
			Log.d(TAG, "ifReceiveText: "+event.isIfRecieveText());
			// context variables
			ed.putBoolean("ifDriving", event.isIfDriving());
			ed.putBoolean("ifTime", event.isIfTime());
			ed.putInt("startMinuteOfDay", event.getStartMinuteOfDay());
			ed.putInt("endMinuteOfDay", event.getEndMinuteOfDay());
			ed.putBoolean("ifDay", event.isIfDay());
			ed.putString("day", event.getDays());
			ed.putBoolean("ifLocation", event.isIfLocation());
			ed.putString("location", event.getLocation());
			ed.putBoolean("ifReceiveText", event.isIfRecieveText());

			// response variables
			ed.putBoolean("changePhoneMode", event.isChangePhoneMode());
			ed.putInt("phoneMode", event.getPhoneMode());
			ed.putBoolean("displayReminder", event.isDisplayReminder());
			ed.putInt("reminderTime", event.getReminderTime());
			ed.putBoolean("sendTextResponse", event.isSendTextResponse());
			ed.putString("textResponse", event.getTextResponse());

			ed.commit();


			//Add the new file to the index
			SharedPreferences eventIndex = context.getSharedPreferences(MyService.EVENT_INDEX_FILE, Context.MODE_PRIVATE);
			ed = eventIndex.edit();
			ed.putString(eventFileName, eventFileName);
			ed.commit();
		} catch (Exception e) {
			printException(e);
		}
	}
	
	/**
	 * Writes the given location into a preference file. If a location with
	 * this name already existed it will be overwritten.
	 * @param context
	 * @param name
	 * @param lat
	 * @param lng
	 * @param rad
	 */
	public static void writeLocation(Context context, String name, double lat, double lng, double rad){
		Log.d(TAG, "entering writeLocation");
		try {
			String fileName = name.replace(' ', '_');

			SharedPreferences locationPrefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
			SharedPreferences.Editor ed = locationPrefs.edit();
			ed.clear();
			ed.putFloat("latitude", (float) lat);
			ed.putFloat("longitude", (float) lng);
			ed.putFloat("radius", (float) rad);
			ed.commit();

			SharedPreferences locationIndex = context.getSharedPreferences(MyService.LOCATION_INDEX_FILE, Context.MODE_PRIVATE);
			ed = locationIndex.edit();
			ed.putString(fileName, fileName);
			ed.commit();
		} catch (Exception e) {
			printException(e);
		}
	}
	
	private static void printException(Exception e) {
		Log.d(TAG, "Caught exception: "+e.toString()+" at line "+e.getStackTrace()[0].getLineNumber());
	}
	
	
	public static boolean testPrefHandler(Context context){
		boolean pass = true;
		
		AutoResponseEvent testEvent = new AutoResponseEvent();
		testEvent.setChangePhoneMode(true);
		testEvent.setDays("1111111");
		testEvent.setDisplayReminder(true);
		testEvent.setChangePhoneMode(true);
		testEvent.setDays("asdf");
		testEvent.setDisplayReminder(true);
		testEvent.setEndMinuteOfDay(1);
		testEvent.setIfDay(true);
		testEvent.setIfDriving(true);
		testEvent.setIfLocation(true);
		testEvent.setIfRecieveText(true);
		testEvent.setIfTime(true);
		testEvent.setLocation("asdf");
		testEvent.setName("asdf");
		testEvent.setPhoneMode(1);
		testEvent.setReminderTime(1);
		testEvent.setSendTextResponse(true);
		testEvent.setStartMinuteOfDay(1);
		testEvent.setTextResponse("asdf");

		if(!testEvent.equals(testEvent)){
			Log.d(TAG, "AutoResponseEvent.equals is broken");
			return false;
		}
		
		writeEvent(context, testEvent);
		List<AutoResponseEvent> events = getEventList(context);
		AutoResponseEvent out = events.get(0);
		if(out == null){
			Log.d(TAG, "result is null");
		}
		pass &= testEvent.equals(out);
		
		return pass;
	}
	
}
