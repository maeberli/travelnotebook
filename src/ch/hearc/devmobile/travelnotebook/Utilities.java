package ch.hearc.devmobile.travelnotebook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Notebook;

import com.google.android.gms.maps.model.LatLng;

public class Utilities {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	public static int createTransparancyColor(int color, int transparency) {
		return Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color));
	}

	public static Notebook loadCurrentNotebookFromIntent(Intent intent, DatabaseHelper databaseHelper, Activity context, String logtag, String idName) {
		// Add items in the list from the database
		if (intent.hasExtra(idName)) {
			int notebookId = intent.getIntExtra(idName, -1);
			if (notebookId != -1) {
				try {
					return databaseHelper.getNotebookDao().queryForId(notebookId);

				}
				catch (SQLException e) {
					e.printStackTrace();
					abortActivityWithError("Notebook query failed: SQL exception", context, logtag);
				}
			}
			else {
				abortActivityWithError("No valid notebook id passed to NotebookActivity!", context, logtag);
			}
		}
		else {
			abortActivityWithError("No notebook id passed to NotebookActivity!", context, logtag);
		}
		return null;
	}

	public static void abortActivityWithError(String error, Activity context, String logtag) {
		Log.e(logtag, error);

		Intent intent = new Intent();
		intent.putExtra(NotebookActivity.NOTEBOOKACTIVITY_RETURN_ERROR, error);

		context.setResult(Activity.RESULT_CANCELED, intent);
		context.finish();
	}

	/********************
	 * GEOCODING
	 ********************/
	public static LatLng getLocation(Geocoder geocoder, String name, int maxresults, String logtag) {
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocationName(name, maxresults);
			Log.d(logtag, "Addresses found for " + name + ": " + addresses.size());

			if (addresses.size() > 0) {
				LatLng location = addressToLatLng(addresses.get(0));

				Log.d(logtag, "Geocoded location of " + name + ": " + location);
				return location;
			}
		}
		catch (IOException e) {
			Log.i(logtag, "Address <" + name + "> not found. Return Lat: 0, Long:0 .");
			return new LatLng(0, 0);
		}

		return null;
	}

	public static LatLng addressToLatLng(Address address) {
		return new LatLng(address.getLatitude(), address.getLongitude());
	}

	/********************
	 * GEOCODING
	 ********************/
	public static Bitmap loadImage(String path, int maxSizePX) {

		int targetW = maxSizePX;
		int targetH = maxSizePX;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		return bitmap;
	}
}
