package ch.hearc.devmobile.travelnotebook.database;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

public class TravelItem {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = TravelItem.class.getSimpleName();
	private static final int MAXGEOCODERRESULTS = 1;
	public static final String FIELD_VOYAGE = "voyage_id";

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(index = true)
	private String title;

	@DatabaseField
	private String description;

	@DatabaseField
	private Date startDate;

	@DatabaseField
	private Date endDate;

	@DatabaseField
	private String startLocation;

	@DatabaseField
	private String endLocation;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Voyage voyage;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, unique = true)
	private Tag tag;

	/********************
	 * Constructors
	 ********************/
	public TravelItem() {
		// Needed by ormlite
		this("", "", new Date(), new Date(), "", "", null, null);
	}

	public TravelItem(String title, String description, Date startDate, Date endDate, String startLocation, String endLocation, Voyage voyage, Tag tag) {
		this.id = 0;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.voyage = voyage;
		this.tag = tag;

	}

	public TravelItem(String title, String description, Date startDate, Date endDate, String startLocation, Voyage voyage, Tag tag) {
		this(title, description, startDate, endDate, startLocation, null, voyage, tag);
	}

	public TravelItem(String title, String description, Date date, String location, Voyage voyage, Tag tag) {
		this(title, description, date, null, location, null, voyage, tag);
	}

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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public LatLng getStartLocationPosition(Geocoder geocoder) throws IOException {
		return getLocation(geocoder, startLocation);
	}

	public String getEndLocation() {
		return endLocation;
	}

	public LatLng getEndLocationPosition(Geocoder geocoder) throws IOException {
		return getLocation(geocoder, endLocation);
	}

	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
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
		return (endDate == null);
	}

	public boolean isSingleLocation() {
		return (endLocation == null);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TravelItem [id=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", startLocation=");
		builder.append(startLocation);
		builder.append(", endLocation=");
		builder.append(endLocation);
		builder.append(", voyage=");
		builder.append(voyage);
		builder.append(", tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}

	/********************
	 * Private methods
	 ********************/
	private static LatLng getLocation(Geocoder geocoder, String name) throws IOException {
		List<Address> addresses = geocoder.getFromLocationName(name, MAXGEOCODERRESULTS);

		Log.d(LOGTAG, "Addresses found for " + name + ": " + addresses.size());

		if (addresses.size() > 0) {
			LatLng location = addressToLatLng(addresses.get(0));

			Log.d(LOGTAG, "Geocoded location of " + name + ": " + location);
			return location;
		}
		return null;
	}

	private static LatLng addressToLatLng(Address address) {
		return new LatLng(address.getLatitude(), address.getLongitude());
	}
}
