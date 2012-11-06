package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import autoresponse.util.AutoResponseEvent;

public class LocationSelectorActivity extends Activity {

	AutoResponseEvent mEvent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);
        Intent intent = getIntent();
        mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
        
    	//TODO Add some kind of list-selector to the UI and assign locationSelected based on that
    	//TODO pull in location list
    	//TODO populate list view
    }
    
    public void createResponse(View view){

    	boolean locationSelected = false; 
    	
    	
    	if(locationSelected){
    		//TODO Start response creator and pass along whatever data we got from the condition selector
    		//Don't forget to include the location too
    		
    	} else{
    		Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void addLocation(View view){
    	//Launch location creator
    	//Again, pass data from previous activities around
    	//This will just be whatever came from the condition selector
    	Intent intent = new Intent(this, LocationCreatorActivity.class);
    	intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
    	startActivity(intent);
    }
}
