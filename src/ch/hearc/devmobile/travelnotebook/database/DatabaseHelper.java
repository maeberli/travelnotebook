package ch.hearc.devmobile.travelnotebook.database;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String LOGTAG = Post.class.getName();

	private static final String DATABASE_NAME = "travelnotebook.db";
	private static final int DATABASE_VERSION = 3;
	private static final Class<?> TABLELIST[] = { Post.class, Image.class,
			FlightTagExtendet.class, Tag.class, TagType.class,
			TravelItem.class, Voyage.class };

	private Dao<Post, Integer> postDao = null;
	private Dao<Image, Integer> imageDao = null;
	private Dao<FlightTagExtendet, Integer> flightTagExtendetDao = null;
	private Dao<Tag, Integer> tagDao = null;
	private Dao<TagType, Integer> tagTypeDao = null;
	private Dao<TravelItem, Integer> travelItemDao = null;
	private Dao<Voyage, Integer> voyageDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(LOGTAG, "onCreate");
			for (Class<?> cl : TABLELIST) {
				TableUtils.createTable(connectionSource, cl);
			}
		} catch (SQLException e) {
			Log.e(LOGTAG, "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(LOGTAG, "onUpgrade");

			for (Class<?> cl : TABLELIST) {
				TableUtils.dropTable(connectionSource, cl, true);
			}

			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<Post, Integer> getPostDao() throws SQLException {
		if (postDao == null) {
			postDao = getDao(Post.class);
		}
		return postDao;
	}

	public Dao<Image, Integer> getImageDao() throws SQLException {
		if (imageDao == null) {
			imageDao = getDao(Image.class);
		}
		return imageDao;
	}

	public Dao<FlightTagExtendet, Integer> getFlightTagExtendetDao()
			throws SQLException {
		if (flightTagExtendetDao == null) {
			flightTagExtendetDao = getDao(FlightTagExtendet.class);
		}
		return flightTagExtendetDao;
	}

	public Dao<Tag, Integer> getTagDao() throws SQLException {
		if (tagDao == null) {
			tagDao = getDao(Tag.class);
		}
		return tagDao;
	}

	public Dao<TagType, Integer> getTypeDao() throws SQLException {
		if (tagTypeDao == null) {
			tagTypeDao = getDao(TagType.class);
		}
		return tagTypeDao;
	}

	public Dao<TravelItem, Integer> getTravelItemDao() throws SQLException {
		if (travelItemDao == null) {
			travelItemDao = getDao(TravelItem.class);
		}
		return travelItemDao;
	}

	public Dao<Voyage, Integer> getVoyageDao() throws SQLException {
		if (voyageDao == null) {
			voyageDao = getDao(Voyage.class);
		}
		return voyageDao;
	}

	@Override
	public void close() {
		super.close();
		postDao = null;
	}
}
