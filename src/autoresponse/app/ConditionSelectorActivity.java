package autoresponse.app;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

public class ConditionSelectorActivity extends Activity {

	private final String TAG = "ConditionSelectorActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_condition_selector);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_condition_selector, menu);
		return true;
	}

	public void createCondition(View view) {
		// Pull info from UI
		CheckBox drivingBox = (CheckBox) findViewById(R.id.driving_checkbox);
		CheckBox locationBox = (CheckBox) findViewById(R.id.location_checkbox);
		CheckBox timeBox = (CheckBox) findViewById(R.id.time_checkbox);
		CheckBox dayBox = (CheckBox) findViewById(R.id.day_checkbox);
		CheckBox incomingTextBox = (CheckBox) findViewById(R.id.incoming_text_checkbox);
		TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time_picker);
		TimePicker endTimePicker = (TimePicker) findViewById(R.id.end_time_picker);
		
		// TODO Add days to UI and then pull them in too.

		// Verify that time range given is valid
		if(timeBox.isChecked() && !validTimeRange(startTimePicker, endTimePicker)){
			Toast.makeText(getApplicationContext(),	"Please choose a valid time range.", Toast.LENGTH_SHORT).show();
			return;
		}

		boolean input = drivingBox.isChecked() || locationBox.isChecked() || timeBox.isChecked() || dayBox.isChecked() || incomingTextBox.isChecked();
		if (!input) {
			// If nothing is given as input, stay in activity and put up a toast
			Toast.makeText(getApplicationContext(),	"Please provide a context.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent;
		if (locationBox.isChecked()) {
			//Go to location selector
			Log.d(TAG, "Continue button was clicked with location checked.");
			intent = new Intent(this, LocationSelectorActivity.class);
		} else {
			// Go to response selector
			Log.d(TAG, "Continue button was clicked without location checked.");
			intent = new Intent(this, ResponseSelectorActivity.class);
		}
		// TODO put info from condition into intent as extras, 
		/*
		 * look into parcelable interface, it might be easiest to make an event object
		 *  that implements parcelable and just pass it around
	 	 *	better yet, make an event builder that will do validation when build()
		 *	is called at the end of the UI workflow
		*/
		startActivity(intent);
	}

	private boolean validTimeRange(TimePicker startTimePicker,
			TimePicker endTimePicker) {
		
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, startTimePicker.getCurrentHour());
		start.set(Calendar.MINUTE, startTimePicker.getCurrentMinute());
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.HOUR_OF_DAY, endTimePicker.getCurrentHour());
		end.set(Calendar.MINUTE, endTimePicker.getCurrentMinute());
		
		return start.before(end);
	}

}
