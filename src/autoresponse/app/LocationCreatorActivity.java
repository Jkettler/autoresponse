package autoresponse.app;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private static LocationListener locationListener1;
	private static LocationListener locationListener2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_creator);

		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
	
	}

	public void registerLocationManager() {
		// TODO figure out why the location manager still seems to be running after onPause/onDestroy is called
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		locationListener1 = new LocationListener() {
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
		locationListener2 = new LocationListener() {
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
		List<String> providers = locationManager.getAllProviders();
		if(providers.contains(LocationManager.GPS_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener1);
		}
		if(providers.contains(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener2);
		}
	}
	
	//Code taken from http://stackoverflow.com/questions/10311834/android-dev-how-to-check-if-location-services-are-enabled
	//and modified to fit the code hree.
	public boolean checkForLocationAwareness(){
		LocationManager lm = null;
		
	    boolean gps_enabled,network_enabled;
        if(lm==null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
	        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		    return gps_enabled || network_enabled;
	        
        }catch(Exception ex){
        	Log.d(TAG, "Exception thrown while checking if location available");
        	return false;
        }
	}
	
	private void displayLocationAlert(){
		final Context context = this;
	    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Location awareness is disabled, go to settings?");
   
        dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface paramDialogInterface, int paramInt) {
               Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               context.startActivity(myIntent);
               //get gps
           }
           });
        dialog.setNegativeButton("Don't use location", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            	//Go back to the condition selector.
            	mEvent.setIfLocation(false);
            	Intent intent = new Intent(context, ConditionSelectorActivity.class);
            	intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
        		startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        dialog.show();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "entering onDestroy");
		super.onDestroy();
		locationManager.removeUpdates(locationListener1);
		locationManager.removeUpdates(locationListener2);
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "entering onPause");
		super.onPause();
		locationManager.removeUpdates(locationListener1);
		locationManager.removeUpdates(locationListener2);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "entering onResume");
		super.onResume();
		
		boolean haveLocation = checkForLocationAwareness();
		Log.d(TAG, "LocationCreator haveLocation = "+haveLocation);
		
		if(haveLocation){
			registerLocationManager();
		} else {
			displayLocationAlert();
		}
	}
	
	public void onAddCurrentLocation(View view) {
		Log.d(TAG, "Add current location button clicked.");

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
				radius = Double.parseDouble(radiusString);
				if(radius <= 0.0) {
					throw new NumberFormatException();
				}
				if(radius > 10.0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), "Invalid radius: "+radiusString+" "+radius, Toast.LENGTH_SHORT).show();
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
			intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
			startActivity(intent);
		} catch (Exception e) {
			Log.d(TAG, "Error in processing data"+e.getStackTrace()[0].getLineNumber());
		}
	}
}
