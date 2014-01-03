package ch.hearc.devmobile.travelnotebook.database;

import com.j256.ormlite.field.DatabaseField;

public class FlightTagExtendet {

	/********************
	 * Static
	 ********************/
	@SuppressWarnings("unused")
	private static final String LOGTAG = FlightTagExtendet.class.getSimpleName();

	/********************
	 * Private members
	 ********************/
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String flightNumber;

	@DatabaseField
	private String airline;

	/********************
	 * Constructors
	 ********************/
	public FlightTagExtendet() {
		this("", "");
	}

	public FlightTagExtendet(String flightNumber, String airline) {
		this.id = 0;
		this.flightNumber = flightNumber;
		this.airline = airline;
	}

	/********************
	 * Public methods
	 ********************/
	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlightTagExtendet [id=");
		builder.append(id);
		builder.append(", flightNumber=");
		builder.append(flightNumber);
		builder.append(", airline=");
		builder.append(airline);
		builder.append("]");
		return builder.toString();
	}

}
