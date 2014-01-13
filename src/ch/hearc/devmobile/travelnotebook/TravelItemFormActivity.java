package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Notebook;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class TravelItemFormActivity extends Activity implements DatePickerFragment.DateListener {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private DatabaseHelper databaseHelper = null;
	private Notebook currentNotebook;
	private SimpleDateFormat dateFormatter;

	/********************
	 * Public members
	 ********************/
	public static final int RESULT_FAIL = 500;
	public static final int RESULT_SQL_FAIL = 501;
	public static final String TRAVELITEM_ID_KEY = "itemId";
	public static final String NOTEBOOK_ID_KEY = "notebookId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_travel_item_form);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		currentNotebook = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG, NOTEBOOK_ID_KEY);
		
		// Date formatter tool
		dateFormatter = new SimpleDateFormat( DATE_FORMAT, Locale.getDefault() );
		
		// Init the buttons
		initButtons();
		
		// Sets default value to the start date
		TextView tvStartView = (TextView) findViewById(R.id.travel_item_start_date);
		Date now = new Date();
	    tvStartView.setText(dateFormatter.format( now.getTime() ));

		// Add tags in the spinner
		Spinner spTag = (Spinner) findViewById(R.id.item_tag);
		spTag.setAdapter(new ArrayAdapter<TagType>(this, android.R.layout.simple_list_item_1, TagType.values()));

	}

	private void initButtons() {
		// Cancel button
				Button btnCancel = (Button) findViewById(R.id.btn_cancel);
				btnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setResult(RESULT_CANCELED);
						finish();
					}
				});
		
		// Save button
		Button btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int id = createItem();

					Intent intent = new Intent();
					intent.putExtra(TRAVELITEM_ID_KEY, id);

					setResult(RESULT_OK, intent);
					finish();

				}
				catch (SQLException e) {
					setResult(RESULT_SQL_FAIL);
					e.printStackTrace();
					finish();

				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});
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
		TextView tvStartDate = (TextView) findViewById(R.id.travel_item_start_date);
		String strStartDate = tvStartDate.getText().toString();
		if (strStartDate.length() == 0)
			throw new Exception("Invalide start date");
		Date startDate = dateFormatter.parse(strStartDate);

		// Gets the end date
		TextView tvEndDate = (TextView) findViewById(R.id.travel_item_end_date);
		String strEndDate = tvEndDate.getText().toString();
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
		if ( endLocation.length() == 0 )
			endLocation = null;

		// Gets the tag
		Spinner spTag = (Spinner) findViewById(R.id.item_tag);
		String strTag = spTag.getSelectedItem().toString();
		Tag tag = new Tag(TagType.valueOf(strTag));

		// Creates the item
		Dao<TravelItem, Integer> itemDao = databaseHelper.getTravelItemDao();
		TravelItem item = new TravelItem(title, description, startDate, endDate, startLocation, endLocation, currentNotebook, tag);
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
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment(v);
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void returnDate(String date, View v) {
		Log.i(LOGTAG, "date returned");
	    TextView tvStartView = (TextView)v;
	    tvStartView.setText(date);
	}
}
