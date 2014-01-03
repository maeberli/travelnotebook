package ch.hearc.devmobile.travelnotebook.database;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

public abstract class Item {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = PlanningItem.class.getSimpleName();
	private static final int MAXGEOCODERRESULTS = 1;
	public static final String FIELD_VOYAGE = "voyage_id";

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField(index = true)
	protected String title;

	@DatabaseField
	protected String description;

	@DatabaseField
	protected Date startDate;

	@DatabaseField
	protected String startLocation;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	protected Voyage voyage;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, unique = true)
	protected Tag tag;

	/********************
	 * Public methods
	 ********************/
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public LatLng getStartLocationPosition(Geocoder geocoder) {
		return getLocation(geocoder, startLocation);
	}

	public Voyage getVoyage() {
		return voyage;
	}

	public void setVoyage(Voyage voyage) {
		this.voyage = voyage;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public boolean isSingleTimed() {
		return false;
	}

	public boolean isSingleLocation() {
		return false;
	}

	public abstract int getIcon();

	/********************
	 * Protected methods
	 ********************/
	protected static LatLng getLocation(Geocoder geocoder, String name) {

		try {
			List<Address> addresses = geocoder.getFromLocationName(name, MAXGEOCODERRESULTS);
			Log.d(LOGTAG, "Addresses found for " + name + ": " + addresses.size());

			if (addresses.size() > 0) {
				LatLng location = addressToLatLng(addresses.get(0));

				Log.d(LOGTAG, "Geocoded location of " + name + ": " + location);
				return location;
			}
		}
		catch (IOException e) {
			Log.e(LOGTAG, "Adress <" + name + "> not found. Return lat: 0, long: 0");
		}
		return new LatLng(0, 0);
	}

	/********************
	 * Private methods
	 ********************/
	private static LatLng addressToLatLng(Address address) {
		return new LatLng(address.getLatitude(), address.getLongitude());
	}

}
