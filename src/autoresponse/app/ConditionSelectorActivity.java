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
import autoresponse.util.AutoResponseEvent;

public class ConditionSelectorActivity extends Activity {

	private final String TAG = "ConditionSelectorActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_condition_selector);
	}
	
	public void createCondition(View view) {

		//TODO: (beta) disable the time pickers unless the corresponding checkbox is checked 
		
		// Pull info from UI
		CheckBox drivingBox = (CheckBox) findViewById(R.id.driving_checkbox);
		CheckBox locationBox = (CheckBox) findViewById(R.id.location_checkbox);
		CheckBox timeBox = (CheckBox) findViewById(R.id.time_checkbox);
		CheckBox dayBox = (CheckBox) findViewById(R.id.day_checkbox);
		CheckBox incomingTextBox = (CheckBox) findViewById(R.id.incoming_text_checkbox);
		TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time_picker);
		TimePicker endTimePicker = (TimePicker) findViewById(R.id.end_time_picker);
		
		// TODO Add days to UI and then pull them in too.
		
		AutoResponseEvent event = new AutoResponseEvent();
		event.setIfDay(dayBox.isChecked());
		event.setIfDriving(drivingBox.isChecked());
		event.setIfLocation(locationBox.isChecked());
		event.setIfTime(timeBox.isChecked());
		event.setIfRecieveText(incomingTextBox.isChecked());
		
		
		if(event.isIfTime()){
			event.setStartMinuteOfDay(getMinutes(startTimePicker));
			event.setEndMinuteOfDay(getMinutes(endTimePicker));
		}

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
		intent.putExtra(AutoResponseEvent.EVENT_KEY, event);
		startActivity(intent);
	}

	private int getMinutes(TimePicker startTimePicker) {
		return startTimePicker.getCurrentHour()*60+startTimePicker.getCurrentMinute();
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
