package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;

import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

public class Utilities {

	public static int createTransparancyColor(int color, int transparency) {
		return Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color));
	}

	public static Voyage loadCurrentNotebookFromIntent(Intent intent, DatabaseHelper databaseHelper, Activity context, String logtag) {
		// Add items in the list from the database
		if (intent.hasExtra(NotebookActivity.NOTEBOOKACTIVITY_VOYAGE_ID)) {
			int voyageId = intent.getIntExtra(NotebookActivity.NOTEBOOKACTIVITY_VOYAGE_ID, -1);
			if (voyageId != -1) {
				try {
					return databaseHelper.getVoyageDao().queryForId(voyageId);

				}
				catch (SQLException e) {
					e.printStackTrace();
					abortActivityWithError("Voyage query failed: SQL exception", context, logtag);
				}
			}
			else {
				abortActivityWithError("No valid voyage id passed to NotebookActivity!", context, logtag);
			}
		}
		else {
			abortActivityWithError("No voyage id passed to NotebookActivity!", context, logtag);
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
}
