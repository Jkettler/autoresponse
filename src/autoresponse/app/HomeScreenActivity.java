package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import autoresponse.util.MyService;
import autoresponse.util.PreferenceHandler;

public class HomeScreenActivity extends Activity {

	private static final String TAG = "HomeScreenActivity";
	private static final boolean TEST = false;

	public static final String EVENT_NAME = "EVENT_NAME";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "entering onCreate");
		setContentView(R.layout.activity_home_screen);

		if (TEST) {
			runTests();
		}

		try {
			// start service
			Intent svc = new Intent(this, MyService.class);
			startService(svc);
		} catch (Exception e) {
			Log.d(TAG, "error in starting service");
		}
	}

	public void viewEvents(View view) {
		Log.d(TAG, "View event button clicked");
		Intent intent = new Intent(this, EventListActivity.class);
		startActivity(intent);
	}

	public void createEvent(View view) {
		Log.d(TAG, "Create event button clicked");
		// TODO Probably want a dialog to specify the name,
		// the first wireframe draft didn't include anywhere to input one.

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter a name");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String eventName = input.getText().toString();
				Intent intent = new Intent(getBaseContext(),
						ConditionSelectorActivity.class);
				intent.putExtra(EVENT_NAME, eventName);
				Log.d(TAG, "Starting ConditionSelector with event name: "+eventName);
				startActivity(intent);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		alert.show();

	}

	public void exitApp(View view) {
		Log.d(TAG, "Exit app button clicked");
		finish();
	}

	private void runTests() {
		boolean pass = PreferenceHandler.testPrefHandler(this);
		String out = "fail";
		if (pass) {
			out = "pass";
		}
		Toast.makeText(this, out, Toast.LENGTH_SHORT).show();
	}
}
