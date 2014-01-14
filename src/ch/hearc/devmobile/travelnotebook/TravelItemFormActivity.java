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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.adapter.TagTypeSelectorAdapter;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Notebook;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class TravelItemFormActivity extends Activity implements DatePickerFragment.DateListener {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private DatabaseHelper databaseHelper = null;
	private TravelItem travelItem;
	private SimpleDateFormat dateFormatter;

	private EditText etTitle;
	private EditText etDescription;
	private TextView tvStartDate;
	private TextView tvEndDate;
	private EditText etStartLocation;
	private EditText etEndLocation;
	private Spinner spTag;
	private TagTypeSelectorAdapter tagTypeAdapter;

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

		// Date formatter tool
		dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		etTitle = (EditText) findViewById(R.id.item_title);
		etDescription = (EditText) findViewById(R.id.item_description);
		tvStartDate = (TextView) findViewById(R.id.travel_item_start_date);
		tvEndDate = (TextView) findViewById(R.id.travel_item_end_date);
		etStartLocation = (EditText) findViewById(R.id.item_start_location);
		etEndLocation = (EditText) findViewById(R.id.item_end_location);
		spTag = (Spinner) findViewById(R.id.item_tag);

		// Add tags in the spinner
		tagTypeAdapter = new TagTypeSelectorAdapter(getApplicationContext(), TagType.values());
		spTag.setAdapter(tagTypeAdapter);

		travelItem = new TravelItem();

		Intent intent = getIntent();
		if (intent.hasExtra(NOTEBOOK_ID_KEY)) {
			Notebook notebook = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG, NOTEBOOK_ID_KEY);
			travelItem.setNotebook(notebook);
		}
		else if (intent.hasExtra(TRAVELITEM_ID_KEY)) {
			int id = intent.getIntExtra(TRAVELITEM_ID_KEY, -1);
			if (id != -1) {
				try {
					travelItem = databaseHelper.getTravelItemDao().queryForId(id);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		// Init the buttons
		initButtons();

		initFields();

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

	private void initFields() {
		this.tvStartDate.setText(dateFormatter.format(travelItem.getStartDate()));
		this.etDescription.setText(travelItem.getDescription());
		this.etTitle.setText(travelItem.getTitle());
		this.etStartLocation.setText(travelItem.getStartLocation());

		int tagPosition = this.tagTypeAdapter.getPosition(travelItem.getTag().getTagType());
		spTag.setSelection(tagPosition);

		if (!travelItem.isSingleTimed()) {
			this.tvEndDate.setText(dateFormatter.format(travelItem.getEndDate()));

		}
		if (!travelItem.isSingleLocation()) {
			this.etEndLocation.setText(travelItem.getEndLocation());
		}
	}

	protected int createItem() throws Exception {

		// Gets the title [not null]
		String title = etTitle.getText().toString();
		if (title.length() == 0)
			throw new Exception("Invalide name");

		// Gets the description
		String description = etDescription.getText().toString();

		// Gets the start date [not null]
		String strStartDate = tvStartDate.getText().toString();
		if (strStartDate.length() == 0)
			throw new Exception("Invalide start date");
		Date startDate = dateFormatter.parse(strStartDate);

		// Gets the end date
		String strEndDate = tvEndDate.getText().toString();
		Date endDate = null;
		if (strEndDate.length() != 0)
			endDate = dateFormatter.parse(strEndDate);

		// Gets the start location [not null]
		String startLocation = etStartLocation.getText().toString();
		if (startLocation.length() == 0)
			throw new Exception("Invalide location");

		// Gets the end location
		String endLocation = etEndLocation.getText().toString();
		if (endLocation.length() == 0)
			endLocation = null;

		// Gets the tag
		String strTag = spTag.getSelectedItem().toString();

		// set values
		travelItem.setDescription(description);
		travelItem.setTitle(title);
		travelItem.setEndLocation(endLocation);
		travelItem.setStartLocation(startLocation);
		travelItem.setStartDate(startDate);
		travelItem.setEndDate(endDate);
		travelItem.getTag().setTagType(TagType.valueOf(strTag));

		// Creates or updates the item
		Dao<TravelItem, Integer> itemDao = databaseHelper.getTravelItemDao();
		Dao<Tag, Integer> tagDao = databaseHelper.getTagDao();

		Log.i(LOGTAG, travelItem.toString());
		itemDao.createOrUpdate(travelItem);
		tagDao.update(travelItem.getTag());

		return travelItem.getId();
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
		TextView tvStartView = (TextView) v;
		tvStartView.setText(date);
	}
}
