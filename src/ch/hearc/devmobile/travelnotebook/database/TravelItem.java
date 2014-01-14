package ch.hearc.devmobile.travelnotebook.database;

import java.util.Date;

import android.location.Geocoder;
import ch.hearc.devmobile.travelnotebook.Utilities;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

public class TravelItem {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = TravelItem.class.getSimpleName();
	private static final int MAXGEOCODERRESULTS = 1;

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
	private Notebook notebook;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, unique = true)
	private Tag tag;

	/********************
	 * Constructors
	 ********************/
	public TravelItem() {
		// Needed by ormlite
		this("", "", new Date(), null, "", "", null, new Tag(TagType.UNSPECIFIED));
	}

	public TravelItem(String title, String description, Date startDate, Date endDate, String startLocation, String endLocation, Notebook notebook, Tag tag) {
		this.id = 0;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.notebook = notebook;
		this.tag = tag;

	}

	public TravelItem(String title, String description, Date startDate, Date endDate, String startLocation, Notebook notebook, Tag tag) {
		this(title, description, startDate, endDate, startLocation, null, notebook, tag);
	}

	public TravelItem(String title, String description, Date date, String location, Notebook notebook, Tag tag) {
		this(title, description, date, null, location, null, notebook, tag);
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

	public LatLng getStartLocationPosition(Geocoder geocoder) {
		return Utilities.getLocation(geocoder, startLocation, MAXGEOCODERRESULTS, LOGTAG);
	}

	public String getEndLocation() {
		return endLocation;
	}

	public LatLng getEndLocationPosition(Geocoder geocoder) {
		return Utilities.getLocation(geocoder, endLocation, MAXGEOCODERRESULTS, LOGTAG);
	}

	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
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
		builder.append(", notebook=");
		builder.append(notebook);
		builder.append(", tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}
}
