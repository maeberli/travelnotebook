package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;
import ch.hearc.devmobile.travelnotebook.database.Voyage;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

public class HomeActivity extends FragmentActivity {

	private static final int VOYAGE_LINE_COLOR_TRANSPARENCY = 180;
	/********************
	 * Static class members
	 ********************/
	private static final String LOGTAG = HomeActivity.class.getSimpleName();
	private static final int NEW_NOTEBOOK_CODE = 100;
	private static final int SETTINGS_CODE = 200;
	private static final int MAP_BOUNDS_MARGIN = 50;

	/********************
	 * Private members
	 ********************/
	private DatabaseHelper databaseHelper = null;
	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private RelativeLayout drawerPanel;
	private SupportMapFragment homeMapView = null;
	private GoogleMap googleMap = null;
	private Geocoder geocoder;

	private Map<Marker, Voyage> markers;

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_home);

		geocoder = new Geocoder(this);
		markers = new HashMap<Marker, Voyage>();

		homeMapView = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.home_map);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == NEW_NOTEBOOK_CODE) {
			switch (resultCode) {

			case RESULT_OK:
				Toast.makeText(this, "Notebook saved", Toast.LENGTH_LONG)
						.show();
				buildDrawer();
				startNotebookActivity(data.getExtras().getInt(
						NewNotebookActivity.NOTEBOOK_ID_KEY));
				break;

			case RESULT_CANCELED:
				Toast.makeText(this, "Canceled !", Toast.LENGTH_LONG).show();
				break;
			case NewNotebookActivity.RESULT_FAIL:
				Log.e(LOGTAG, "Result fail");
			case NewNotebookActivity.RESULT_SQL_FAIL:
				Toast.makeText(getApplicationContext(), "Creation failed !",
						Toast.LENGTH_SHORT).show();
				break;

			}
		} else if (requestCode == SETTINGS_CODE) {
			switch (resultCode) {
			case RESULT_CANCELED:
				// NOP
				break;
			case RESULT_OK:
				// TODO Refresh activity
				break;
			}
		}
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
		drawerPanel = (RelativeLayout) findViewById(R.id.right_drawer);

		// New notebook button
		Button btnNewNotebook = (Button) findViewById(R.id.btn_new_notebook);
		btnNewNotebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						NewNotebookActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(intent, NEW_NOTEBOOK_CODE);

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
				startActivityForResult(intent, SETTINGS_CODE);
				HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Add voyages in the list from the database
		try {

			for (final Voyage voyage : getHelper().getVoyageDao().queryForAll()) {

				addNoteBookLink(voyage);
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

	private void addNoteBookLink(final Voyage voyage) {

		MenuElement voyageMenuElement = null;
		voyageMenuElement = new MenuElement(voyage.getTitle(),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								NotebookActivity.class);
						intent.putExtra("notebookId", voyage.getId());
						startActivity(intent);
						HomeActivity.this.drawerLayout.closeDrawer(drawerPanel);
					}

				});
		drawerListViewItems.add(voyageMenuElement);
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
		// bounds builder to get outer bounds of each position.
		final Builder boundsBuilder = new LatLngBounds.Builder();

		// Initialize events
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				Voyage voyage = markers.get(marker);
				startNotebookActivity(voyage.getId());
			}
		});

		if (this.homeMapView.getView().getViewTreeObserver().isAlive()) {
			homeMapView.getView().getViewTreeObserver()
					.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						public void onGlobalLayout() {

							homeMapView.getView().getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);

							LatLngBounds latLngBounds = null;

							try {
								latLngBounds = boundsBuilder.build();
							} catch (IllegalStateException e) {

								Log.i(LOGTAG,
										"No LatLng in boundsBuilder: will not center the map");
							}
							if (latLngBounds != null)
								googleMap.moveCamera(CameraUpdateFactory
										.newLatLngBounds(latLngBounds,
												MAP_BOUNDS_MARGIN));
						}
					});
		}

		// show informations.
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

							// Append startPosition to bounds list
							boundsBuilder.include(startLatLng);
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

								// Append endPosition to bounds list
								boundsBuilder.include(endLatLng);
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
				int voyageColorTransparent = Utilities.createTransparancyColor(
						voyageColor, VOYAGE_LINE_COLOR_TRANSPARENCY);

				// get the marker position of the current voyage
				// if the center of the voyageBounds is not within the bound,
				// take position in the middle of the voyage
				LatLng markerPosition = travelItemPositions.getFirst();

				googleMap.addPolygon(new PolygonOptions()
						.addAll(travelItemPositions)
						.strokeColor(voyageColorTransparent).geodesic(true));

				Marker marker = googleMap.addMarker(new MarkerOptions()
						.position(markerPosition).title(voyage.getTitle()));

				// append the marker and the voyage to the markers list
				// Allows to associate click events to markers
				this.markers.put(marker, voyage);

			}
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
			e.printStackTrace();
		}
	}

	private void startNotebookActivity(int id) {
		Intent intent = new Intent(HomeActivity.this, NotebookActivity.class);
		intent.putExtra(NotebookActivity.NOTEBOOKACTIVITY_VOYAGE_ID, id);
		startActivity(intent);
	}
}
