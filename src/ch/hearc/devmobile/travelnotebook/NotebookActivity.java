package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.google.android.gms.maps.GoogleMap;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class NotebookActivity extends Activity {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = NotebookActivity.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	private GoogleMap notebookMap = null;
	private DatabaseHelper databaseHelper = null;

	/********************
	 * Proteted methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notebook);

		setUpMapIfNeeded();
		getDBHelperIfNecessary();

		createDBEntries();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		getDBHelperIfNecessary();
	}

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.notebook, menu);
		return true;
	}

	/********************
	 * Private methods
	 ********************/
	private void setUpMapIfNeeded() {
		// if (notebookMap == null) {
		// MapView mapView = (MapView) (findViewById(R.id.notebook_map));
		//
		// notebookMap = mapView.getMap();
		//
		// if (notebookMap != null) {
		// setUpMap();
		// }
		// }
	}

	private void setUpMap() {
	}

	private void createDBEntries() {
		Log.i(LOGTAG, "Create example db entries");

		try {
			Dao<Voyage, Integer> voyageDao = databaseHelper.getVoyageDao();

			for (Voyage voyage : voyageDao.queryForAll()) {
				voyageDao.delete(voyage);
			}
		} catch (SQLException e) {
			Log.e(LOGTAG, "Voyage creation error:" + e.getMessage());
			e.printStackTrace();
		}

		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.HOUR, 1);

		Voyage voyage = new Voyage("Weekend trip London", Color.BLUE);

		List<TravelItem> travelItems = new ArrayList<TravelItem>();

		travelItems.add(new TravelItem("ZHR-London",
				"The flight with the numer xy", startDate.getTime(), endDate
						.getTime(), "Zurich, Airport", "London, Heathrow",
				voyage));

		travelItems.add(new TravelItem("Bus Transfer to the hotel",
				"The bus leafes heathrow in terminal 2", startDate.getTime(),
				endDate.getTime(), "London,Heathrow airport",
				"Hilton Hotel, London", voyage));

		travelItems.add(new TravelItem("Senior Suite in Hilton Hotel",
				"Let's enjoy the welness area", startDate.getTime(), endDate
						.getTime(), "Hilton Hotel, London", voyage));

		travelItems.add(new TravelItem("Chinesse food",
				"Yea in london i usually eat chinesse food.", startDate
						.getTime(), "DownTown London", voyage));

		travelItems
				.add(new TravelItem(
						"Taxi Transfer Hotel-> Airport",
						"As a typicall tourist i wan't to take a classic london cab to return to the airport",
						startDate.getTime(), endDate.getTime(),
						"Hilton Hotel, London", "Heathrow Airport, London",
						voyage));

		travelItems.add(new TravelItem("Heathrow->ZRH",
				"Please no kids in avion", startDate.getTime(), endDate
						.getTime(), "Heathrow Airport, London",
				"Zurich International Airport", voyage));

		try {
			for (TravelItem travelItem : travelItems) {
				databaseHelper.getTravelItemDao().create(travelItem);
			}
		} catch (SQLException e) {
			Log.e(LOGTAG, "Voyage creation error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void getDBHelperIfNecessary() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
	}

}
