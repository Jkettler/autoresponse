package autoresponse.app;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.PreferenceHandler;

public class LocationCreatorActivity extends Activity {

	private static final String TAG = "LocationCreatorActivity";

	AutoResponseEvent mEvent;

	private static double latitude = -1;
	private static double longitude = -1;
	private static LocationManager locationManager;
	private static LocationListener locationListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_creator);

		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
	
		registerLocationManager();
	}
	
	public void registerLocationManager() {
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// This is the method that will be called when the location has changed
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
			// Other methods are note used, but must be implemented.
			public void onProviderDisabled(String provider) {}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status,Bundle extras) {}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "entering onDestroy");
		super.onDestroy();
		locationManager.removeUpdates(locationListener);
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "entering onPause");
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "entering onResume");
		super.onResume();
		registerLocationManager();
	}
	
	public void onAddCurrentLocation(View view) {
		Log.d(TAG, "Add current location button clicked.");
		// TODO 

		try {
			
			EditText locationName = (EditText) findViewById(R.id.location_name_edit_text);
			EditText locationRadius = (EditText) findViewById(R.id.location_radius_edit_text);

			String name = locationName.getText().toString();
			if(name == "") {
				Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
				return;
			}

			double radius = 0;
			String radiusString = locationRadius.getText().toString();
			try {
				Double.parseDouble(radiusString);
				/*
				if(radius <= 0) {
					throw new NumberFormatException();
				}
				if(radius > 10) {
					throw new NumberFormatException();
				}
				*/
			} catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), "Invalid radius: "+radiusString, Toast.LENGTH_SHORT).show();
				return;
			}

			if(latitude == -1 || longitude == -1) {
				Toast.makeText(getApplicationContext(), "Waiting for location to load..."+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
				return;
			}
			
			PreferenceHandler.writeLocation(getApplicationContext(), name, latitude, longitude, radius);

			//Get current location
			//Get name and radius from the UI
			//Verify that the values given are valid
			//Is the name in use?
			//Is the radius greater than zero and not too large (presumably there's going to be some reasonable limit)
			//If not, throw up a toast and return
			//Add new location to the storage
			//Either launch the location selector or finish this activity, not sure which works better
			Intent intent = new Intent(this, LocationSelectorActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.d(TAG, "Error in processing data"+e.getStackTrace()[0].getLineNumber());
		}
	}
}
