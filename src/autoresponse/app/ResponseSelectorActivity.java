package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import autoresponse.util.AutoResponseEvent;

public class ResponseSelectorActivity extends Activity {

	AutoResponseEvent mEvent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_selector);
//      TODO  mEvent = savedInstanceState.getParcelable(key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_response_selector, menu);
        return true;
    }
    
    public void createEvent(View view){
    	//TODO Pull in data from UI, add it to the AutoResponseEvent object
    	//put the event in long term storage
    	//alert the service/whatever we're using to detect events
    }
}
