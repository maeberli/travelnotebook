package ch.hearc.devmobile.travelnotebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Image;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class PostItemFormActivity extends Activity implements DatePickerFragment.DateListener {

	/********************
	 * Private Static constants
	 ********************/
	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final int APPEND_FROM_GALLERY_CODE = 55;
	private static final int APPEND_FROM_CAMERA_CODE = 66;

	/********************
	 * Private members
	 ********************/

	private DatabaseHelper databaseHelper = null;
	private Voyage currentVoyage;
	private SimpleDateFormat dateFormatter;

	private GridView imageGrid;
	private ImageAdapter imageAdapter;
	private List<Image> images;
	private File photoToAppend;
	private Post postItem;

	/********************
	 * Public static members
	 ********************/
	public static final int RESULT_FAIL = 500;
	public static final int RESULT_SQL_FAIL = 501;
	public static final String ITEM_ID_KEY = "itemId";

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

		currentVoyage = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG);

		postItem = new Post();

		// Date formatter tool
		dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		// initialize gridview for images
		photoToAppend = null;
		images = new ArrayList<Image>();
		imageAdapter = new ImageAdapter(this, images);
		imageGrid = (GridView) findViewById(R.id.photo_grid);
		imageGrid.setAdapter(imageAdapter);
		imageGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Image removed = images.remove(position);

				// delete removed photo from memory.
				File photo = new File(removed.getImageURI());
				photo.delete();

				imageAdapter.notifyDataSetChanged();
			}
		});

		initButtons();

		// Sets default value to the start date
		TextView tvStartDate = (TextView) findViewById(R.id.post_item_start_date);
		Date now = new Date();
		tvStartDate.setText(dateFormatter.format(now.getTime()));

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

						Image image = new Image();
						image.setImageURI(destination.getPath());
						images.add(image);
						imageAdapter.notifyDataSetChanged();
					}
				}
			}
			break;
		case APPEND_FROM_CAMERA_CODE:
			if (resultCode == RESULT_OK && photoToAppend != null) {
				Image image = new Image();
				image.setImageURI(photoToAppend.getPath());
				images.add(image);
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

		// Append photo button
		Button btnAppendPhoto = (Button) findViewById(R.id.btn_photo_add);
		btnAppendPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAppendPhotoDialog().show();

			}
		});
	}

	private int savePostItem() throws Exception {
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

		// Creates or updates the post item
		Dao<Post, Integer> itemDao = databaseHelper.getPostDao();

		postItem.setTitle(title);
		postItem.setDescription(description);
		postItem.setDate(startDate);
		postItem.setLocation(startLocation);
		postItem.setVoyage(currentVoyage);

		Log.i(LOGTAG, postItem.toString());
		itemDao.createOrUpdate(postItem);

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
}
