package autoresponse.app;

import java.util.HashMap;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.PreferenceHandler;

public class LocationSelectorActivity extends Activity {

	private AutoResponseEvent mEvent;
	private ListView mEventListView;

	private String selectedLocationName;
	private boolean locationSelected = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_selector);
		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);

		// pull in location list
		HashMap<String, double[]> locations = PreferenceHandler
				.getLocationList(getApplicationContext());
		String[] items = locations.keySet().toArray(
				new String[locations.keySet().size()]);

		if (items.length > 0) {

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items);

			mEventListView = (ListView) findViewById(R.id.location_list_view);

			mEventListView.setAdapter(adapter);

			mEventListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> myAdapter, View myView,
						int myItemInt, long mylng) {
					selectedLocationName = (String) (mEventListView
							.getItemAtPosition(myItemInt));
					createResponse();
				}
			});
		} else {
			// TODO display textview
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_location_selector, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_location_menu_item:
			addLocation();
			return true;
		}
		return false;
	}

	private void createResponse() {

		// Start response selector and pass along whatever data we got from
		// the condition selector

		// Set the location
		mEvent.setLocation(selectedLocationName);

		// Pass the updated event to the response selector
		Intent intent = new Intent(this, ResponseSelectorActivity.class);
		intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
		startActivity(intent);

	}

	private void addLocation() {
		// Launch location creator
		// Again, pass data from previous activities around
		// This will just be whatever came from the condition selector
		Intent intent = new Intent(this, LocationCreatorActivity.class);
		intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
		startActivity(intent);
	}
}
