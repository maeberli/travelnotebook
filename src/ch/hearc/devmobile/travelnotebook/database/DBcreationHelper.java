package ch.hearc.devmobile.travelnotebook.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class DBcreationHelper {

	private static final String LOGTAG = DBcreationHelper.class.getSimpleName();

	public static void createDBEntries(DatabaseHelper databaseHelper) {
		Log.i(LOGTAG, "Create example db entries");

		try {
			Dao<Voyage, Integer> voyageDao = databaseHelper.getVoyageDao();

			for (Voyage voyage : voyageDao.queryForAll()) {
				voyageDao.delete(voyage);
			}
		}
		catch (SQLException e) {
			Log.e(LOGTAG, "Voyage creation error:" + e.getMessage());
			e.printStackTrace();
		}

		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.HOUR, 1);

		Voyage voyage = new Voyage("Weekend trip London", Color.BLUE);

		List<PlanningItem> travelItems = new ArrayList<PlanningItem>();

		travelItems.add(new PlanningItem("ZHR-London", "The flight with the numer xy", startDate.getTime(), endDate.getTime(), "Zurich, Airport",
				"London, Heathrow", voyage, new Tag(TagType.PLANE)));

		travelItems.add(new PlanningItem("Bus Transfer to the hotel", "The bus leafes heathrow in terminal 2", startDate.getTime(), endDate.getTime(),
				"London,Heathrow airport", "Hilton Hotel, London", voyage, new Tag(TagType.BUS)));

		travelItems.add(new PlanningItem("Senior Suite in Hilton Hotel", "Let's enjoy the welness area", startDate.getTime(), endDate.getTime(),
				"Hilton Hotel, London", voyage, new Tag(TagType.HOTEL)));

		travelItems.add(new PlanningItem("Chinesse food", "Yea in london i usually eat chinesse food.", startDate.getTime(), "DownTown London", voyage, new Tag(
				TagType.FOOD)));

		travelItems.add(new PlanningItem("Taxi Transfer Hotel-> Airport", "As a typicall tourist i wan't to take a classic london cab to return to the airport",
				startDate.getTime(), endDate.getTime(), "Hilton Hotel, London", "Heathrow Airport, London", voyage, new Tag(TagType.TAXI)));

		travelItems.add(new PlanningItem("Heathrow->Hamburg", "Please no kids in avion", startDate.getTime(), endDate.getTime(), "Heathrow Airport, London",
				"Hamburg Airport", voyage, new Tag(TagType.PLANE)));

		travelItems.add(new PlanningItem("Hamburg->ZRH", "Please no kids in avion", startDate.getTime(), endDate.getTime(), "Hamburg Airport", "Zurich, Airport",
				voyage, new Tag(TagType.PLANE)));

		try {
			for (PlanningItem travelItem : travelItems) {
				databaseHelper.getTravelItemDao().create(travelItem);
			}
		}
		catch (SQLException e) {
			Log.e(LOGTAG, "Voyage creation error:" + e.getMessage());
			e.printStackTrace();
		}
	}
}