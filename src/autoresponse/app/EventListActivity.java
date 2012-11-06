package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class EventListActivity extends Activity {

	private ListView mEventListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        mEventListView = (ListView) findViewById(R.id.event_list_view);

        //TODO: (beta) make it possible to turn off and on a particular event
        
        String[] items = new String[] {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"};
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, items);
        
        mEventListView.setAdapter(adapter);
        //TODO pull in list of events from storage, populate list view, add listeners
        //TODO inside listener, jump to event view
        
        
    }

    public void closeActivity(View view){
    	finish();
    }
    
    public void editConditions(View view){
//    	Toast.makeText(this, , Toast.LENGTH_SHORT).show();
    	//TODO get activity, put it in the intent, then launch activity
    }
    
    public void editResponse(View view){
    	//TODO get activity, put it in the intent, then launch activity
    }
    
    public void deleteEvent(View view){
    	//delete activity, restart service
    }
}
