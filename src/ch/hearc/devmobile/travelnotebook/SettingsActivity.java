package ch.hearc.devmobile.travelnotebook;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity {

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	/********************
	 * Protected methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_settings);
				
		Button btnSave = (Button) findViewById(R.id.save_settings);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingsActivity.this.saveSettings();
				SettingsActivity.this.finish();
			}
		});
	}

	/********************
	 * Private methods
	 ********************/
	private void saveSettings() {
		// save settings here
	}

}
