package ch.hearc.devmobile.travelnotebook;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PostItemFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_item_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_item_form, menu);
		return true;
	}

}
