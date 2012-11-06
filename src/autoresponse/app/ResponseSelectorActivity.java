package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import autoresponse.util.AutoResponseEvent;

public class ResponseSelectorActivity extends Activity {

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

		// put the event in long term storage
		// alert the service/whatever we're using to detect events

		// Go back to the home screen but pass a flag so that you can't hit back
		// and wind up here.
		Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}
}
