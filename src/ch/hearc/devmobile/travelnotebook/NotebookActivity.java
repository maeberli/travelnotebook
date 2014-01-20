package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.devmobile.travelnotebook.adapter.MenuElement;
import ch.hearc.devmobile.travelnotebook.adapter.MenuElementArrayAdapter;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Notebook;
import ch.hearc.devmobile.travelnotebook.database.Post;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class NotebookActivity extends FragmentActivity {

	/********************
	 * Private Static constants
	 ********************/
	private static final String LOGTAG = NotebookActivity.class.getSimpleName();
	private static final int MAP_BOUNDS_MARGIN = 100;
	private static final int NOTEBOOK_ITEM_LINE_TRANSPARENCY = 50;
	private static final int NOTEBOOK_ITEM_LINE_TRANSPARENCY_SELECTED = 255;
	private static final int NEW_TRAVELITEM_CODE = 110;
	private static final int EDIT_TRAVELITEM_CODE = 111;
	private static final int SHOW_TRAVEL_ITEM_CODE = 112;
	private static final int NEW_POST_CODE = 120;
	private static final int EDIT_POST_CODE = 121;
	private static final int SHOW_POST_CODE = 122;

	/********************
	 * Public Static constants
	 ********************/
	public static final String TRAVEL_ITEM_ID = "travelItemId";
	public static final String NOTEBOOKACTIVITY_NOTEBOOK_ID = "notebookId";
	public static final String NOTEBOOKACTIVITY_RETURN_ERROR = "error";

	/********************
	 * Private members
	 ********************/
	private GoogleMap googleMap = null;
	private DatabaseHelper databaseHelper = null;

	private List<MenuElement> drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private RelativeLayout drawerPanel;
	private Notebook currentNotebook;
	private SupportMapFragment notebookMapView;
	private TextView notebookTitleTextView;
	private Geocoder geocoder;

	private Marker lastClickedMarker;
	private Map<Marker, TravelItem> travelItemMarkers;
	private Map<Marker, Post> postMarkers;
	private Map<TravelItem, Polygon> polygons;
	private BitmapDescriptor postIcon;

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_notebook);

		// Variable instanciation
		geocoder = new Geocoder(this);
		travelItemMarkers = new HashMap<Marker, TravelItem>();
		postMarkers = new HashMap<Marker, Post>();
		polygons = new HashMap<TravelItem, Polygon>();
		lastClickedMarker = null;

		notebookMapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.notebook_mapview);

		notebookTitleTextView = (TextView) findViewById(R.id.notebookTitleTextView);

		// get the DB Helper first.
		getDBHelper();

		// Now we can load the notebook to treat in this activity.
		currentNotebook = Utilities.loadCurrentNotebookFromIntent(getIntent(), databaseHelper, this, LOGTAG, NOTEBOOKACTIVITY_NOTEBOOK_ID);

		// The last task is to setup the UI.
		setUpMapIfNeeded();

		initButtons();
		initTitle();
		buildDrawer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(LOGTAG, "On resume called");
		setUpMapIfNeeded();
		getDBHelper();
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(LOGTAG, "On activity result called");

		if (requestCode == NEW_POST_CODE || requestCode == NEW_TRAVELITEM_CODE || requestCode == EDIT_POST_CODE || requestCode == EDIT_TRAVELITEM_CODE) {
			switch (resultCode) {

			case RESULT_OK:
				Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
				buildDrawer();

				Builder boundsBuilder = new LatLngBounds.Builder();
				buildMapElements(boundsBuilder);
				centerMap(boundsBuilder);
				break;

			case RESULT_CANCELED:
				Toast.makeText(this, "Canceled !", Toast.LENGTH_LONG).show();
				break;
			case TravelItemFormActivity.RESULT_FAIL:
				Log.e(LOGTAG, "result fail");
				// unused for now
				break;
			case NotebookFormActivity.RESULT_SQL_FAIL:
				Log.e(LOGTAG, "Sql creation fail");
				Toast.makeText(getApplicationContext(), "Creation failed !", Toast.LENGTH_SHORT).show();
				break;
			}
		}
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

		for (final TravelItem item : currentNotebook.getTravelItems()) {

			itemMenuElement = new MenuElement(item.getTitle(), new OnClickListener() {

				@Override
				public void onClick(View v) {
					createTravelItemMarkerClickDialog(item.getId()).show();

					NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
				}

			});
			drawerListViewItems.add(itemMenuElement);
		}

		// add sepereator
		for (final Post item : currentNotebook.getPosts()) {

			itemMenuElement = new MenuElement(item.getTitle(), new OnClickListener() {

				@Override
				public void onClick(View v) {
					createPostMarkerClickDialog(item.getId()).show();

					NotebookActivity.this.drawerLayout.closeDrawer(drawerPanel);
				}
			});
			drawerListViewItems.add(itemMenuElement);
		}

		// gets ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.right_drawer_list);

		// Sets the adapter for the list view
		drawerListView.setAdapter(new MenuElementArrayAdapter(this, drawerListViewItems));

	}

	private void initButtons() {

		// Get the planning button
		Button btnGetPlanning = (Button) findViewById(R.id.btn_get_planning);
		btnGetPlanning.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startPlanningActivity();

				drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Gets the add planning item button
		Button btnAddPostItem = (Button) findViewById(R.id.btn_add_post_item);
		btnAddPostItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewPostItem();

				drawerLayout.closeDrawer(drawerPanel);
			}
		});

		// Gets the add travel item button
		Button btnAddTravelItem = (Button) findViewById(R.id.btn_add_travel_item);
		btnAddTravelItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewTravelItemActivity();

				drawerLayout.closeDrawer(drawerPanel);
			}
		});
	}

	private void initTitle() {
		String title = currentNotebook.getTitle();
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

		// clear the to begin at 0
		final Builder boundsBuilder = new LatLngBounds.Builder();

		postIcon = BitmapDescriptorFactory.fromResource(R.drawable.post);

		// initialize map events
		final View view = this.notebookMapView.getView();
		if (view.getViewTreeObserver().isAlive()) {
			view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {

					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					centerMap(boundsBuilder);
				}
			});
		}

		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				// unselect a marker (if there is one selected)
				unselectMarker();

				// select the new marker
				selectMarker(marker);

				return false;
			}
		});

		googleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				unselectMarker();
			}
		});

		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				if (travelItemMarkers.containsKey(marker)) {
					TravelItem travelItem = travelItemMarkers.get(marker);
					createTravelItemMarkerClickDialog(travelItem.getId()).show();
				}

				if (postMarkers.containsKey(marker)) {
					Post post = postMarkers.get(marker);
					createPostMarkerClickDialog(post.getId()).show();
				}

			}
		});

		googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng latLng) {
				createPopupDialog().show();
			}
		});

		buildMapElements(boundsBuilder);

	}

	private void buildMapElements(Builder boundsBuilder) {
		// Clear the markers list, will be reinitialized directly after.
		travelItemMarkers.clear();
		postMarkers.clear();
		polygons.clear();

		googleMap.clear();

		for (TravelItem travelItem : currentNotebook.getTravelItems()) {
			displayTraveItemOnMap(travelItem, boundsBuilder);
		}

		for (Post post : currentNotebook.getPosts()) {
			displayPostOnMap(post);
		}
	}

	private void displayPostOnMap(Post post) {
		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.title(post.getTitle());
		LatLng position = post.getLocationPosition(geocoder);
		markerOptions.position(position).icon(postIcon);

		// append marker to the google map
		Marker marker = googleMap.addMarker(markerOptions);
		postMarkers.put(marker, post);
	}

	private void centerMap(Builder boundsBuilder) {
		LatLngBounds latLngBounds = null;
		try {
			latLngBounds = boundsBuilder.build();
		}
		catch (IllegalStateException e) {
			Log.i(LOGTAG, "No LatLng in boundsBuilder: will not center the map");
		}

		if (latLngBounds != null)
			googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, MAP_BOUNDS_MARGIN));
	}

	private void displayTraveItemOnMap(TravelItem travelItem, Builder boundsBuilder) {
		MarkerOptions markerOptions = new MarkerOptions();

		Tag tag = travelItem.getTag();

		// Append icon of the tag to the marker
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(TagType.getIconRessource(tag.getTagType()));
		String title = travelItem.getTitle();

		markerOptions.icon(icon).title(title);

		// Set marker positions if the only one position is available
		if (travelItem.isSingleLocation()) {
			LatLng startPosition = travelItem.getStartLocationPosition(geocoder);

			boundsBuilder.include(startPosition);

			markerOptions.position(startPosition);

			// append marker to the google map
			Marker marker = googleMap.addMarker(markerOptions);

			// Append markers to the marker map
			travelItemMarkers.put(marker, travelItem);
		}
		else {
			// Set the two markers and trace the line between the the markers.
			LatLng startPosition = travelItem.getStartLocationPosition(geocoder);
			LatLng endPosition = travelItem.getEndLocationPosition(geocoder);

			boundsBuilder.include(startPosition);
			boundsBuilder.include(endPosition);

			// append marker only on start position
			markerOptions.position(startPosition);

			// append line between end and start position
			int color = Utilities.createTransparancyColor(this.currentNotebook.getColor(), NOTEBOOK_ITEM_LINE_TRANSPARENCY);
			PolygonOptions polygonOptions = new PolygonOptions();
			polygonOptions.add(startPosition).add(endPosition).strokeColor(color).geodesic(true);

			// append marker and polygon to the google map.
			Marker marker = googleMap.addMarker(markerOptions);
			Polygon polygon = googleMap.addPolygon(polygonOptions);

			// Append markers to the marker map and same for the polygon
			travelItemMarkers.put(marker, travelItem);
			polygons.put(travelItem, polygon);
		}
	}

	private Dialog createPopupDialog() {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.notebookactivity_popupactions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					startNewPostItem();
					break;
				case 1:
					startNewTravelItemActivity();
					break;
				default:
					Log.i(LOGTAG, "No action found for item: " + which);
				}
			}
		});
		builder.setTitle(R.string.title_actiondialog);
		return builder.create();
	}

	private Dialog createTravelItemMarkerClickDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.item_actions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					startShowTravelItem(id);
					break;
				case 1:
					startEditTravelItemActivity(id);
					break;
				case 2:
					deleteTravelItem(id);
					break;
				default:
					Toast.makeText(getApplicationContext(), "action not implemented", Toast.LENGTH_SHORT).show();
					break;
				}
			}

		});
		builder.setTitle(R.string.title_item_actions_popup);
		return builder.create();
	}

	private Dialog createPostMarkerClickDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.item_actions, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					startShowPostItem(id);
					break;
				case 1:
					startEditPostActivity(id);
					break;
				case 2:
					deletePost(id);
					break;
				default:
					Toast.makeText(getApplicationContext(), "action not implemented", Toast.LENGTH_SHORT).show();
					break;
				}
			}

		});
		builder.setTitle(R.string.title_item_actions_popup);
		return builder.create();
	}

	private void selectMarker(Marker marker) {
		// save the lastClicked marker (necessary for the unselection)
		lastClickedMarker = marker;

		TravelItem travelItem = travelItemMarkers.get(marker);
		if (travelItem != null) {
			Polygon polygon = polygons.get(travelItem);
			if (polygon != null) {
				int color = Utilities.createTransparancyColor(currentNotebook.getColor(), NOTEBOOK_ITEM_LINE_TRANSPARENCY_SELECTED);
				polygon.setStrokeColor(color);
			}
		}
	}

	private void unselectMarker() {
		TravelItem travelItem = travelItemMarkers.get(lastClickedMarker);
		if (travelItem != null) {
			Polygon polygon = polygons.get(travelItem);
			if (polygon != null) {
				int color = Utilities.createTransparancyColor(currentNotebook.getColor(), NOTEBOOK_ITEM_LINE_TRANSPARENCY);
				polygon.setStrokeColor(color);
			}
		}
		lastClickedMarker = null;
	}

	private DatabaseHelper getDBHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return databaseHelper;
	}

	private void startEditTravelItemActivity(int id) {
		Intent intent = new Intent(NotebookActivity.this, TravelItemFormActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		intent.putExtra(TravelItemFormActivity.TRAVELITEM_ID_KEY, id);

		startActivityForResult(intent, EDIT_TRAVELITEM_CODE);
	}

	private void startEditPostActivity(int id) {
		Intent intent = new Intent(NotebookActivity.this, PostItemFormActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		intent.putExtra(PostItemFormActivity.POST_ID_KEY, id);

		startActivityForResult(intent, EDIT_POST_CODE);
	}

	private void startPlanningActivity() {
		// Intent intent = new Intent(NotebookActivity.this,
		// PlanningActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// startActivity(intent);

		Toast.makeText(getApplicationContext(), "Planning", Toast.LENGTH_SHORT).show();
	}

	private void startNewPostItem() {
		Intent intent = new Intent(NotebookActivity.this, PostItemFormActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(PostItemFormActivity.NOOTEBOOK_ID_KEY, currentNotebook.getId());
		startActivityForResult(intent, NEW_POST_CODE);
	}

	private void startNewTravelItemActivity() {
		Intent intent = new Intent(NotebookActivity.this, TravelItemFormActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(TravelItemFormActivity.NOTEBOOK_ID_KEY, currentNotebook.getId());
		startActivityForResult(intent, NEW_TRAVELITEM_CODE);
	}

	private void startShowPostItem(int id) {
		Intent intent = new Intent(NotebookActivity.this, PostShowActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		intent.putExtra(PostShowActivity.POST_ID_KEY, id);
		startActivityForResult(intent, SHOW_POST_CODE);
	}

	private void startShowTravelItem(int id) {
		Intent intent = new Intent(NotebookActivity.this, TravelItemShowActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		intent.putExtra(TravelItemShowActivity.TRAVEL_ITEM_ID_KEY, id);
		startActivityForResult(intent, SHOW_TRAVEL_ITEM_CODE);
	}

	private void deletePost(int id) {
		try {
			databaseHelper.getPostDao().deleteById(id);
		}
		catch (SQLException e) {
			Toast.makeText(getApplicationContext(), "Notebook deletion error. Sorry!", Toast.LENGTH_LONG).show();
		}

		Builder boundsBuilder = new LatLngBounds.Builder();
		buildDrawer();
		buildMapElements(boundsBuilder);
	}

	private void deleteTravelItem(int id) {
		try {
			databaseHelper.getTravelItemDao().deleteById(id);
		}
		catch (SQLException e) {
			Toast.makeText(getApplicationContext(), "Notebook deletion error. Sorry!", Toast.LENGTH_LONG).show();
		}

		Builder boundsBuilder = new LatLngBounds.Builder();
		buildDrawer();
		buildMapElements(boundsBuilder);
	}
}
