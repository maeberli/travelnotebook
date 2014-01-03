package ch.hearc.devmobile.travelnotebook;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Voyage;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class NewTravelItemActivity extends Activity {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = NewPlanningItemActivity.class.getSimpleName();

	private DatabaseHelper databaseHelper = null;
	private Voyage currentVoyage;

	/********************
	 * Public members
	 ********************/
	public static final int RESULT_FAIL = 500;
	public static final int RESULT_SQL_FAIL = 501;
	public static final String ITEM_ID_KEY = "itemId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_new_travel_item);
		
		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		
		currentVoyage = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_travel_item, menu);
		return true;
	}

}
