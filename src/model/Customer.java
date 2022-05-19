package model;
/**
 * Customer object class. Used to store local entries of customers in database for filtering.
 *
 * @author James Watson - 001520415
 */
public class Customer {
	private final String id, name, address, postal, phone, division;
	/**
	 * Customer class default constructor method.
	 *
	 * @param id - customer id
	 * @param name - customer name
	 * @param address - customer address
	 * @param postal - customer postal code
	 * @param phone - customer phone number
	 * @param division - customer division id
	 */
	public Customer(String id, String name, String address, String postal, String phone, String division) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.postal = postal;
		this.phone = phone;
		this.division = division;
	}
	/** @return - returns customer id */
	public String getId() {
		return this.id;
	}
	/** @return - returns customer name */
	public String getName() {
		return this.name;
	}
	/** @return - returns customer address */
	public String getAddress() {
		return this.address;
	}
	/** @return - returns customer postal code */
	public String getPostal() {
		return this.postal;
	}
	/** @return - returns customer phone number */
	public String getPhone() {
		return this.phone;
	}
	/** @return - returns customer division id */
	public String getDivision() {
		return this.division;
	}
}
