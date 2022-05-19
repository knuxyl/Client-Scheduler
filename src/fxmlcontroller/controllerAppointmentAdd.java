package fxmlcontroller;
import javafx.scene.control.*;
import model.Appointment;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import database.Jdbc;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
/**
 * Adding an appointment class. Implements variables and methods for adding an appointment to the database.
 *
 * @author James Watson - 001520415
 */
public class controllerAppointmentAdd implements Initializable {
	/** gui buttons */
	public Button buttonCancel, buttonSave;
	/** gui labels */
	public Label labelId, labelTitle, labelDescription, labelLocation, labelContact, labelType, labelStartDate,
		labelStartTime, labelEndDate, labelEndTime, labelCustomer, labelUser, labelError;
	/** gui text fields */
	public TextField textId, textTitle, textDescription, textLocation, textType;
	/** gui date pickers */
	public DatePicker pickStartDate, pickEndDate;
	/** gui combo boxes for strings */
	public ComboBox<String> comboCustomer, comboContact, comboUser;
	/** gui combo boxes for times */
	public ComboBox<LocalTime> comboStartTime, comboEndTime;
	/** array to store text fields */
	public ArrayList<TextField> allText = new ArrayList<>();
	/** array for storing labels associated with text fields */
	public ArrayList<Label> allLabel = new ArrayList<>();
	/**
	 * Initial setup for class. Sets formatting and adds data from the server that will be needed.
	 *
	 * @param url - javafx variable
	 * @param resourceBundle - javafx variable
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		resetForm();
		Collections.addAll(allText, textTitle, textDescription, textLocation, textType);
		Collections.addAll(allLabel, labelTitle, labelDescription, labelLocation, labelType);
		comboCustomer.getItems().clear();
		comboContact.getItems().clear();
		comboUser.getItems().clear();
		pickStartDate.setValue(null);
		pickEndDate.setValue(null);
		for (int i = 0; i < 96; i++) {
			int minutes = (i * 15) % 60;
			int hours = (i * 15) / 60;
			LocalTime tempTime = LocalTime.parse(String.format("%02d", hours) + ":" + String.format("%02d", minutes));
			comboStartTime.getItems().add(tempTime);
			comboEndTime.getItems().add(tempTime);
		}
		try {
			ResultSet contactSet = Jdbc.queryDB("" +
				"SELECT Contact_Name " +
				"FROM contacts ");
			ResultSet customerSet = Jdbc.queryDB("" +
				"SELECT Customer_Name " +
				"FROM customers");
			ResultSet userSet = Jdbc.queryDB("" +
				"SELECT User_Name " +
				"FROM users");
			ResultSet idSet = Jdbc.queryDB("" +
				"SELECT MAX(Appointment_ID) AS id " +
				"FROM appointments");
			while (contactSet.next()) {
				comboContact.getItems().add(contactSet.getString("Contact_Name"));
			}
			while (customerSet.next()) {
				comboCustomer.getItems().add(customerSet.getString("Customer_Name"));
			}
			while (userSet.next()) {
				comboUser.getItems().add(userSet.getString("User_Name"));
			}
			idSet.next();
			String id = Integer.toString(Integer.parseInt(idSet.getString("id")) + 1);
			textId.setText(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Closes window without adding appointment. Called when cancel button is clicked.
	 */
	public void buttonCancel() {
		controllerMain.message = "Appointment was not added";
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
			LocalDateTime dtStart = LocalDateTime.of(
				pickStartDate.getValue().getYear(),
				pickStartDate.getValue().getMonth(),
				pickStartDate.getValue().getDayOfMonth(),
				comboStartTime.getValue().getHour(),
				comboStartTime.getValue().getMinute());
			LocalDateTime dtEnd = LocalDateTime.of(
				pickEndDate.getValue().getYear(),
				pickEndDate.getValue().getMonth(),
				pickEndDate.getValue().getDayOfMonth(),
				comboEndTime.getValue().getHour(),
				comboEndTime.getValue().getMinute());
			ZoneId zoneUTC = ZoneId.of("UTC");
			ZonedDateTime localStart = ZonedDateTime.of(dtStart, ZoneId.systemDefault());
			ZonedDateTime localEnd = ZonedDateTime.of(dtEnd, ZoneId.systemDefault());
			ZonedDateTime utcStart = ZonedDateTime.ofInstant(localStart.toInstant(), zoneUTC);
			ZonedDateTime utcEnd = ZonedDateTime.ofInstant(localEnd.toInstant(), zoneUTC);
			LocalDateTime ldtNow = LocalDateTime.now();
			ZonedDateTime localNow = ZonedDateTime.of(ldtNow, ZoneId.systemDefault());
			ZonedDateTime utcNow = ZonedDateTime.ofInstant(localNow.toInstant(), zoneUTC);
			ResultSet customerSet = Jdbc.queryDB("" +
				"SELECT Customer_ID " +
				"FROM customers " +
				"WHERE Customer_Name=\"" + comboCustomer.getValue() + "\"");
			customerSet.next();
			String customerId = customerSet.getString("Customer_ID");
			ResultSet contactSet = Jdbc.queryDB("" +
				"SELECT Contact_ID " +
				"FROM contacts " +
				"WHERE Contact_Name=\"" + comboContact.getValue() + "\"");
			contactSet.next();
			String contactId = contactSet.getString("Contact_ID");
			ResultSet userSet = Jdbc.queryDB("" +
				"SELECT User_ID " +
				"FROM users " +
				"WHERE User_Name=\"" + comboUser.getValue() + "\"");
			userSet.next();
			String userId = userSet.getString("User_ID");
			String id = textId.getText();
			String title = textTitle.getText();
			String description = textDescription.getText();
			String location = textLocation.getText();
			String contact = comboContact.getValue();
			String type = textType.getText();
			String customer = comboCustomer.getValue();
			String user = comboUser.getValue();
			Jdbc.updateDB("" +
				"INSERT INTO appointments " +
				"VALUES (\"" + id + "\", \"" + title + "\", \"" + description + "\", \"" + location + "\", \"" + type +
				"\", \"" + utcStart.toLocalDateTime() + "\", \"" + utcEnd.toLocalDateTime() + "\", \"" +
				utcNow.toLocalDateTime() + "\", \"" + controllerMain.user + "\", \"" + utcNow.toLocalDateTime() +
				"\", \"" + controllerMain.user + "\", \"" + customerId + "\", \"" + userId + "\", \"" + contactId + "\")");
			Appointment tempAppointment = new Appointment(id, title, description, location, contact, type,
				localStart.toLocalDateTime(), localEnd.toLocalDateTime(), customer, user);
			controllerMain.allAppointments.add(tempAppointment);
			controllerMain.message = "Appointment was added";
			controllerMain.messageColor = "green";
			controllerMain.appointmentIndex = controllerMain.allAppointments.indexOf(tempAppointment);
			((Stage)buttonCancel.getScene().getWindow()).close();
		}
	}
	/**
	 * Validates input meets data requirements.
	 *
	 * @return - returns true if input meets requirements
	 * @throws SQLException - throws if error communicating with database
	 */
	public boolean inputValidated() throws SQLException {
		resetForm();
		boolean error = false;
		for (int i = 0; i < allLabel.size(); i++) {
			if (allText.get(i).getText().equals("")) {
				allLabel.get(i).setStyle("-fx-text-fill: red");
				error = true;
			}
		}
		if (comboContact.getValue() == null) {
			labelContact.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (pickStartDate.getValue() == null) {
			labelStartDate.setStyle("-fx-text-fill:red");
			error = true;
		} else if (pickStartDate.getEditor().getText().equals("")) {
			labelStartDate.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (comboStartTime.getValue() == null) {
			labelStartTime.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (pickEndDate.getValue() == null) {
			labelEndDate.setStyle("-fx-text-fill:red");
			error = true;
		} else if (pickEndDate.getEditor().getText().equals("")) {
			labelEndDate.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (comboEndTime.getValue() == null) {
			labelEndTime.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (comboCustomer.getValue() == null) {
			labelCustomer.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (comboUser.getValue() == null) {
			labelUser.setStyle("-fx-text-fill:red");
			error = true;
		}
		if (error) {
			labelError.setText("Field(s) cannot be empty");
			return false;
		}
		LocalDateTime dtStart = LocalDateTime.of(
			pickStartDate.getValue().getYear(),
			pickStartDate.getValue().getMonth(),
			pickStartDate.getValue().getDayOfMonth(),
			comboStartTime.getValue().getHour(),
			comboStartTime.getValue().getMinute());
		LocalDateTime dtEnd = LocalDateTime.of(
			pickEndDate.getValue().getYear(),
			pickEndDate.getValue().getMonth(),
			pickEndDate.getValue().getDayOfMonth(),
			comboEndTime.getValue().getHour(),
			comboEndTime.getValue().getMinute());
		if (dtStart.isAfter(dtEnd)) {
			labelStartDate.setStyle("-fx-text-fill:red");
			labelEndDate.setStyle("-fx-text-fill:red");
			labelError.setText("End date/time is before start date/time");
			return false;
		}
		ZoneId zoneUTC = ZoneId.of("UTC");
		ZonedDateTime localStart = ZonedDateTime.of(dtStart, ZoneId.systemDefault());
		ZonedDateTime localEnd = ZonedDateTime.of(dtEnd, ZoneId.systemDefault());
		LocalDateTime ldtOpenBusiness = LocalDateTime.of(localStart.getYear(), localStart.getMonth(), localStart.getDayOfMonth(), 8, 0);
		ZonedDateTime estOpenBusiness = ZonedDateTime.of(ldtOpenBusiness, ZoneId.of("America/New_York"));
		LocalDateTime ldtCloseBusiness = LocalDateTime.of(localEnd.getYear(), localEnd.getMonth(), localEnd.getDayOfMonth(), 22, 0);
		ZonedDateTime estCloseBusiness = ZonedDateTime.of(ldtCloseBusiness, ZoneId.of("America/New_York"));
		if (localStart.isBefore(estOpenBusiness)) {
			labelStartTime.setStyle("-fx-text-fill: red");
			labelError.setText("Start time is not within business hours (8am-10pm EST)");
			return false;
		}
		if (localEnd.isAfter(estCloseBusiness)) {
			labelEndTime.setStyle("-fx-text-fill: red");
			labelError.setText("End time is not within business hours (8am-10pm EST)");
			return false;
		}
		ResultSet appointmentSet = Jdbc.queryDB("" +
			"SELECT Appointment_ID, Start, End " +
			"FROM appointments ");
		while (appointmentSet.next()) {
			ZonedDateTime utcStartDB = ZonedDateTime.of(appointmentSet.getTimestamp("Start").toLocalDateTime(), zoneUTC);
			ZonedDateTime utcEndDB = ZonedDateTime.of(appointmentSet.getTimestamp("End").toLocalDateTime(), zoneUTC);
			if ((localStart.isAfter(utcStartDB) && localStart.isBefore(utcEndDB))
				|| ((localEnd.isAfter(utcStartDB) && localEnd.isBefore(utcEndDB))
				|| localStart.isEqual(utcStartDB) || localEnd.isEqual(utcEndDB)
				|| localEnd.isEqual(utcEndDB) || localEnd.isEqual(utcEndDB)
				|| (utcStartDB.isAfter(localStart) && utcStartDB.isBefore(localEnd))
				|| (utcEndDB.isAfter(localEnd) && utcEndDB.isBefore(localEnd)))) {
				labelError.setStyle("-fx-text-fill: red");
				labelError.setText("Start date/time conflicts with appointment " + appointmentSet.getString("Appointment_ID"));
				return false;
			}
		}
		return true;
	}
	/**
	 * LAMBDA_EXPRESSION1.
	 * I used a lambda expression inside a forEach method on an array list. This allowed for much cleaner code in a
	 * single line. It's also more readable.
	 *
	 * Resets formatting on gui elements.
	 */
	public void resetForm() {
		allLabel.forEach( label -> label.setStyle("-fx-text-fill: white"));
		labelContact.setStyle("-fx-text-fill: white");
		labelStartDate.setStyle("-fx-text-fill: white");
		labelStartTime.setStyle("-fx-text-fill: white");
		labelEndDate.setStyle("-fx-text-fill: white");
		labelEndTime.setStyle("-fx-text-fill: white");
		labelCustomer.setStyle("-fx-text-fill: white");
		labelUser.setStyle("-fx-text-fill: white");
		labelError.setText("");
	}
}
