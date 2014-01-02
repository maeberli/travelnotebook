package ch.hearc.devmobile.travelnotebook.database;

import ch.hearc.devmobile.travelnotebook.R;

public enum TagType {
	HOTEL, BUS, PLANE, BOAT, TENT, CAR, TAXI, MOTORHOME, FOOD;

	public static int getIconRessource(TagType type) {
		switch (type) {
		case BOAT:
			return R.drawable.boat;
		case BUS:
			return R.drawable.bus;
		case CAR:
			return R.drawable.car;
		case HOTEL:
			return R.drawable.bed;
		case MOTORHOME:
			return R.drawable.motorhome;
		case PLANE:
			return R.drawable.aircraft;
		case TAXI:
			return R.drawable.car;
		case TENT:
			return R.drawable.tent;
		case FOOD:
			return R.drawable.food;
		default:
			break;

		}
		return -1;
	}

	public static String getDescription(TagType type) {
		switch (type) {
		case BOAT:
			return "Boat";
		case BUS:
			return "Bus";
		case CAR:
			return "Car";
		case HOTEL:
			return "Bed";
		case MOTORHOME:
			return "Motorhome";
		case PLANE:
			return "Plane";
		case TAXI:
			return "Taxi";
		case TENT:
			return "Tent";
		case FOOD:
			return "Food";
		default:
			break;
		}
		return "";
	}

	public static Boolean isExtendet(TagType type) {
		switch (type) {
		case BOAT:
		case BUS:
		case CAR:
		case HOTEL:
		case MOTORHOME:
		case TAXI:
		case TENT:
		case FOOD:
			return false;
		case PLANE:
			return true;
		}
		return false;
	}
}
