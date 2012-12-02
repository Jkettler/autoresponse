package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.MyService;
import autoresponse.util.PreferenceHandler;

public class EventDisplayActivity extends Activity {

	private static final String TAG = "EventDisplayActivity";

	private static final int DIALOG_DELETE_ID = 0;

	private AutoResponseEvent event;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_display);

		event = (AutoResponseEvent) getIntent().getParcelableExtra(
				AutoResponseEvent.EVENT_KEY);
		Log.d(TAG, "Displaying event: "+event.getName());
		
		//Set the activity title to the name of the event.
		setTitle("Event: "+event.getName());
		
		TextView[] textViews = {
				(TextView) findViewById(R.id.event_viewer_time_textView),
				(TextView) findViewById(R.id.event_viewer_day_textView),
				(TextView) findViewById(R.id.event_viewer_location_textView),
				(TextView) findViewById(R.id.event_viewer_recieveText_textView),

				(TextView) findViewById(R.id.event_viewer_phoneMode_textView),
				(TextView) findViewById(R.id.event_viewer_reminder_textView),
				(TextView) findViewById(R.id.event_viewer_textResponse_textView) };

		boolean[] displayTextView = { event.isIfTime(), event.isIfDay(),
				event.isIfLocation(), event.isIfRecieveText(),
				event.isChangePhoneMode(), event.isDisplayReminder(),
				event.isSendTextResponse() };

		String[] details = { event.getTimeString(), event.getDayString(),
				event.getLocationString(), event.getRecieveTextString(),
				event.getChangePhoneModeString(),
				event.getDisplayReminderString(),
				event.getSendTextResponseString() };

		if (!(textViews.length == displayTextView.length && displayTextView.length == details.length)) {
			Toast.makeText(this, "Problem displaying event", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Iterate through the textViews and set their visiblity/text
		TextView textView;
		boolean display;
		String detail;
		String label;
		for (int i = 0; i < textViews.length; i++) {
			textView = textViews[i];
			display = displayTextView[i];
			detail = details[i];

			textView.setVisibility(display ? View.VISIBLE : View.GONE);
			label = textView.getText().toString();
			label += ' ' + detail;
			textView.setText(label);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_event_display, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// TODO
		case R.id.edit_event_context_menu_item:
			editConditions();
			return true;
		case R.id.edit_event_response_menu_item:
			editResponse();
			return true;
		case R.id.delete_event_menu_item:
			showDialog(DIALOG_DELETE_ID);
			return true;
		}
		return false;
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_DELETE_ID) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage(R.string.delete_question)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									deleteEvent();
								}
							}).setNegativeButton(R.string.no, null);
			dialog = builder.create();
		}
		return dialog;
	}

	private void editConditions() {
		Log.d(TAG, "entering closeActivity");
		// Toast.makeText(this, selectedFromList, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ConditionSelectorActivity.class);
		intent.putExtra(AutoResponseEvent.EVENT_KEY, event);
		startActivity(intent);
	}

	private void editResponse() {
		Log.d(TAG, "entering editResponse");
		// Toast.makeText(this, selectedFromList, Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, ResponseSelectorActivity.class);
		intent.putExtra(AutoResponseEvent.EVENT_KEY, event);
		startActivity(intent);
	}

	private void deleteEvent() {
		Log.d(TAG, "entering deleteEvent");
		// Toast.makeText(this, selectedFromList, Toast.LENGTH_SHORT).show();

		// delete the event
		PreferenceHandler.deleteEvent(this, event.getName().replace(' ', '_'));

		// Restarting service
		Intent svc = new Intent(this, MyService.class);
		stopService(svc);
		startService(svc);

		// Go back to the home screen but pass a flag so that you can't hit back
		// and wind up here.
		Intent intent = new Intent(this, EventListActivity.class);
		startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}
}
