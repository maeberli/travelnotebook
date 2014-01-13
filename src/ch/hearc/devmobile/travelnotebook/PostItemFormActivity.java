package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Voyage;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PostItemFormActivity extends Activity implements DatePickerFragment.DateListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	/********************
	 * Private members
	 ********************/

	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private DatabaseHelper databaseHelper = null;
	private Voyage currentVoyage;
	private SimpleDateFormat dateFormatter;

	private EditText address;
	private Location currentLocation;
	private LocationClient locationClient;

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
		TextView tvStartDate = (TextView) findViewById(R.id.post_item_start_date);
		Date now = new Date();
		tvStartDate.setText(dateFormatter.format(now.getTime()));

		// Set default value to the position
		address = (EditText) findViewById(R.id.post_item_start_location);
		
		// Set action to update location button
		Button updateAdress = (Button) findViewById(R.id.btn_post_item_update_location);
		updateAdress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateAddress();
			}
		});
	}

	
	/*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        setUpLocationClientIfNeeded();
        locationClient.connect();
    }
    
    
    private void setUpLocationClientIfNeeded() {	
		if (locationClient == null) {
			locationClient = new LocationClient(this, this, this);
		}
	}
    
    private void updateAddress() {
    	(new GetAddressTask(this, address)).execute(currentLocation);
    }


	/*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
    	if(locationClient != null ){
            locationClient.disconnect();
    	}
        super.onStop();
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
		TextView tvStartDate = (TextView) findViewById(R.id.post_item_start_date);
		String strStartDate = tvStartDate.getText().toString();
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

	/***********************************************
	 * Google play service interface implementaiton
	 ***********************************************/

	@Override
	public void onConnected(Bundle dataBundle) {
		currentLocation = locationClient.getLastLocation();
	}

	@Override
	public void onDisconnected() {
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			}
			catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		}
		else {
			// NOP
		}
	}

}
