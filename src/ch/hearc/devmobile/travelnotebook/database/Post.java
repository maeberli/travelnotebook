package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Post {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = Post.class.getSimpleName();

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
	private String location;

	@ForeignCollectionField
	private ForeignCollection<Image> images;

	/********************
	 * Constructors
	 ********************/
	public Post() {
		// Needed by ormlite
		this("", "", "");
	}

	public Post(String title, String description, String location) {
		this.id = 0;
		this.title = title;
		this.description = description;
		this.location = location;
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

	public ForeignCollection<Image> getImages() {
		return images;
	}

	public void setImages(ForeignCollection<Image> images) {
		this.images = images;
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
		builder.append(", location=");
		builder.append(location);
		builder.append("]");
		return builder.toString();
	}

}
