package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class EventListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        ListView eventListView = (ListView) findViewById(R.id.event_list_view);

        String[] items = new String[] {"Item 1", "Item 2", "Item 3"};
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        eventListView.setAdapter(adapter);
        //TODO pull in list of events from storage, populate list view, add listeners
        //TODO inside listener, jump to event view
        
    }

    public void closeActivity(View view){
    	finish();
    }
    
}
