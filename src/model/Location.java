package model;
/**
 * Location object class. Used to store local entries of locations in database for filtering.
 *
 * @author James Watson - 001520415
 */
public class Location {
	/** location name */
	private final String division;
	/** location id */
	private final String divisionId;
	/** location country */
	private final String country;
	/** location country id */
	private final String countryId;
	/**
	 * Location class default constructor method.
	 *
	 * @param division - location name
	 * @param divisionId - location id
	 * @param country - location country
	 * @param countryId - location country id
	 */
	public Location(String division, String divisionId, String country, String countryId) {
		this.division = division;
		this.divisionId = divisionId;
		this.country = country;
		this.countryId = countryId;
	}
	/** @return location name */
	public String getDivision() {
		return this.division;
	}
	/** @return location id */
	public String getDivisionId() {
		return this.divisionId;
	}
	/** @return country name */
	public String getCountry() {
		return this.country;
	}
	/** @return country id */
	public String getCountryId() {
		return this.countryId;
	}
}
