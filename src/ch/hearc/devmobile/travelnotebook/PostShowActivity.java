package ch.hearc.devmobile.travelnotebook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import ch.hearc.devmobile.travelnotebook.adapter.ImagePagerAdapter;
import ch.hearc.devmobile.travelnotebook.database.DatabaseHelper;
import ch.hearc.devmobile.travelnotebook.database.Image;
import ch.hearc.devmobile.travelnotebook.database.Post;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class PostShowActivity extends FragmentActivity {

	/********************
	 * Public Static constants
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = PostShowActivity.class.getSimpleName();
	private static final float MAP_BOUNDS_ZOOM = (float) 14.0;

	/********************
	 * Private Static constants
	 ********************/
	public static final String POST_ID_KEY = "postId";
	public static final int RESULT_FAILURE = 500;

	/********************
	 * Private members
	 ********************/
	private DatabaseHelper dbHelper;

	private ViewPager imagePager;
	private TextView tvTitle;
	private TextView tvDescription;

	private List<String> images;
	private ImagePagerAdapter imagePagerAdapter;
	private Post post;
	private SupportMapFragment postShowMapView;
	private GoogleMap googleMap;
	private Geocoder geocoder;

	/********************
	 * Public methods
	 ********************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_post, menu);
		return true;
	}

	/********************
	 * Protected methods
	 ********************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_show);

		dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		geocoder = new Geocoder(this);

		images = new ArrayList<String>();

		imagePager = (ViewPager) findViewById(R.id.imagepager_posthow);
		tvTitle = (TextView) findViewById(R.id.tv_postshow_title);
		tvDescription = (TextView) findViewById(R.id.tv_postshow_description);

		postShowMapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.post_show_map);
		postShowMapView.onCreate(savedInstanceState);

		imagePagerAdapter = new ImagePagerAdapter(getApplicationContext(), images);
		imagePager.setAdapter(imagePagerAdapter);

		Intent intent = getIntent();
		if (intent.hasExtra(POST_ID_KEY)) {
			int id = intent.getIntExtra(POST_ID_KEY, -1);
			if (id != -1) {
				try {
					post = dbHelper.getPostDao().queryForId(id);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (this.post == null) {
			setResult(RESULT_FAILURE);
			finish();
		}
		else {
			initFields();
		}

		setUpMapIfNeeded();
	}

	@Override
	protected void onPause() {
		super.onPause();
		postShowMapView.onPause();
	}

	/********************
	 * Private methods
	 ********************/
	private void initFields() {
		tvTitle.setText(post.getTitle());
		tvDescription.setText(post.getDescription());

		for (Image image : post.getImages()) {
			images.add(image.getImageURI());
		}
		imagePagerAdapter.notifyDataSetChanged();
	}

	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = postShowMapView.getMap();

			if (googleMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {

		googleMap.clear();

		final LatLng latLng = post.getLocationPosition(geocoder);
		googleMap.addMarker(new MarkerOptions().position(latLng));

		if (this.postShowMapView.getView().getViewTreeObserver().isAlive()) {
			postShowMapView.getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					postShowMapView.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_BOUNDS_ZOOM));
				}
			});
		}

		googleMap.getUiSettings().setZoomControlsEnabled(false);
	}

}
