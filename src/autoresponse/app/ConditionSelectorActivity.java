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
	
	private CheckBox mDrivingBox;
	private CheckBox mLocationBox;
	private CheckBox mTimeBox;
	private CheckBox mDayBox;
	private CheckBox mIncomingTextBox;
	private TimePicker mStartTimePicker;
	private TimePicker mEndTimePicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_condition_selector);
		Log.d(TAG, "onCreate called");

		mDrivingBox = (CheckBox) findViewById(R.id.driving_checkbox);
		mLocationBox = (CheckBox) findViewById(R.id.location_checkbox);
		mTimeBox = (CheckBox) findViewById(R.id.time_checkbox);
		mDayBox = (CheckBox) findViewById(R.id.day_checkbox);
		mIncomingTextBox = (CheckBox) findViewById(R.id.incoming_text_checkbox);
		mStartTimePicker = (TimePicker) findViewById(R.id.start_time_picker);
		mEndTimePicker = (TimePicker) findViewById(R.id.end_time_picker);
		
		//Fill in UI with details from event passed via intent
		AutoResponseEvent event = getIntent().getParcelableExtra(AutoResponseEvent.EVENT_KEY);
		if(event != null){
			//TODO days still...
			mDrivingBox.setChecked(event.isIfDriving());
			mLocationBox.setChecked(event.isIfLocation());
			mTimeBox.setChecked(event.isIfTime());
			mDayBox.setChecked(event.isIfDay());
			mIncomingTextBox.setChecked(event.isIfRecieveText());
			mStartTimePicker.setCurrentHour(event.getStartMinuteOfDay()/60);
			mStartTimePicker.setCurrentMinute(event.getStartMinuteOfDay()%60);
			mEndTimePicker.setCurrentHour(event.getEndMinuteOfDay()/60);
			mEndTimePicker.setCurrentMinute(event.getEndMinuteOfDay()%60);
		}
	}
	
	public void createCondition(View view) {

		//TODO: (beta) disable the time pickers unless the corresponding checkbox is checked 
		
		// TODO Add days to UI and then pull them in too.
		
		AutoResponseEvent event = new AutoResponseEvent();
		event.setIfDay(mDayBox.isChecked());
		event.setIfDriving(mDrivingBox.isChecked());
		event.setIfLocation(mLocationBox.isChecked());
		event.setIfTime(mTimeBox.isChecked());
		event.setIfRecieveText(mIncomingTextBox.isChecked());
		
		event.setName(getIntent().getStringExtra(HomeScreenActivity.EVENT_NAME));
		
		if(event.getName() == null){
			Log.w(TAG, "Event name taken from intent was null.");
		}
		
		
		if(event.isIfTime()){
			event.setStartMinuteOfDay(getMinutes(mStartTimePicker));
			event.setEndMinuteOfDay(getMinutes(mEndTimePicker));
		}

		// Verify that time range given is valid
		if(mTimeBox.isChecked() && !validTimeRange(mStartTimePicker, mEndTimePicker)){
			Toast.makeText(getApplicationContext(),	"Please choose a valid time range.", Toast.LENGTH_SHORT).show();
			return;
		}

		boolean input = mDrivingBox.isChecked() || mLocationBox.isChecked() || mTimeBox.isChecked() || mDayBox.isChecked() || mIncomingTextBox.isChecked();
		if (!input) {
			// If nothing is given as input, stay in activity and put up a toast
			Toast.makeText(getApplicationContext(),	"Please provide a context.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent;
		if (mLocationBox.isChecked()) {
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
