package ch.hearc.devmobile.travelnotebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Image;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Notebook;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class PostItemFormActivity extends Activity implements DatePickerFragment.DateListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	/********************
	 * Private Static constants
	 ********************/
	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private static final int APPEND_FROM_GALLERY_CODE = 55;
	private static final int APPEND_FROM_CAMERA_CODE = 66;

	/********************
	 * Private members
	 ********************/

	private DatabaseHelper databaseHelper = null;
	private Notebook currentNotebook;
	private SimpleDateFormat dateFormatter;

	private Location currentLocation;
	private ImageAdapter imageAdapter;
	private File photoToAppend;
	private LocationClient locationClient;
	private Post postItem;

	private GridView imageGrid;
	private EditText etTitle;
	private EditText etStartLocation;
	private EditText etDescription;
	private TextView tvStartDate;

	/********************
	 * Public static members
	 ********************/
	public static final int RESULT_FAIL = 500;
	public static final int RESULT_SQL_FAIL = 501;
	public static final String POST_ID_KEY = "postId";
	public static final String NOOTEBOOK_ID_KEY = "notebookId";

	/********************
	 * Public methods
	 ********************/
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

	/********************
	 * Protected methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide application title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_post_item_form);

		databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		// analyze intent data
		Intent intent = getIntent();
		if (intent.hasExtra(NOOTEBOOK_ID_KEY)) {
			currentNotebook = Utilities.loadCurrentNotebookFromIntent(intent, databaseHelper, this, LOGTAG, NOOTEBOOK_ID_KEY);

			Log.v(LOGTAG, "Create new post");
			postItem = new Post();
			if (postItem.getImages() == null) {
				try {
					databaseHelper.getPostDao().assignEmptyForeignCollection(postItem, "images");
					postItem.getImages().clear();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else if (intent.hasExtra(POST_ID_KEY)) {
			int postid = intent.getIntExtra(POST_ID_KEY, -1);
			if (postid != -1) {
				try {
					postItem = databaseHelper.getPostDao().queryForId(postid);
					currentNotebook = postItem.getNotebook();
				}
				catch (SQLException e) {
				}
			}
		}

		if (postItem == null)
			Utilities.abortActivityWithError("PostItemForm start up failed: missing parameters", this, LOGTAG);

		// Date formatter tool
		dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		// initialize gridview for images
		photoToAppend = null;
		imageAdapter = new ImageAdapter(this, postItem.getImages());
		imageGrid = (GridView) findViewById(R.id.photo_grid);
		imageGrid.setAdapter(imageAdapter);

		// initialize other controls
		etTitle = (EditText) findViewById(R.id.post_item_title);
		etStartLocation = (EditText) findViewById(R.id.post_item_start_location);
		etDescription = (EditText) findViewById(R.id.post_item_description);
		tvStartDate = (TextView) findViewById(R.id.post_item_start_date);
		
		initControlsFromPost();

		initButtons();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case APPEND_FROM_GALLERY_CODE:
			if (resultCode == RESULT_OK) {
				Uri targetUri = data.getData();
				if (targetUri != null) {
					String filePath = loadImageFilePath(targetUri);

					File destination = createAppInternImageFile();
					if (destination != null) {
						saveBitmap(filePath, destination);

						Image image = new Image(destination.getPath(), postItem);
						postItem.getImages().add(image);
						imageAdapter.notifyDataSetChanged();
					}
				}
			}
			break;
		case APPEND_FROM_CAMERA_CODE:
			if (resultCode == RESULT_OK && photoToAppend != null) {
				Image image = new Image(photoToAppend.getPath(), postItem);
				postItem.getImages().add(image);
				imageAdapter.notifyDataSetChanged();
			}
			else if (photoToAppend != null) {
				photoToAppend.delete();
				photoToAppend = null;
			}
			break;
		default:
			Log.i(LOGTAG, "Unhandled on activity result(request code = " + requestCode + ", resultCode=" + resultCode + ", data=" + data.toString());
			break;
		}
	}

	/********************
	 * Private methods
	 ********************/

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
		(new GetAddressTask(this, etStartLocation)).execute(currentLocation);
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		if (locationClient != null) {
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
					int id = savePostItem();

					Intent intent = new Intent();
					intent.putExtra(POST_ID_KEY, id);

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

		// Append photo button
		Button btnAppendPhoto = (Button) findViewById(R.id.btn_photo_add);
		btnAppendPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAppendPhotoDialog().show();
			}
		});

		// Set action to update location button
		Button updateAddress = (Button) findViewById(R.id.btn_post_item_update_location);
		updateAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(LOGTAG, "Hey update updateAddress called");
				updateAddress();
			}
		});
	}

	private void initControlsFromPost() {
		this.etTitle.setText(postItem.getTitle());
		this.etDescription.setText(postItem.getDescription());
		tvStartDate.setText(dateFormatter.format(postItem.getDate().getTime()));
		this.etStartLocation.setText(postItem.getLocation());
	}

	private int savePostItem() throws Exception {
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

		// Gets the start location [not null]
		String startLocation = etStartLocation.getText().toString();
		if (startLocation.length() == 0)
			throw new Exception("Invalide location");

		// Creates or updates the post item

		postItem.setTitle(title);
		postItem.setDescription(description);
		postItem.setDate(startDate);
		postItem.setLocation(startLocation);
		postItem.setNotebook(currentNotebook);

		Dao<Post, Integer> itemDao = databaseHelper.getPostDao();
		Dao<Image, Integer> imageDao = databaseHelper.getImageDao();

		Log.v(LOGTAG, "postItem.getImages().size(): " + postItem.getImages().size());
		itemDao.createOrUpdate(postItem);

		for (Image image : postItem.getImages()) {
			imageDao.createOrUpdate(image);
		}

		return postItem.getId();
	}

	private Dialog createAppendPhotoDialog() {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.postitemformactivity_add_photo_actions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					startAppendPhotoFromCamera();
					break;
				case 1:
					startAppendPhotoFromGallery();
					break;
				default:
					Log.i(LOGTAG, "No action found for item: " + which);
				}
			}
		});
		builder.setTitle(R.string.add_from_actiondialog);
		return builder.create();
	}

	private void startAppendPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, APPEND_FROM_GALLERY_CODE);
	}

	private void startAppendPhotoFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// check if camera capture is available
		if (intent.resolveActivity(getPackageManager()) != null) {
			File photoFile = createAppInternImageFile();

			if (photoFile != null) {
				photoToAppend = photoFile;

				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

				startActivityForResult(intent, APPEND_FROM_CAMERA_CODE);
			}
		}
		else {
			Toast.makeText(this, R.string.no_camera_availlable, Toast.LENGTH_LONG).show();
		}
	}

	private File createAppInternImageFile() {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStorageDirectory();

		File image = new File(storageDir, imageFileName);
		try {
			if (image.createNewFile()) {
				return image;
			}
		}
		catch (IOException e) {
		}

		return null;
	}

	private String loadImageFilePath(Uri targetUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(targetUri, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}

	private void saveBitmap(String filePath, File destination) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		try {
			FileOutputStream fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		}
		catch (IOException ex) {
			Log.w(LOGTAG, "Save image error: " + ex.getMessage());
		}
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
