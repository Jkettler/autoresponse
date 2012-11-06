package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import autoresponse.util.AutoResponseEvent;

public class LocationCreatorActivity extends Activity {

	private static final String TAG = "LocationCreatorActivity";

	AutoResponseEvent mEvent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_creator);
		
		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
	}

	public void onAddCurrentLocation(View view) {
		Log.d(TAG, "Add current location button clicked.");
		// TODO 
		//Get current location
		//Get name and radius from the UI
			//Verify that the values given are valid
				//Is the name in use?
				//Is the radius greater than zero and not too large (presumably there's going to be some reasonable limit)
			//If not, throw up a toast and return
		//Add new location to the storage
		//Either launch the location selector or finish this activity, not sure which works better
		Intent intent = new Intent(this, ResponseSelectorActivity.class);
		startActivity(intent);
	}
}
