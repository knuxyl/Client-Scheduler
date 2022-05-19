package fxmlcontroller;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Customer;
import model.Location;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import database.Jdbc;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
/**
 * Updating a customer class. Implements variables and methods for updating a customer to the database.
 *
 * @author James Watson - 001520415
 */
public class controllerCustomerUpdate implements Initializable {
	/** gui buttons */
	public Button buttonCancel, buttonSave;
	/** gui labels */
	public Label labelId, labelName, labelPhone, labelAddress, labelPostal, labelCountry, labelDivision, labelError;
	/** gui text fields */
	public TextField textId, textName, textPhone, textAddress, textPostal;
	/** gui combo boxes for strings */
	public ComboBox<String> comboCountry, comboDivision;
	/** array to store text fields */
	public ArrayList<TextField> allText = new ArrayList<>();
	/** array to store labels associated with text fields */
	public ArrayList<Label> allLabel = new ArrayList<>();
	/** array to store location data */
	public ArrayList<Location> allLocation = new ArrayList<>();
	/**
	 * Initial setup for class. Sets formatting and adds data from the server that will be needed.
	 *
	 * @param url - javafx variable
	 * @param resourceBundle - javafx variable
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		Customer customer = controllerMain.customer;
		Collections.addAll(allLabel, labelId, labelName, labelPhone, labelAddress, labelPostal);
		Collections.addAll(allText, textId, textName, textPhone, textAddress, textPostal);
		try {
			ResultSet locationSet = Jdbc.queryDB("" +
				"SELECT first_level_divisions.Division, first_level_divisions.Division_ID, countries.Country, " +
				"countries.Country_ID " +
				"FROM first_level_divisions " +
				"INNER JOIN countries ON first_level_divisions.Country_ID=countries.Country_ID");
			ResultSet countrySet = Jdbc.queryDB("" +
				"SELECT *" +
				"FROM countries");
			ResultSet customerSet = Jdbc.queryDB("" +
				"SELECT * FROM customers " +
				"WHERE Customer_ID=\"" + customer.getId() + "\"");
			while (locationSet.next()) {
				String country = locationSet.getString("Country");
				String division = locationSet.getString("Division");
				String divisionId = locationSet.getString("Division_ID");
				String countryId = locationSet.getString("Country_ID");
				allLocation.add(new Location(division, divisionId, country, countryId));
			}
			while(countrySet.next()) {
				comboCountry.getItems().add(countrySet.getString("Country"));
			}
			customerSet.next();
			textId.setText(customerSet.getString("Customer_ID"));
			textName.setText(customerSet.getString("Customer_Name"));
			textPhone.setText(customerSet.getString("Phone"));
			textAddress.setText(customerSet.getString("Address"));
			textPostal.setText(customerSet.getString("Postal_Code"));
			String dbDivision = customerSet.getString("Division_ID");
			String country = "";
			String division = "";
			for (Location location : allLocation) {
				if (location.getDivisionId().equals(dbDivision)) {
					country = location.getCountry();
					division = location.getDivision();
				}
			}
			comboDivision.getItems().clear();
			comboCountry.getSelectionModel().select(country);
			comboDivision.getSelectionModel().select(division);
			for (Location location : allLocation) {
				if (location.getCountry().equals(comboCountry.getValue())) {
					comboDivision.getItems().add(location.getDivision());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		comboCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, newSelected) -> {
			comboDivision.getItems().clear();
			comboDivision.getSelectionModel().select("");
			for (Location location : allLocation) {
				if (location.getCountry().equals(comboCountry.getValue())) {
					comboDivision.getItems().add(location.getDivision());
				}
			}
		});
	}
	/**
	 * Closes window without updating customer. Called when cancel button is clicked.
	 */
	public void buttonCancel() {
		controllerMain.message = "Customer was not updated";
		controllerMain.messageColor = "white";
		((Stage)buttonCancel.getScene().getWindow()).close();
	}
	/**
	 * Saves the data to the database. Also updates the main window's tableview. Called when save button is clicked.
	 *
	 * @throws SQLException - throws if error communicating with database
	 */
	public void buttonSave() throws SQLException {
		if (inputValidated()) {
			String id = textId.getText();
			String name = textName.getText();
			String address = textAddress.getText();
			String postal = textPostal.getText();
			String phone = textPhone.getText();
			String divisionId = "";
			String divisionName = "";
			for (Location location : allLocation) {
				if (location.getDivision().equals(comboDivision.getValue())) {
					divisionName = location.getDivision();
					divisionId = location.getDivisionId();
					break;
				}
			}
			Timestamp timeUTC = Timestamp.valueOf(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).toLocalDateTime());
			Jdbc.updateDB("" +
				"UPDATE customers " +
				"SET Customer_Name = \"" + name + "\", " +
				"Address = \"" + address + "\", " +
				"Postal_Code = \"" + postal + "\", " +
				"Phone = \"" + phone + "\", " +
				"Last_Update = \"" + timeUTC + "\", " +
				"Last_Updated_By = \"" + controllerMain.user + "\", " +
				"Division_ID = \"" + divisionId + "\"" +
				"WHERE Customer_ID = \"" + id + "\"");
			controllerMain.allCustomers.set(controllerMain.customerIndex, new Customer(id, name, address, postal, phone, divisionName));
			controllerMain.message = "Customer updated successfully";
			controllerMain.messageColor = "green";
			((Stage)buttonSave.getScene().getWindow()).close();
		}
	}
	/**
	 * Validates input meets data requirements.
	 *
	 * @return - returns true if input meets requirements
	 */
	public boolean inputValidated() {
		resetForm();
		boolean error = false;
		for (int i = 0; i < allLabel.size(); i++) {
			if (allText.get(i).getText().equals("")) {
				allLabel.get(i).setStyle("-fx-text-fill: red");
				error = true;
			}
		}
		if (comboCountry.getValue() == null) {
			labelCountry.setStyle("-fx-text-fill: red");
			error = true;
		} else if (comboCountry.getValue().equals("")) {
			labelCountry.setStyle("-fx-text-fill: red");
			error = true;
		}
		if (comboDivision.getValue() == null) {
			labelDivision.setStyle("-fx-text-fill: red");
			error = true;
		} else if (comboDivision.getValue().equals("")) {
			labelDivision.setStyle("-fx-text-fill: red");
			error = true;
		}
		if (error) {
			labelError.setText("Field(s) cannot be empty");
			return false;
		}
		if (!textPhone.getText().matches("^[\\d()\\-+.]+$")) {
			labelPhone.setStyle("-fx-text-fill: red");
			labelError.setText("Field must contain a phone number");
			return false;
		}
		return true;
	}
	/**
	 * Resets formatting on gui elements.
	 */
	public void resetForm() {
		allLabel.forEach( label -> label.setStyle("-fx-text-fill: white"));
		labelCountry.setStyle("-fx-text-fill: white");
		labelDivision.setStyle("-fx-text-fill: white");
		labelError.setText("");
	}
}
