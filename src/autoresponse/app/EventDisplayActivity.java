package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EventDisplayActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event_display, menu);
        return true;
    }
}
