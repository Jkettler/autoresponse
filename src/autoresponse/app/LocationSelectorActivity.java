package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class LocationSelectorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);
        //TODO Assign instance variables using extras from the intent
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_selector, menu);
        return true;
    }
    
    public void createResponse(View view){
    	//TODO Add some kind of list-selector to the UI and assign locationSelected based on that
    	boolean locationSelected = false; 
    	
    	
    	if(locationSelected){
    		//TODO Start response creator and pass along whatever data we got from the condition selector
    		//Don't forget to include the location too
    	} else{
    		Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void addLocation(View view){
    	//TODO launch location creator
    	//Again, will need to pass data from previous activities around
    	//This will presumably just be whatever came from the condition selector
    	Intent intent = new Intent(this, LocationCreatorActivity.class);
    	startActivity(intent);
    }
}
