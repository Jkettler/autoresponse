package autoresponse.app;

import java.util.List;

import autoresponse.util.MyService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "entering onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        mEventListView = (ListView) findViewById(R.id.event_list_view);

        //TODO: (beta) make it possible to turn off and on a particular event
        populateEventList();
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

	public void closeActivity(View view){
		Log.d(TAG, "entering closeActivity");
    	finish();
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
