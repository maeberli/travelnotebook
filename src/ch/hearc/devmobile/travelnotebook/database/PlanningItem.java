package ch.hearc.devmobile.travelnotebook.database;

import java.util.Date;

import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

public class PlanningItem extends Item implements NotSingleLocation_I, NotSingleDate_I {

	/********************
	 * Static
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = PlanningItem.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField
	private Date endDate;

	@DatabaseField
	private String endLocation;

	/********************
	 * Constructors
	 ********************/
	public PlanningItem() {
		// Needed by ormlite
		this("", "", new Date(), new Date(), "", "", null, null);
	}

	public PlanningItem(String title, String description, Date startDate, Date endDate, String startLocation, String endLocation, Voyage voyage, Tag tag) {
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

	public PlanningItem(String title, String description, Date startDate, Date endDate, String startLocation, Voyage voyage, Tag tag) {
		this(title, description, startDate, endDate, startLocation, null, voyage, tag);
	}

	public PlanningItem(String title, String description, Date date, String location, Voyage voyage, Tag tag) {
		this(title, description, date, null, location, null, voyage, tag);
	}

	/********************
	 * Public methods
	 ********************/
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndLocation() {
		return endLocation;
	}

	public LatLng getEndLocationPosition(Geocoder geocoder) {
		return getLocation(geocoder, endLocation);
	}

	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
	}	

	@Override
	public boolean isSingleTimed() {
		return (endDate == null);
	}

	@Override
	public boolean isSingleLocation() {
		return (endLocation == null);
	}
	
	@Override
	public int getIcon() {
		return TagType.getIconRessource(tag.getTagType());
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
}
