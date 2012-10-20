package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

public class ConditionSelectorActivity extends Activity {

	private final String TAG = "ConditionSelectorActivity"; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_selector);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_condition_selector, menu);
        return true;
    }
    
    public void createCondition(View view){
    	//If location checked
    	CheckBox locationBox = (CheckBox) findViewById(R.id.location_checkbox);
    	//TODO pull info from other checkboxes --maybe use helper method to keep code clean...
    	//TODO if nothing is given as input, stay in activity and put up a toast asking for input
    	if(locationBox.isChecked()){
    		//Go to location selector
    		Log.d(TAG, "Continue button was clicked with location checked.");
    	} else {
    		//Go to response selector
    		Log.d(TAG, "Continue button was clicked without location checked.");
    		Intent intent = new Intent(this, ResponseSelectorActivity.class);
    		//TODO put info from condition into intent as extras
        	startActivity(intent);
    	}
    }
}
