package ch.hearc.devmobile.travelnotebook.database;

import ch.hearc.devmobile.travelnotebook.R;

public enum TagType {
	HOTEL, BUS, PLANE, BOAT, TENT, CAR, TAXI, MOTORHOME, FOOD, UNSPECIFIED;

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
		case UNSPECIFIED:
			return R.drawable.unspecified;
		default:
			break;

		}
		return -1;
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
