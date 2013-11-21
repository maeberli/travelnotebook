package ch.hearc.devmobile.travelnotebook.database;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;

public class TagType {

	/********************
	 * Static
	 ********************/
	private static final String LOGTAG = TagType.class.getSimpleName();

	private static final TagType[] tagTypes = new TagType[] {
			new TagType("Hotel", false), new TagType("Bus", false),
			new TagType("Plane", true), new TagType("Boat", false),
			new TagType("Tent", false), new TagType("Car", false),
			new TagType("Taxi", false), new TagType("Motorhome", false) };

	private static TagType[] dbTagTypes = null;

	public static TagType[] getTagTypes(Context context) {
		if (dbTagTypes == null) {

			try {
				Dao<TagType, Integer> dao = new DatabaseHelper(context)
						.getTagTypeDao();

				List<TagType> existant = queryExistant(dao);

				if (existant != null && existant.size() != 0) {
					dbTagTypes = new TagType[existant.size()];

					int i = 0;
					for (TagType tagType : existant) {
						dbTagTypes[i++] = tagType;
					}
				} else {
					for (TagType tagType : tagTypes) {
						dao.create(tagType);
					}
					dbTagTypes = tagTypes;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Log.e(LOGTAG, "TagType creation error: " + e.getMessage());
			}
		}

		return dbTagTypes;
	}

	private static List<TagType> queryExistant(Dao<TagType, Integer> dao)
			throws SQLException {
		List<TagType> existant = null;

		existant = dao.queryForAll();

		return existant;
	}

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
