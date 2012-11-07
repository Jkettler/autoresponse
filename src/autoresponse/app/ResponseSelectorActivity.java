package autoresponse.app;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.PreferenceHandler;

public class ResponseSelectorActivity extends Activity {

	private static final String TAG = "ResponseSelectorActivity";
	AutoResponseEvent mEvent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response_selector);
		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
	}

	public void createEvent(View view) {
		// TODO Pull in data from UI, add it to the AutoResponseEvent object
		CheckBox phoneModeCheckbox = (CheckBox) findViewById(R.id.phone_mode_checkbox);
		CheckBox reminderCheckbox = (CheckBox) findViewById(R.id.reminder_checkbox);
		CheckBox textResponseCheckbox = (CheckBox) findViewById(R.id.text_response_checkbox);
		//TODO Need text response input
		TimePicker reminderTimePicker = (TimePicker) findViewById(R.id.reminder_time_picker);

		mEvent.setChangePhoneMode(phoneModeCheckbox.isChecked());
		mEvent.setDisplayReminder(reminderCheckbox.isChecked());
		mEvent.setSendTextResponse(textResponseCheckbox.isChecked());
		
		if(mEvent.isDisplayReminder()){
			mEvent.setReminderTime(reminderTimePicker.getCurrentHour()*60+reminderTimePicker.getCurrentMinute());
		}
			
		
		RadioGroup phoneModeRadioGroup = (RadioGroup) findViewById(R.id.phone_mode_radio);
		int id = phoneModeRadioGroup.getCheckedRadioButtonId();

		if (id != -1 && phoneModeCheckbox.isChecked()) {
			RadioButton phoneModeButton = (RadioButton) findViewById(id);

			String type = phoneModeButton.getText().toString();
			if (type == null) {
				Log.d(TAG, "Problem determining ringer mode.");
			} else if (type.equals(getString(R.string.normal))) {
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_NORMAL);
			} else if (type.equals(getString(R.string.silent))){
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_SILENT);
			} else if (type.equals(getString(R.string.vibrate))){
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_VIBRATE);
			}

		}

		// put the event in long term storage
		PreferenceHandler.writeEvent(this, mEvent);
		// TODO restart the service

		// Go back to the home screen but pass a flag so that you can't hit back
		// and wind up here.
		Intent intent = new Intent(this, HomeScreenActivity.class);
		startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		;
	}
}
