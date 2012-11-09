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
	private CheckBox mMonBox;
	private CheckBox mTueBox;
	private CheckBox mWedBox;
	private CheckBox mThurBox;
	private CheckBox mFriBox;
	private CheckBox mSatBox;
	private CheckBox mSunBox;
	private CheckBox mIncomingTextBox;
	private TimePicker mStartTimePicker;
	private TimePicker mEndTimePicker;
	
	private AutoResponseEvent mEvent; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_condition_selector);
		Log.d(TAG, "onCreate called");

		
		mDrivingBox = (CheckBox) findViewById(R.id.driving_checkbox);
		mLocationBox = (CheckBox) findViewById(R.id.location_checkbox);
		mTimeBox = (CheckBox) findViewById(R.id.time_checkbox);
		mDayBox = (CheckBox) findViewById(R.id.day_checkbox);
		mMonBox = (CheckBox)findViewById(R.id.monday_checkbox);
		mTueBox = (CheckBox)findViewById(R.id.tuesday_checkbox);
		mWedBox = (CheckBox)findViewById(R.id.wednesday_checkbox);
		mThurBox = (CheckBox)findViewById(R.id.thursday_checkbox);
		mFriBox = (CheckBox)findViewById(R.id.friday_checkbox);
		mSatBox = (CheckBox)findViewById(R.id.saturday_checkbox);
		mSunBox = (CheckBox)findViewById(R.id.sunday_checkbox);
		mIncomingTextBox = (CheckBox) findViewById(R.id.incoming_text_checkbox);
		mStartTimePicker = (TimePicker) findViewById(R.id.start_time_picker);
		mEndTimePicker = (TimePicker) findViewById(R.id.end_time_picker);
		
		//Fill in UI with details from event passed via intent
		mEvent = getIntent().getParcelableExtra(AutoResponseEvent.EVENT_KEY);
		if(mEvent != null){
			mDrivingBox.setChecked(mEvent.isIfDriving());
			mLocationBox.setChecked(mEvent.isIfLocation());
			mTimeBox.setChecked(mEvent.isIfTime());
			mDayBox.setChecked(mEvent.isIfDay());
			mSunBox.setChecked(mEvent.getDays().charAt(0) == '1');
			mMonBox.setChecked(mEvent.getDays().charAt(1) == '1'); 
			mTueBox.setChecked(mEvent.getDays().charAt(2) == '1'); 
			mWedBox.setChecked(mEvent.getDays().charAt(3) == '1');
			mThurBox.setChecked(mEvent.getDays().charAt(4) == '1');
			mFriBox.setChecked(mEvent.getDays().charAt(5) == '1');
			mSatBox.setChecked(mEvent.getDays().charAt(6) == '1');
			mIncomingTextBox.setChecked(mEvent.isIfRecieveText());
			mStartTimePicker.setCurrentHour(mEvent.getStartMinuteOfDay()/60);
			mStartTimePicker.setCurrentMinute(mEvent.getStartMinuteOfDay()%60);
			mEndTimePicker.setCurrentHour(mEvent.getEndMinuteOfDay()/60);
			mEndTimePicker.setCurrentMinute(mEvent.getEndMinuteOfDay()%60);
		}
	}
	
	public char getCharValue(boolean value) {
		if(value == true) {
			return '1';
		}
		return '0';
	}
	
	public void createCondition(View view) {

		//TODO: (beta) disable the time pickers unless the corresponding checkbox is checked 
		
		// TODO Add days to UI and then pull them in too.
		if(mEvent == null){
			mEvent = new AutoResponseEvent();
			mEvent.setName(getIntent().getStringExtra(HomeScreenActivity.EVENT_NAME));
		}
		mEvent.setIfDay(mDayBox.isChecked());
		mEvent.setIfDriving(mDrivingBox.isChecked());
		mEvent.setIfLocation(mLocationBox.isChecked());
		mEvent.setIfTime(mTimeBox.isChecked());
		mEvent.setIfRecieveText(mIncomingTextBox.isChecked());
		
		char sun = getCharValue(mSunBox.isChecked());
		char mon = getCharValue(mMonBox.isChecked()); 
		char tue = getCharValue(mTueBox.isChecked());
		char wed = getCharValue(mWedBox.isChecked());
		char thur = getCharValue(mThurBox.isChecked());
		char fri = getCharValue(mFriBox.isChecked());
		char sat = getCharValue(mSatBox.isChecked());
		
		mEvent.setDays(sun+""+mon+""+tue+""+wed+""+thur+""+fri+""+sat);
		
		
		if(mEvent.getName() == null){
			Log.w(TAG, "Event name taken from intent was null.");
		}
		
		
		if(mEvent.isIfTime()){
			mEvent.setStartMinuteOfDay(getMinutes(mStartTimePicker));
			mEvent.setEndMinuteOfDay(getMinutes(mEndTimePicker));
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
		intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
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
