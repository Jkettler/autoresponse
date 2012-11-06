package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import autoresponse.util.AutoResponseEvent;

public class LocationSelectorActivity extends Activity {

	private AutoResponseEvent mEvent;
	private ListView mEventListView;

	private String selectedLocationName;
	private boolean locationSelected = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);
        Intent intent = getIntent();
        mEvent = intent.getParcelableExtra(AutoResponseEvent.EVENT_KEY);
        
        //TODO pull in location list
    	//TODO populate list view for real instead of using this fake list
        String[] items = new String[] {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"};
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, items);
        
        mEventListView = (ListView) findViewById(R.id.location_list_view);
        
        mEventListView.setAdapter(adapter);
        
        mEventListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
              selectedLocationName =(String) (mEventListView.getItemAtPosition(myItemInt));
              locationSelected = true;
            }                 
      });

    }
    
    public void createResponse(View view){

    	if(locationSelected){
    		//TODO Start response selector and pass along whatever data we got from the condition selector
    		//Don't forget to include the location too
    		
    		//Set the location
    		
    		//Pass the updated event to the response selector
    		Intent intent = new Intent(this, ResponseSelectorActivity.class);
    		intent.putExtra(AutoResponseEvent.EVENT_KEY, mEvent);
    		startActivity(intent);
    		
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
