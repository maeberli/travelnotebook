package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Voyage {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = Voyage.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(index = true)
	private String title;

	@DatabaseField
	private int color;

	@ForeignCollectionField
	private ForeignCollection<TravelItem> travelItems;

	/********************
	 * Constructors
	 ********************/
	public Voyage() {
		// Needed by ormlite
		this("", 0);
	}

	public Voyage(String title, int color) {
		this.id = 0;
		this.title = title;
		this.color = color;
	}

	/********************
	 * Public methods
	 ********************/
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public ForeignCollection<TravelItem> getTravelItems() {
		return travelItems;
	}

	public void setTravelItems(ForeignCollection<TravelItem> travelItems) {
		this.travelItems = travelItems;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Voyage [id=");
		builder.append(id);
		builder.append(", title=");
		builder.append(title);
		builder.append(", color=");
		builder.append(color);
		builder.append(", travelItems=");
		builder.append(travelItems);
		builder.append("]");
		return builder.toString();
	}
}