package autoresponse.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ResponseSelectorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_selector);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_response_selector, menu);
        return true;
    }
}
