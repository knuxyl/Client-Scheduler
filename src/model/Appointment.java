package model;
import java.time.LocalDateTime;
/**
 * Appointment object class. Used to store local entries of appointments in database for filtering.
 *
 * @author James Watson - 001520415
 */
public class Appointment {
	/** appointment attributes */
	private final String id, title, description, location, contact, type, customer, user;
	/** appointment attributes */
	private final LocalDateTime start, end;
	/**
	 * Appointment class default constructor method.
	 *
	 * @param id - appointment id
	 * @param title - appointment title
	 * @param description - appointment description
	 * @param location - appointment location
	 * @param contact - appointment contact
	 * @param type - appointment type
	 * @param start - appointment start date and time
	 * @param end - appointment end date and time
	 * @param customer - appointment customer name
	 * @param user - appointment user
	 */
	public Appointment(String id, String title, String description, String location, String contact, String type,
					   LocalDateTime start, LocalDateTime end, String customer, String user) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.location = location;
		this.contact = contact;
		this.type = type;
		this.start = start;
		this.end = end;
		this.customer = customer;
		this.user = user;
	}
	/** @return - returns appointment id */
	public String getId() {
		return this.id;
	}
	/** @return - returns appointment title */
	public String getTitle() {
		return this.title;
	}
	/** @return - returns appointment description */
	public String getDescription() {
		return this.description;
	}
	/** @return - returns appointment location */
	public String getLocation() {
		return this.location;
	}
	/** @return - returns appointment contact */
	public String getContact() {
		return this.contact;
	}
	/** @return - returns appointment type */
	public String getType() {
		return this.type;
	}
	/** @return - returns appointment start date and time */
	public LocalDateTime getStart() {
		return this.start;
	}
	/** @return - returns appointment end date and time */
	public LocalDateTime getEnd() {
		return this.end;
	}
	/** @return - returns appointment customer */
	public String getCustomer() {
		return this.customer;
	}
	/** @return - returns appointment user */
	public String getUser() {
		return this.user;
	}
}
