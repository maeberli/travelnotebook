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
			Dao<Notebook, Integer> notebookDao = databaseHelper.getNotebookDao();

			for (Notebook notebook : notebookDao.queryForAll()) {
				notebookDao.delete(notebook);
			}
		}
		catch (SQLException e) {
			Log.e(LOGTAG, "Notebook creation error:" + e.getMessage());
			e.printStackTrace();
		}

		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.HOUR, 1);

		Notebook notebook = new Notebook("Weekend trip London", Color.BLUE);

		List<TravelItem> travelItems = new ArrayList<TravelItem>();

		travelItems.add(new TravelItem("ZHR-London", "The flight with the numer xy", startDate.getTime(), endDate.getTime(), "Zurich, Airport",
				"London, Heathrow", notebook, new Tag(TagType.PLANE)));

		travelItems.add(new TravelItem("Bus Transfer to the hotel", "The bus leafes heathrow in terminal 2", startDate.getTime(), endDate.getTime(),
				"London,Heathrow airport", "Hilton Hotel, London", notebook, new Tag(TagType.BUS)));

		travelItems.add(new TravelItem("Senior Suite in Hilton Hotel", "Let's enjoy the welness area", startDate.getTime(), endDate.getTime(),
				"Hilton Hotel, London", notebook, new Tag(TagType.HOTEL)));

		travelItems.add(new TravelItem("Chinesse food", "Yea in london i usually eat chinesse food.", startDate.getTime(), "DownTown London", notebook, new Tag(
				TagType.FOOD)));

		travelItems.add(new TravelItem("Taxi Transfer Hotel-> Airport", "As a typicall tourist i wan't to take a classic london cab to return to the airport",
				startDate.getTime(), endDate.getTime(), "Hilton Hotel, London", "Heathrow Airport, London", notebook, new Tag(TagType.TAXI)));

		travelItems.add(new TravelItem("Heathrow->Hamburg", "Please no kids in avion", startDate.getTime(), endDate.getTime(), "Heathrow Airport, London",
				"Hamburg Airport", notebook, new Tag(TagType.PLANE)));

		travelItems.add(new TravelItem("Hamburg->ZRH", "Please no kids in avion", startDate.getTime(), endDate.getTime(), "Hamburg Airport", "Zurich, Airport",
				notebook, new Tag(TagType.PLANE)));

		try {
			for (TravelItem travelItem : travelItems) {
				databaseHelper.getTravelItemDao().create(travelItem);
			}
		}
		catch (SQLException e) {
			Log.e(LOGTAG, "Notebook creation error:" + e.getMessage());
			e.printStackTrace();
		}
	}
}