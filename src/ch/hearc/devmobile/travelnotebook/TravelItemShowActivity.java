package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Tag;
import ch.hearc.devmobile.travelnotebook.database.TagType;
import ch.hearc.devmobile.travelnotebook.database.TravelItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class TravelItemShowActivity extends FragmentActivity {

	/********************
	 * Public Static constants
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = TravelItemShowActivity.class.getSimpleName();
	private static final int MAP_MARGIN = 50;
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	/********************
	 * Private Static constants
	 ********************/
	public static final String TRAVEL_ITEM_ID_KEY = "travelItemId";
	public static final int RESULT_FAILURE = 500;

	/********************
	 * Private members
	 ********************/
	private DatabaseHelper dbHelper;

	private TextView tvTitle;
	private TextView tvDescription;
	private TextView tvStartDate;
	private TextView tvEndDate;
	private ImageView ivDateTo;
	private ImageView ivDateLink;

	private TravelItem travelItem;
	private SupportMapFragment travelItemShowMapView;
	private GoogleMap googleMap;
	private Geocoder geocoder;
	private SimpleDateFormat dateFormatter;

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_item_show, menu);
		return true;
	}

	/********************
	 * Protected methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel_item_show);

		dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		geocoder = new Geocoder(this);
		dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

		tvTitle = (TextView) findViewById(R.id.tv_travel_item_title);
		tvDescription = (TextView) findViewById(R.id.tv_travel_item_show_description);
		tvStartDate = (TextView) findViewById(R.id.tv_travel_item_start_date);
		tvEndDate = (TextView) findViewById(R.id.tv_travel_item_end_date);
		ivDateTo = (ImageView) findViewById(R.id.iv_travel_item_date_to);
		ivDateLink = (ImageView) findViewById(R.id.iv_travel_item_date_link);

		travelItemShowMapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.travel_item_show_map);
		travelItemShowMapView.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent.hasExtra(TRAVEL_ITEM_ID_KEY)) {
			int id = intent.getIntExtra(TRAVEL_ITEM_ID_KEY, -1);
			if (id != -1) {
				try {
					travelItem = dbHelper.getTravelItemDao().queryForId(id);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (this.travelItem == null) {
			setResult(RESULT_FAILURE);
			finish();
		}
		else {
			initFields();
		}

		setUpMapIfNeeded();
	}

	private void initFields() {
		tvTitle.setText(travelItem.getTitle());
		tvDescription.setText(travelItem.getDescription());
		tvStartDate.setText(dateFormatter.format(travelItem.getStartDate()));

		if (!travelItem.isSingleLocation()) {
			tvEndDate.setText(dateFormatter.format(travelItem.getEndDate()));
		}
		else {
			ivDateTo.setVisibility(View.GONE);
			ivDateLink.setVisibility(View.GONE);
		}
	}

	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = travelItemShowMapView.getMap();

			if (googleMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {

		googleMap.clear();

		final Builder boundsBuilder = new LatLngBounds.Builder();

		Tag tag = travelItem.getTag();
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(TagType.getIconRessource(tag.getTagType()));

		LatLng startLatLng = travelItem.getStartLocationPosition(geocoder);
		googleMap.addMarker(new MarkerOptions().position(startLatLng).icon(icon));
		boundsBuilder.include(startLatLng);

		if (!travelItem.isSingleLocation()) {
			LatLng endLatLng = travelItem.getEndLocationPosition(geocoder);
			googleMap.addMarker(new MarkerOptions().position(endLatLng).icon(icon));
			boundsBuilder.include(endLatLng);

			int color = travelItem.getNotebook().getColor();
			PolygonOptions polygonOptions = new PolygonOptions();
			polygonOptions.add(startLatLng).add(endLatLng).strokeColor(color).geodesic(true);

			googleMap.addPolygon(polygonOptions);
		}

		if (this.travelItemShowMapView.getView().getViewTreeObserver().isAlive()) {
			travelItemShowMapView.getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					travelItemShowMapView.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

					googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), MAP_MARGIN));
				}
			});
		}

		googleMap.getUiSettings().setZoomControlsEnabled(false);
	}

}
