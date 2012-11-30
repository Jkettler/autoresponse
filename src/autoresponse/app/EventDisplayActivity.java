package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import autoresponse.util.AutoResponseEvent;

public class EventDisplayActivity extends Activity {

	private AutoResponseEvent event;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_display);

		event = (AutoResponseEvent) getIntent().getParcelableExtra(
				AutoResponseEvent.EVENT_KEY);

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

		// String[] details = {
		// event.getTimeString(),
		// event.getDayString(),
		// event.getLocationString(),
		// event.getRecieveTextString(),
		// event.getChangePhoneModeString(),
		// event.getDisplayReminderString(),
		// event.getSendTextResponseString()
		// };

		String[] details = { "test", "test", "test", "test", "test", "test",
				"test" };

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

		// Process the title:

		TextView eventNameView = (TextView) findViewById(R.id.event_viewer_eventName_textView);
		label = eventNameView.getText().toString();
		label += ' ' + event.getName();

		eventNameView.setText(label);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_event_display, menu);
		return true;
	}
}
