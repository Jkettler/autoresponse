package autoresponse.app;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.MyService;
import autoresponse.util.PreferenceHandler;

public class ResponseSelectorActivity extends Activity {

	private static final String TAG = "ResponseSelectorActivity";
	AutoResponseEvent mEvent;

	private CheckBox mPhoneModeCheckbox;
	private CheckBox mReminderCheckbox;
	private CheckBox mTextResponseCheckbox;

	private TimePicker reminderTimePicker;
	private EditText mTextResponseEditText;
	private RadioGroup mPhoneModeRadioGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response_selector);
		Intent intent = getIntent();
		mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
		
		mPhoneModeCheckbox = (CheckBox) findViewById(R.id.phone_mode_checkbox);
		mReminderCheckbox = (CheckBox) findViewById(R.id.reminder_checkbox);
		mTextResponseCheckbox = (CheckBox) findViewById(R.id.text_response_checkbox);

		reminderTimePicker = (TimePicker) findViewById(R.id.reminder_time_picker);
		mTextResponseEditText = (EditText) findViewById(R.id.text_response_edit);
		mPhoneModeRadioGroup = (RadioGroup) findViewById(R.id.phone_mode_radio);

		Log.d(TAG, "Response Selector started for event with name: " + mEvent.getName());
		
		mPhoneModeCheckbox.setChecked(mEvent.isChangePhoneMode());
		mReminderCheckbox.setChecked(mEvent.isDisplayReminder());
		mTextResponseCheckbox.setChecked(mEvent.isSendTextResponse());

		reminderTimePicker.setCurrentHour(mEvent.getReminderTime() / 60);
		reminderTimePicker.setCurrentMinute(mEvent.getReminderTime() % 60);
		mTextResponseEditText.setText(mEvent.getTextResponse());

		if (mEvent.isChangePhoneMode()) {
			int id = -1;
			switch (mEvent.getPhoneMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				id = R.id.normal_radio;
				break;
			case AudioManager.RINGER_MODE_SILENT:
				id = R.id.silent_radio;
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				id = R.id.vibrate_radio;
				break;
			}
			
			if(id != -1){
				RadioButton button = (RadioButton) findViewById(id);
				button.setChecked(true);
			}
		}
	}

	public void createEvent(View view) {

		Log.d(TAG, "Ok button clicked, creating event.");

		// TODO Pull in data from UI, add it to the AutoResponseEvent object

		mEvent.setChangePhoneMode(mPhoneModeCheckbox.isChecked());
		mEvent.setDisplayReminder(mReminderCheckbox.isChecked());
		mEvent.setSendTextResponse(mTextResponseCheckbox.isChecked());

		if (mEvent.isDisplayReminder()) {
			mEvent.setReminderTime(reminderTimePicker.getCurrentHour() * 60
					+ reminderTimePicker.getCurrentMinute());
		}

		if (mEvent.isSendTextResponse()) {

			String textResponse = mTextResponseEditText.getText().toString();
			Log.d(TAG, "Creating event with text response: " + textResponse);
			mEvent.setTextResponse(textResponse);
		}

		int id = mPhoneModeRadioGroup.getCheckedRadioButtonId();

		if (id != -1 && mPhoneModeCheckbox.isChecked()) {
			RadioButton phoneModeButton = (RadioButton) findViewById(id);

			String type = phoneModeButton.getText().toString();
			if (type == null) {
				Log.d(TAG, "Problem determining ringer mode.");
			} else if (type.equals(getString(R.string.normal))) {
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_NORMAL);
			} else if (type.equals(getString(R.string.silent))) {
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_SILENT);
			} else if (type.equals(getString(R.string.vibrate))) {
				mEvent.setPhoneMode(AudioManager.RINGER_MODE_VIBRATE);
			}

		}

		// put the event in long term storage

		if (mEvent.getName() == null) {
			Log.w(TAG, "Event passed via intent has no name.");
		} else {
			Log.d(TAG, "Storing event with name: " + mEvent.getName());
		}

		PreferenceHandler.writeEvent(this, mEvent);

		// restart service
		Intent svc = new Intent(this, MyService.class);
		stopService(svc);
		startService(svc);

		// Go back to the home screen but pass a flag so that you can't hit back
		// and wind up here.
		Intent intent = new Intent(this, EventListActivity.class);
		startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}
}
