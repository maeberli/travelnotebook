package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class NotebookActivity extends Activity {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = NotebookActivity.class.getSimpleName();
	public static final String TRAVEL_ITEM_ID = "travelItemId";
	public static final String NOTEBOOKACTIVITY_VOYAGE_ID = "notebookId";

	/********************
	 * Private members
	 ********************/
	private GoogleMap googleMap = null;
	private DatabaseHelper databaseHelper = null;

	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private RelativeLayout drawerPanel;
	private Voyage currentVoyage;
	private MapView notebookMapView;
	private TextView notebookTitleTextView;

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
		notebookMapView = (MapView) findViewById(R.id.notebook_mapview);
		notebookMapView.onCreate(savedInstanceState);
		
		notebookTitleTextView = (TextView)findViewById(R.id.notebookTitleTextView);

		setUpMapIfNeeded();
		getDBHelperIfNecessary();

		loadCurrentNotebookFromIntent();

		initButtons();
		initTitle();
		buildDrawer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		getDBHelperIfNecessary();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		notebookMapView.onDestroy();

		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		notebookMapView.onPause();
	}
	
	/********************
	 * Private methods
	 ********************/
	private void buildDrawer() {
		// Travel list
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = new ArrayList<MenuElement>();
		drawerPanel = (RelativeLayout) findViewById(R.id.right_drawer);

		// Add items in the list from the database
		MenuElement itemMenuElement = null;

		for (final TravelItem item : currentVoyage.getTravelItems()) {

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
									item.getDescription(), Toast.LENGTH_SHORT)
									.show();

							NotebookActivity.this.drawerLayout
									.closeDrawer(drawerPanel);
						}

					});
			drawerListViewItems.add(itemMenuElement);
		}

		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.right_drawer_list);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this,
				drawerListViewItems));

	}

	private void initButtons() {

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

	private void loadCurrentNotebookFromIntent() {
		// Add items in the list from the database
		if (getIntent().hasExtra(NOTEBOOKACTIVITY_VOYAGE_ID)) {

			int voyageId = getIntent().getIntExtra(NOTEBOOKACTIVITY_VOYAGE_ID,
					-1);
			if (voyageId != -1) {
				try {
					this.currentVoyage = databaseHelper.getVoyageDao()
							.queryForId(voyageId);

				} catch (SQLException e) {
					e.printStackTrace();
					Log.e(LOGTAG, "Voyage query failed !");
				}
			} else {
				Log.e(LOGTAG, "No valid voyage id passed to NotebookActivity!");
			}
		} else {
			Log.e(LOGTAG, "No voyage id passed to NotebookActivity!");
		}
	}

	private void initTitle() {
		String title = currentVoyage.getTitle();
		notebookTitleTextView.setText(title);
	}

	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = notebookMapView.getMap();

			if (googleMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		googleMap.addMarker(new MarkerOptions().title("hello World").position(new LatLng(0.0, 0.0)));
	}

	private void getDBHelperIfNecessary() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
	}

}
