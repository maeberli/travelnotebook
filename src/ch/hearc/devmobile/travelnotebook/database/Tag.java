package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.field.DatabaseField;

public class Tag {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = Post.class.getName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true)
	private TravelItem travelItem;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true)
	private TagType tagType;

	@DatabaseField(canBeNull = true, foreign = true, foreignAutoCreate = true)
	private FlightTagExtendet flightTagExtendet;

	/********************
	 * Constructors
	 ********************/
	public Tag() {
		// Needed by ormlite
		this(null, null);
	}

	public Tag(TravelItem travelItem, TagType tagType) {
		this.id = 0;
		this.travelItem = travelItem;
		this.tagType = tagType;
	}

	/********************
	 * Public methods
	 ********************/
	public TravelItem getTravelItem() {
		return travelItem;
	}

	public void setTravelItem(TravelItem travelItem) {
		this.travelItem = travelItem;
	}

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	public FlightTagExtendet getFlightTagExtendet() {
		return flightTagExtendet;
	}

	public void setFlightTagExtendet(FlightTagExtendet flightTagExtendet) {
		this.flightTagExtendet = flightTagExtendet;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tag [id=");
		builder.append(id);
		builder.append(", travelItem=");
		builder.append(travelItem);
		builder.append(", tagType=");
		builder.append(tagType);
		builder.append(", flightTagExtendet=");
		builder.append(flightTagExtendet);
		builder.append("]");
		return builder.toString();
	}
}
