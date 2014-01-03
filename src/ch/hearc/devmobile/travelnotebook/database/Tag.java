package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.field.DatabaseField;

public class Tag {

	/********************
	 * Static
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = Tag.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(canBeNull = false)
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

	public Tag(FlightTagExtendet flightTagExtendet) {
		this(TagType.PLANE, flightTagExtendet);
	}

	public Tag(TagType tagType) {
		this(tagType, null);
	}

	private Tag(TagType tagType, FlightTagExtendet flightTagExtendet) {
		this.id = 0;
		this.tagType = tagType;
		this.flightTagExtendet = flightTagExtendet;
	}

	/********************
	 * Public methods
	 ********************/

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
		builder.append(", tagType=");
		builder.append(tagType);
		builder.append(", flightTagExtendet=");
		builder.append(flightTagExtendet);
		builder.append("]");
		return builder.toString();
	}
}
