package kpk297.androidservicetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import kpk297.anroidservicetest.R;
import kpk297.anroidservicetest.R.layout;
import kpk297.anroidservicetest.R.menu;

public class ServiceTest extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_service_test, menu);
        return true;
    }
    
    public void startService(View view) {
    	Log.d("SERVICE", "in startService");
    	Toast.makeText(getApplicationContext(), "startService", Toast.LENGTH_SHORT).show();    
    	try {
    		//start service
    		Intent svc = new Intent(this, MyService.class);
    		startService(svc);
    	} catch (Exception e) {
    		Log.d("SERVICE", "error in starting service");
    	}
    }
    
    public void stopService(View view) {
    	Log.d("SERVICE", "in stopService");
    	Toast.makeText(getApplicationContext(), "stopService", Toast.LENGTH_SHORT).show();
    	try {
    		Intent svc = new Intent(this, MyService.class);
    		
    		stopService(svc);
    	} catch (Exception e) {
    		Log.d("SERVICE", "error in stopping service");
    	}
    }
}

