package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.larswerkman.holocolorpicker.ColorPicker;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewOnTravelItemActivity extends Activity {

	/********************
	 * Private members
	 ********************/
	
	private static final String LOGTAG = NewOnTravelItemActivity.class.getSimpleName();

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_new_on_travel_item);

		databaseHelper = OpenHelperManager
				.getHelper(this, DatabaseHelper.class);
		
		loadCurrentNotebookFromIntent();

		// Cancel button
		Button btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NewOnTravelItemActivity.this.setResult(RESULT_CANCELED);
				NewOnTravelItemActivity.this.finish();
			}
		});

		// Save button
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int id = NewOnTravelItemActivity.this.createItem();

					Intent intent = new Intent();
					intent.putExtra(ITEM_ID_KEY, id);

					NewOnTravelItemActivity.this.setResult(RESULT_OK, intent);
					NewOnTravelItemActivity.this.finish();

				} catch (SQLException e) {
					NewOnTravelItemActivity.this.setResult(RESULT_SQL_FAIL);
					e.printStackTrace();
					NewOnTravelItemActivity.this.finish();

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		
		// Add tags in the spinner
		Spinner spTag =  (Spinner) findViewById(R.id.item_tag);
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
		
		// Date formatter tool
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		// Gets the start date [not null]
		EditText etStartDate = (EditText) findViewById(R.id.item_start_date);
		String strStartDate = etStartDate.getText().toString();
		if (strStartDate.length() == 0)
			throw new Exception("Invalide start date");
		Date startDate = formatter.parse(strStartDate);
		
		// Gets the end date
		EditText etEndDate = (EditText) findViewById(R.id.item_start_date);
		String strEndDate = etEndDate.getText().toString();
		Date endDate = null;
		if (strEndDate.length() != 0)
			endDate = formatter.parse(strStartDate);
			
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
		
		// Creats the item
		Dao<TravelItem, Integer> itemDao = databaseHelper.getTravelItemDao();
		TravelItem item = new TravelItem(title, description, startDate, endDate, startLocation, endLocation, currentVoyage, tag);
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
	
	// Copy from NotebookActivity :(
	private void loadCurrentNotebookFromIntent() {
		// Add items in the list from the database
		if (getIntent().hasExtra(NotebookActivity.NOTEBOOKACTIVITY_VOYAGE_ID)) {
			int voyageId = getIntent().getIntExtra(NotebookActivity.NOTEBOOKACTIVITY_VOYAGE_ID,
					-1);
			if (voyageId != -1) {
				try {
					this.currentVoyage = databaseHelper.getVoyageDao()
							.queryForId(voyageId);

				} catch (SQLException e) {
					e.printStackTrace();
					abortActivityWithError("Voyage query failed: SQL exception");
				}
			} else {
				abortActivityWithError("No valid voyage id passed to NotebookActivity!");
			}
		} else {
			Log.e(LOGTAG, "==== INTENT HAS NO EXTRA ====");
			abortActivityWithError("No voyage id passed to NotebookActivity!");
		}
	}
	
	private void abortActivityWithError(String error) {
		Log.e(LOGTAG, error);

		Intent intent = new Intent();
		intent.putExtra(NotebookActivity.NOTEBOOKACTIVITY_RETURN_ERROR, error);

		this.setResult(RESULT_CANCELED, intent);
		this.finish();
	}

}