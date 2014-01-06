package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Voyage;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostItemFormActivity extends Activity {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
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

		setContentView(R.layout.activity_post_item_form);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		currentVoyage = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG);

		// Date formatter tool
		dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		initButtons();

		// Sets default value to the start date
		EditText etStartDate = (EditText) findViewById(R.id.post_item_start_date);
		Date now = new Date();
		etStartDate.setText(dateFormatter.format(now.getTime()));

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
					intent.putExtra(ITEM_ID_KEY, id);

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
		EditText etTitle = (EditText) findViewById(R.id.post_item_title);
		String title = etTitle.getText().toString();
		if (title.length() == 0)
			throw new Exception("Invalide name");

		// Gets the description
		EditText etDescription = (EditText) findViewById(R.id.post_item_description);
		String description = etDescription.getText().toString();

		// Gets the start date [not null]
		EditText etStartDate = (EditText) findViewById(R.id.post_item_start_date);
		String strStartDate = etStartDate.getText().toString();
		if (strStartDate.length() == 0)
			throw new Exception("Invalide start date");
		Date startDate = dateFormatter.parse(strStartDate);

		// Gets the start location [not null]
		EditText etStartLocation = (EditText) findViewById(R.id.post_item_start_location);
		String startLocation = etStartLocation.getText().toString();
		if (startLocation.length() == 0)
			throw new Exception("Invalide location");

		// Creates the item
		Dao<Post, Integer> itemDao = databaseHelper.getPostDao();
		Post item = new Post(title, description, startDate, startLocation, currentVoyage);
		Log.i(LOGTAG, item.toString());
		itemDao.create(item);

		return item.getId();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_item_form, menu);
		return true;
	}

}
