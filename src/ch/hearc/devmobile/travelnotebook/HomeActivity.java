package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

public class HomeActivity extends Activity {

	/********************
	 * Static class members
	 ********************/
	private static final String LOGTAG = HomeActivity.class.getSimpleName();
	public static final String NOTEBOOK_ID = "notebookId";

	/********************
	 * Private members
	 ********************/
	private DatabaseHelper databaseHelper = null;
	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private MapView homeMapView = null;
	private GoogleMap googleMap = null;
	private Geocoder geocoder;

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		homeMapView.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		homeMapView.onSaveInstanceState(outState);
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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_home);

		geocoder = new Geocoder(this);

		homeMapView = (MapView) findViewById(R.id.home_map);
		homeMapView.onCreate(savedInstanceState);

		buildDrawer();

		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();

		homeMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		homeMapView.onDestroy();

		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		homeMapView.onPause();
	}

	/********************
	 * Private methods
	 ********************/
	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	private void buildDrawer() {
		// Travel list
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListViewItems = new ArrayList<MenuElement>();
		final RelativeLayout drawerPanel = (RelativeLayout) findViewById(R.id.right_drawer);

		// New notebook button
		Button btnNewNotebook = (Button) findViewById(R.id.btn_new_notebook);
		btnNewNotebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						NotebookActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Settings button
		Button btnSettings = (Button) findViewById(R.id.btn_settings);
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						SettingsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Add voyages in the list from the database
		try {

			MenuElement voyageMenuElement = null;
			for (final Voyage voyage : getHelper().getVoyageDao().queryForAll()) {

				voyageMenuElement = new MenuElement(voyage.getTitle(),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(HomeActivity.this,
										NotebookActivity.class);
								intent.putExtra("notebookId", voyage.getId());
								startActivity(intent);
								HomeActivity.this.drawerLayout
										.closeDrawer(drawerPanel);
							}

						});
				drawerListViewItems.add(voyageMenuElement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.right_drawer_list);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this,
				drawerListViewItems));

	}

	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = homeMapView.getMap();

			if (googleMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		Log.i(LOGTAG, "SetupMap with polygons and markers");

		try {
			List<Voyage> voyages = this.getHelper().getVoyageDao()
					.queryForAll();

			for (Voyage voyage : voyages) {

				ForeignCollection<TravelItem> travelItems = voyage
						.getTravelItems();

				LinkedList<LatLng> travelItemPositions = new LinkedList<LatLng>();

				for (TravelItem travelItem : travelItems) {

					LatLng startLatLng = travelItem
							.getStartLocationPosition(geocoder);
					Log.i(LOGTAG, "startLatLng" + startLatLng);

					if (startLatLng != null) {
						if (travelItemPositions.listIterator() != null
								&& !travelItemPositions.listIterator().equals(
										startLatLng)) {
							travelItemPositions.addLast(startLatLng);
						} else {
							Log.i(LOGTAG, "startPosition already in list");
						}

					} else {
						Log.w(LOGTAG,
								"No start position found for travelItem: "
										+ travelItem);
					}

					if (!travelItem.isSingleLocation()) {
						LatLng endLatLng = travelItem
								.getEndLocationPosition(geocoder);

						if (endLatLng != null) {
							if (travelItemPositions.listIterator() != null
									&& !travelItemPositions.listIterator()
											.equals(endLatLng)) {
								travelItemPositions.addLast(endLatLng);
							} else {
								Log.i(LOGTAG, "endPosition already in list");
							}
						} else {
							Log.w(LOGTAG,
									"No end position found for travelItem: "
											+ travelItem);
						}
					}
				}

				// Get the colors associated to the voyage
				int voyageColor = voyage.getColor();
				int voyageColorTransparent = Color.argb(180,
						Color.red(voyageColor), Color.green(voyageColor),
						Color.blue(voyageColor));

				// get the marker position of the current voyage
				// if the center of the voyageBounds is not within the bound,
				// take position in the middle of the voyage
				LatLng markerPosition = travelItemPositions.getFirst();

				googleMap.addPolygon(new PolygonOptions()
						.addAll(travelItemPositions)
						.fillColor(voyageColorTransparent)
						.strokeColor(voyageColor).geodesic(true));

				googleMap.addMarker(new MarkerOptions()
						.position(markerPosition).title(voyage.getTitle()));

			}
		} catch (Exception e) {
			Log.e(LOGTAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
