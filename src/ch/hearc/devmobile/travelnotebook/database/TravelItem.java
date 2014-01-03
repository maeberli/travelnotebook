package ch.hearc.devmobile.travelnotebook.database;

import java.util.Date;

import ch.hearc.devmobile.travelnotebook.R;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

public class TravelItem extends Item{
	
	/********************
	 * Static
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = PlanningItem.class.getSimpleName();
	
	/********************
	 * Private members
	 ********************/
	@ForeignCollectionField
	private ForeignCollection<Image> images;
	
	/********************
	 * Constructors
	 ********************/
	public TravelItem() {
		// Needed by ormlite
		this("", "", new Date(), "", null, null);
	}

	public TravelItem(String title, String description, Date startDate, String startLocation, Voyage voyage, Tag tag) {
		this.id = 0;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.startLocation = startLocation;
		this.voyage = voyage;
		this.tag = tag;
	}
	
	/********************
	 * Public methods
	 ********************/
	public ForeignCollection<Image> getImages() {
		return images;
	}

	public void setImages(ForeignCollection<Image> images) {
		this.images = images;
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
		builder.append(", startLocation=");
		builder.append(startLocation);
		builder.append(", voyage=");
		builder.append(voyage);
		builder.append(", tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int getIcon() {
		return R.drawable.boat;
	}
}
