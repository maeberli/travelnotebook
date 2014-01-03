package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.field.DatabaseField;

public class Image {

	/********************
	 * Static
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = Image.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String imageURI;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true)
	private Post post;

	/********************
	 * Constructors
	 ********************/
	public Image() {
		this("", null);
	}

	public Image(String imageURI, Post post) {
		this.id = 0;
		this.imageURI = imageURI;
		this.post = post;
	}

	/********************
	 * Public methods
	 ********************/
	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Image [id=");
		builder.append(id);
		builder.append(", imageURI=");
		builder.append(imageURI);
		builder.append(", post=");
		builder.append(post);
		builder.append("]");
		return builder.toString();
	}

}
