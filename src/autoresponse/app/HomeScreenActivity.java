package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class HomeScreenActivity extends Activity {

	private static final String TAG = "HomeScreenActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public void viewEvents(View view){
    	Log.d(TAG, "View event button clicked");
    	Intent intent = new Intent(this, EventListActivity.class);
    	startActivity(intent);
    }
    
    public void createEvent(View view) {
    	Log.d(TAG, "Create event button clicked");
    	//TODO Probably want a dialog to specify the name, 
    	//the first wireframe draft didn't include anywhere to input one. 
    	Intent intent = new Intent(this, ConditionSelectorActivity.class);
    	startActivity(intent);
	}
    
    public void exitApp(View view){
    	Log.d(TAG, "Exit app button clicked");
    	finish();
    }
}
