package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.field.DatabaseField;

public class TagType {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = Post.class.getName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String description;

	@DatabaseField
	private boolean extendetType;

	/********************
	 * Constructors
	 ********************/
	public TagType() {
		// Needed by ormlite
		this("", false);
	}

	public TagType(String description, boolean extendetType) {
		this.id = 0;
		this.description = description;
		this.extendetType = extendetType;
	}

	/********************
	 * Public methods
	 ********************/
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isExtendetType() {
		return extendetType;
	}

	public void setExtendetType(boolean extendetType) {
		this.extendetType = extendetType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagType [id=");
		builder.append(id);
		builder.append(", description=");
		builder.append(description);
		builder.append(", extendetType=");
		builder.append(extendetType);
		builder.append("]");
		return builder.toString();
	}

}
