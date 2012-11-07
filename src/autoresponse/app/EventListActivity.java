package autoresponse.app;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
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

	private ListView mEventListView;
	private String selectedFromList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        mEventListView = (ListView) findViewById(R.id.event_list_view);

        //TODO: (beta) make it possible to turn off and on a particular event
        
        String[] names = getNames();
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, names);
        
        mEventListView.setAdapter(adapter);
        
        mEventListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
              selectedFromList =(String) (mEventListView.getItemAtPosition(myItemInt));
            }                 
      });
        //TODO pull in list of events from storage, populate list view, add listeners
        //TODO inside listener, jump to event view
        
        
    }

    private String[] getNames() {
		List<AutoResponseEvent> events = PreferenceHandler.getEventList(this);
		String[] out = new String[events.size()];
		for(int i=0; i<out.length; i++){
			out[i] = events.get(i).getName();
		}
		return out;
	}

	public void closeActivity(View view){
    	finish();
    }
    
    public void editConditions(View view){
    	Toast.makeText(this, selectedFromList, Toast.LENGTH_SHORT).show();
    	//TODO get activity, put it in the intent, then launch activity
    	if(selectedFromList!=null){
    		
    	}
    }
    
    public void editResponse(View view){
    	//TODO get activity, put it in the intent, then launch activity
    }
    
    public void deleteEvent(View view){
    	//delete activity, restart service
    }
}
