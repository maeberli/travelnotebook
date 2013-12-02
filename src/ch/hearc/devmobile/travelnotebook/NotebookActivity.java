package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.google.android.gms.maps.GoogleMap;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class NotebookActivity extends Activity {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = NotebookActivity.class.getSimpleName();
	public static final String TRAVEL_ITEM_ID = "travelItemId";
	

	/********************
	 * Private members
	 ********************/
	private GoogleMap notebookMap = null;
	private DatabaseHelper databaseHelper = null;

	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private RelativeLayout drawerPanel;

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notebook, menu);
		return true;
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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_notebook);
		setUpMapIfNeeded();
		getDBHelperIfNecessary();

		buildDrawer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		getDBHelperIfNecessary();
	}

	/********************
	 * Private methods
	 ********************/
	private void buildDrawer() {
		// Travel list
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = new ArrayList<MenuElement>();
		drawerPanel = (RelativeLayout) findViewById(R.id.right_drawer);

		initButtons();

		// Add items in the list from the database
		try {

			MenuElement itemMenuElement = null;

			// Builds the query
			QueryBuilder<TravelItem, Integer> queryBuilder = getHelper()
					.getTravelItemDao().queryBuilder();
			Where<TravelItem, Integer> where = queryBuilder.where();
			where.eq(TravelItem.FIELD_VOYAGE,
					getIntent().getIntExtra(HomeActivity.NOTEBOOK_ID, 0));

			for (final TravelItem item : queryBuilder.query()) {

				itemMenuElement = new MenuElement(item.getTitle(),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Intent intent = new Intent(
								// NotebookActivity.this,
								// NotebookActivity.class);
								// intent.putExtra(NotebookActivity.this.TRAVEL_ITEM_ID,
								// item.getId());
								// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								// startActivity(intent);

								Toast.makeText(getApplicationContext(),
										item.getDescription(),
										Toast.LENGTH_SHORT).show();

								NotebookActivity.this.drawerLayout
										.closeDrawer(drawerPanel);
							}

						});
				drawerListViewItems.add(itemMenuElement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(LOGTAG, "Travel item query failed !");
		}

		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.right_drawer_list);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this,
				drawerListViewItems));

	}

	private void initButtons() {
		
		// Get the planning button
				Button btnBackHome = (Button) findViewById(R.id.btn_back_home);
				btnBackHome.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						 Intent intent = new Intent(NotebookActivity.this, HomeActivity.class);
						 intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						 startActivity(intent);

						NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
					}
				});
				
		// Get the planning button
		Button btnNewNotebook = (Button) findViewById(R.id.btn_get_planning);
		btnNewNotebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(NotebookActivity.this,
				// PlanningActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				// startActivity(intent);

				Toast.makeText(getApplicationContext(), "Planning",
						Toast.LENGTH_SHORT).show();

				NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Gets the add planning item button
		Button btnAddPlanningItem = (Button) findViewById(R.id.btn_add_planing_item);
		btnAddPlanningItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(NotebookActivity.this,
				// PlanningActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				// startActivity(intent);

				Toast.makeText(getApplicationContext(), "Add planning item",
						Toast.LENGTH_SHORT).show();

				NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Gets the add travel item button
		Button btnAddTravelItem = (Button) findViewById(R.id.btn_add_travel_item);
		btnAddTravelItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(NotebookActivity.this,
				// PlanningActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				// startActivity(intent);

				Toast.makeText(getApplicationContext(), "Add travel item",
						Toast.LENGTH_SHORT).show();

				NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});
	}

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

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
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

		travelItems.add(new TravelItem("Heathrow->Hamburg",
				"Please no kids in avion", startDate.getTime(), endDate
						.getTime(), "Heathrow Airport, London",
				"Hamburg Airport", voyage));
		
		travelItems.add(new TravelItem("Hamburg->ZRH",
				"Please no kids in avion", startDate.getTime(), endDate
						.getTime(), "Hamburg Airport",
				"Zurich, Airport", voyage));

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
