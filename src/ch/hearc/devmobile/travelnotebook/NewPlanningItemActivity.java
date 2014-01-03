package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.PlanningItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class NewPlanningItemActivity extends Activity {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = NewPlanningItemActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private DatabaseHelper databaseHelper = null;
	private Voyage currentVoyage;
	private SimpleDateFormat dateFormatter;

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

		setContentView(R.layout.activity_new_planning_item);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		currentVoyage = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG);
		
		// Date formatter tool
		dateFormatter = new SimpleDateFormat( DATE_FORMAT, Locale.getDefault() );

		// Cancel button
		Button btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NewPlanningItemActivity.this.setResult(RESULT_CANCELED);
				NewPlanningItemActivity.this.finish();
			}
		});

		// Save button
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int id = NewPlanningItemActivity.this.createItem();

					Intent intent = new Intent();
					intent.putExtra(ITEM_ID_KEY, id);

					NewPlanningItemActivity.this.setResult(RESULT_OK, intent);
					NewPlanningItemActivity.this.finish();

				}
				catch (SQLException e) {
					NewPlanningItemActivity.this.setResult(RESULT_SQL_FAIL);
					e.printStackTrace();
					NewPlanningItemActivity.this.finish();

				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});
		
		// Sets default value to the start date
		EditText etStartDate = (EditText) findViewById(R.id.item_start_date);
		Date now = new Date();
		etStartDate.setText(dateFormatter.format( now.getTime() ));

		// Add tags in the spinner
		Spinner spTag = (Spinner) findViewById(R.id.item_tag);
		spTag.setAdapter(new ArrayAdapter<TagType>(this, android.R.layout.simple_list_item_1, TagType.values()));

	}

	protected int createItem() throws Exception {

		// Gets the title [not null]
		EditText etTitle = (EditText) findViewById(R.id.item_title);
		String title = etTitle.getText().toString();
		if (title.length() == 0)
			throw new Exception("Invalide name");

		// Gets the description
		EditText etDescription = (EditText) findViewById(R.id.item_description);
		String description = etDescription.getText().toString();
		
		// Gets the start date [not null]
		EditText etStartDate = (EditText) findViewById(R.id.item_start_date);
		String strStartDate = etStartDate.getText().toString();
		if (strStartDate.length() == 0)
			throw new Exception("Invalide start date");
		Date startDate = dateFormatter.parse(strStartDate);

		// Gets the end date
		EditText etEndDate = (EditText) findViewById(R.id.item_end_date);
		String strEndDate = etEndDate.getText().toString();
		Date endDate = null;
		if (strEndDate.length() != 0)
			endDate = dateFormatter.parse(strStartDate);

		// Gets the start location [not null]
		EditText etStartLocation = (EditText) findViewById(R.id.item_start_location);
		String startLocation = etStartLocation.getText().toString();
		if (startLocation.length() == 0)
			throw new Exception("Invalide location");

		// Gets the end location
		EditText etEndLocation = (EditText) findViewById(R.id.item_end_location);
		String endLocation = etEndLocation.getText().toString();

		// Gets the tag
		Spinner spTag = (Spinner) findViewById(R.id.item_tag);
		String strTag = spTag.getSelectedItem().toString();
		Tag tag = new Tag(TagType.valueOf(strTag));

		// Creates the item
		Dao<PlanningItem, Integer> itemDao = databaseHelper.getTravelItemDao();
		PlanningItem item = new PlanningItem(title, description, startDate, endDate, startLocation, endLocation, currentVoyage, tag);
		Log.i(LOGTAG, item.toString());
		itemDao.create(item);

		return item.getId();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_on_travel_item, menu);
		return true;
	}
}
