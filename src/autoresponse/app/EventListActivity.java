package autoresponse.app;

import java.util.List;

import autoresponse.util.MyService;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import autoresponse.util.AutoResponseEvent;
import autoresponse.util.PreferenceHandler;

public class EventListActivity extends Activity {

	private final String TAG = "EventListActivity";
	private ListView mEventListView;
	private String selectedFromList;
	private List<AutoResponseEvent> mEvents; 

	public static final String EVENT_NAME = "EVENT_NAME";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "entering onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        mEventListView = (ListView) findViewById(R.id.event_list_view);

        //TODO: (beta) make it possible to turn off and on a particular event
        populateEventList();
    }
    
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.event_list_menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.new_event_menu_item:
    		createEvent();
    		return true;
    	}
    	return false;
    }
    
	private void createEvent() {
		Log.d(TAG, "Create event menu-button clicked");

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
    
    private void populateEventList() {
    	mEvents = PreferenceHandler.getEventList(this);
    	String[] names = getNames(mEvents);
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, names);
        
        mEventListView.setAdapter(adapter);

        mEventListView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
        		selectedFromList =(String) (mEventListView.getItemAtPosition(myItemInt));
        	}                 
        });
    }

    private String[] getNames(List<AutoResponseEvent> events) {
    	Log.d(TAG, "entering getNames");
		String[] out = new String[events.size()];
		for(int i=0; i<out.length; i++){
			out[i] = events.get(i).getName();
		}
		return out;
	}

    public void editConditions(View view){
    	Log.d(TAG, "entering closeActivity");
//    	Toast.makeText(this, selectedFromList, Toast.LENGTH_SHORT).show();
    	if(selectedFromList!=null){
    		Intent intent = new Intent(this, ConditionSelectorActivity.class);
    		intent.putExtra(AutoResponseEvent.EVENT_KEY, getEventFromList(selectedFromList));
        	startActivity(intent);
    	}
    }
    
    private AutoResponseEvent getEventFromList(String eventName) {
    	for(AutoResponseEvent event : mEvents){
    		if(event.getName().equals(eventName)){
    			return event;
    		}
    	}
    	return null;
	}

	public void editResponse(View view){
    	Log.d(TAG, "entering editResponse");
//    	Toast.makeText(this, selectedFromList, Toast.LENGTH_LONG).show();
    	if(selectedFromList != null) {
    		Intent intent = new Intent(this, ResponseSelectorActivity.class);
    		intent.putExtra(AutoResponseEvent.EVENT_KEY, getEventFromList(selectedFromList));
    		startActivity(intent);
    	}
    }
    
    public void deleteEvent(View view){
    	Log.d(TAG, "entering deleteEvent");
//    	Toast.makeText(this, selectedFromList, Toast.LENGTH_SHORT).show();

    	// delete the event
    	PreferenceHandler.deleteEvent(this, selectedFromList.replace(' ', '_'));
    	
    	// empty instance variables
    	selectedFromList = null;
    	
    	
    	// Restarting service
    	Intent svc = new Intent(this, MyService.class);
		stopService(svc);
		startService(svc);
		
		// redraw view with updated event list
		populateEventList();
		view.invalidate();
    }
}
