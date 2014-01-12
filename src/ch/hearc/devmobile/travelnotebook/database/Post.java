package ch.hearc.devmobile.travelnotebook.database;

import java.util.Date;

import android.location.Geocoder;

import ch.hearc.devmobile.travelnotebook.Utilities;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Post {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = Post.class.getSimpleName();
	private static final int MAXGEOCODERRESULT = 1;

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
	private Date date;

	@DatabaseField
	private String location;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Voyage voyage;

	@ForeignCollectionField(eager = true)
	private ForeignCollection<Image> images;

	/********************
	 * Constructors
	 ********************/
	public Post() {
		// Needed by ormlite
		this("", "", new Date(), "", null);
	}

	public Post(String title, String description, Date date, String location, Voyage voyage) {
		this.id = 0;
		this.title = title;
		this.description = description;
		this.date = date;
		this.location = location;
		this.voyage = voyage;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ForeignCollection<Image> getImages() {
		return images;
	}

	public void setImages(ForeignCollection<Image> images) {
		this.images = images;
	}

	public void setVoyage(Voyage voyage) {
		this.voyage = voyage;
	}

	public LatLng getLocationPosition(Geocoder geocoder) {
		return Utilities.getLocation(geocoder, location, MAXGEOCODERRESULT, LOGTAG);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Post [id=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", Date=");
		builder.append(date);
		builder.append(", location=");
		builder.append(location);
		builder.append("]");
		return builder.toString();
	}
}
