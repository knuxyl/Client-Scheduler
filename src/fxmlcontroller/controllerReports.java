package fxmlcontroller;
import database.Jdbc;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import model.Appointment;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
/**
 * Report generation controller class. Generates reports from data collected from database.
 *
 * @author James Watson - 001520415
 */
public class controllerReports implements Initializable {
	/** utc zone */
	public ZoneId zoneUTC = ZoneId.of("UTC");
	/** date and time of now */
	public LocalDateTime now;
	/** local and utc date and time of now */
	public ZonedDateTime localNow, utcNow;
	/** gui text fields */
	public TextArea textAppointments, textSchedule, textLocation;
	/** gui hyperlinks */
	public Hyperlink linkAppointments, linkSchedule, linkLocation;
	/** gui anchor panes */
	public AnchorPane anchorAppointments, anchorSchedule, anchorLocation;
	/** array to store appointment types */
	public ArrayList<String> allTypes = new ArrayList<>();
	/** string builder object for building appointments report */
	public StringBuilder reportAppointments = new StringBuilder();
	/** string builder object for building schedule report */
	public StringBuilder reportSchedule = new StringBuilder();
	/** string builder object for building location report */
	public StringBuilder reportLocation = new StringBuilder();
	/**
	 * Initial setup for class.
	 *
	 * @param url - javafx variable
	 * @param resourceBundle - javafx variable
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			linkRefresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Refreshes all reports. Called when refresh link is clicked.
	 *
	 * @throws SQLException - throws when error communicating with server
	 */
	public void linkRefresh() throws SQLException {
		now = LocalDateTime.now();
		localNow = ZonedDateTime.of(now, ZoneId.systemDefault());
		utcNow = ZonedDateTime.ofInstant(localNow.toInstant(), zoneUTC);
		linkAppointments();
	}
	/**
	 * Link to appointment tab. Generates report for total number of customer appointments by type and month.
	 */
	public void linkAppointments() {
		reportAppointments.setLength(0);
		linkAppointments.setStyle("-fx-background-color: #666666; -fx-underline: false; -fx-focus-color: transparent; " +
		"-fx-background-radius: 8 8 0 0;");
		linkSchedule.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		linkLocation.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		anchorAppointments.setVisible(true);
		anchorSchedule.setVisible(false);
		anchorLocation.setVisible(false);
		reportAppointments.append("" +
		"----------------------------------------\n" +
		"Total number of customer appointments by type and month\n" +
		"----------------------------------------\n" +
		"Report generated by user " + controllerMain.user + "\n" +
		"Local " + localNow.getDayOfMonth() + "-" + localNow.getMonth().name() + "-" + localNow.getYear() + " at " +
		localNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"UTC " + utcNow.getDayOfMonth() + "-" + utcNow.getMonth().name() + "-" + utcNow.getYear() + " at " +
		utcNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"----------------------------------------\n\n");
		for (int i = 1; i < 13; i++) {
			int numberTypes = 0;
			int total = 0;
			reportAppointments.append("--------------------\n" + Month.of(i).name() + "\n--------------------\n");
			allTypes.clear();
			for (Appointment allAppointments : controllerMain.allAppointments) {
				if (allAppointments.getStart().getMonth().name().equals(Month.of(i).name())) {
					total++;
					if (!allTypes.contains(allAppointments.getType())) {
						allTypes.add(allAppointments.getType());
						numberTypes = 0;
						for (Appointment typeAppointments : controllerMain.allAppointments) {
							if (allAppointments.getType().equals(typeAppointments.getType())
								&& typeAppointments.getStart().getMonth().name().equals(Month.of(i).name())) {
								numberTypes++;
							}
						}
						reportAppointments.append("\t" + allAppointments.getType() + " : " + numberTypes + "\n");
					}
				}
			}
			reportAppointments.append("\tTotal Appointments: " + total + "\n\n");
		}
		reportAppointments.append("\n\n");
		textAppointments.setText(reportAppointments.toString());
	}
	/**
	 * Link to schedule tab. Generates schedule reports for each contact.
	 *
	 * @throws SQLException - throws when error communicating with server
	 */
	public void linkSchedule() throws SQLException {
		reportSchedule.setLength(0);
		linkAppointments.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		"-fx-background-radius: 8 8 0 0;");
		linkSchedule.setStyle("-fx-background-color: #666666; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		linkLocation.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		anchorAppointments.setVisible(false);
		anchorSchedule.setVisible(true);
		anchorLocation.setVisible(false);
		reportSchedule.append("" +
		"----------------------------------------\n" +
		"Contact Schedules\n" +
		"----------------------------------------\n" +
		"Report generated by " + controllerMain.user + "\n" +
		"Local " + localNow.getDayOfMonth() + "-" + localNow.getMonth().name() + "-" + localNow.getYear() + " at " +
		localNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"UTC " + utcNow.getDayOfMonth() + "-" + utcNow.getMonth().name() + "-" + utcNow.getYear() + " at " +
		utcNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"----------------------------------------\n\n");
		ResultSet contactSet = Jdbc.queryDB("" +
			"SELECT Contact_Name, Contact_ID " +
			"FROM contacts");
		while (contactSet.next()) {
			reportSchedule.append("--------------------\n" + contactSet.getString("Contact_Name") + "\n--------------------\n");
			ResultSet appointmentSet = Jdbc.queryDB("" +
				"SELECT Appointment_ID, Title, Type, Description, Start, End, Customer_ID " +
				"FROM appointments " +
				"WHERE Contact_ID=\"" + contactSet.getString("Contact_ID") + "\"");
			while (appointmentSet.next()) {
				String id = appointmentSet.getString("Appointment_ID");
				String customerId = appointmentSet.getString("Customer_ID");
				ResultSet customerSet = Jdbc.queryDB("" +
					"SELECT Customer_Name " +
					"FROM customers " +
					"WHERE Customer_ID=\"" + customerId + "\"");
				customerSet.next();
				String type = appointmentSet.getString("Type");
				String customer = customerSet.getString("Customer_Name");
				String title = appointmentSet.getString("Title");
				String description = appointmentSet.getString("Description");
				LocalDateTime dbStart = appointmentSet.getTimestamp("Start").toLocalDateTime();
				LocalDateTime dbEnd = appointmentSet.getTimestamp("End").toLocalDateTime();
				ZonedDateTime localStart = ZonedDateTime.of(dbStart, ZoneId.systemDefault());
				ZonedDateTime utcStart = ZonedDateTime.ofInstant(localStart.toInstant(), zoneUTC);
				ZonedDateTime localEnd = ZonedDateTime.of(dbEnd, ZoneId.systemDefault());
				ZonedDateTime utcEnd = ZonedDateTime.ofInstant(localEnd.toInstant(), zoneUTC);
				reportSchedule.append("Appointment [ " + id + " ] - " + title + "\n");
				reportSchedule.append("\tType - " + type + " \n");
				reportSchedule.append("\tDescription - " + description + "\n");
				reportSchedule.append("\tCustomer [ " + customerId + " ] - " + customer + "\n");
				reportSchedule.append("\tStart UTC - " + utcStart.toLocalDateTime() + "\n");
				reportSchedule.append("\tEnd UTC - " + utcEnd.toLocalDateTime() + "\n");
				reportSchedule.append("\tStart Local - " + localStart.toLocalDateTime() + "\n");
				reportSchedule.append("\tEnd Local - " + localEnd.toLocalDateTime() + "\n");
			}
			reportSchedule.append("\n");
		}
		textSchedule.setText(reportSchedule.toString());
	}
	/**
	 * Link to location tab. Generates report for appointment count by location.
	 *
	 * @throws SQLException - throws when error communicating with server
	 */
	public void linkLocation() throws SQLException {
		reportLocation.setLength(0);
		linkAppointments.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		"-fx-background-radius: 8 8 0 0;");
		linkSchedule.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		linkLocation.setStyle("-fx-background-color: #666666; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		anchorAppointments.setVisible(false);
		anchorSchedule.setVisible(false);
		anchorLocation.setVisible(true);
		reportLocation.append("" +
		"----------------------------------------\n" +
		"Appointments by location\n" +
		"----------------------------------------\n" +
		"Report generated by " + controllerMain.user + "\n" +
		"Local " + localNow.getDayOfMonth() + "-" + localNow.getMonth().name() + "-" + localNow.getYear() + " at " +
		localNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"UTC " + utcNow.getDayOfMonth() + "-" + utcNow.getMonth().name() + "-" + utcNow.getYear() + " at " +
		utcNow.getHour() + ":" + utcNow.getMinute() + "\n" +
		"----------------------------------------\n\n");
		ResultSet countrySet = Jdbc.queryDB("" +
			"SELECT Country, Country_ID " +
			"FROM countries ");
		while (countrySet.next()) {
			String countryId = countrySet.getString("Country_ID");
			reportLocation.append("--------------------\n" + countrySet.getString("Country") + "\n--------------------\n");
			ResultSet divisionSet = Jdbc.queryDB("" +
				"SELECT Division_ID, Division, Country_ID " +
				"FROM first_level_divisions " +
				"WHERE Country_ID=\"" + countryId + "\"");
			while (divisionSet.next()) {
				ResultSet appointmentSet = Jdbc.queryDB("" +
					"SELECT COUNT(appointments.Customer_ID) AS appointments " +
					"FROM appointments " +
					"INNER JOIN customers ON appointments.Customer_ID=customers.customer_ID " +
					"WHERE customers.Division_ID=\"" + divisionSet.getString("Division_ID") + "\"");
				appointmentSet.next();
				reportLocation.append("\t" + divisionSet.getString("Division") + " - " +
					appointmentSet.getString("appointments") + "\n");
			}
			reportLocation.append("\n");
		}
		textLocation.setText(reportLocation.toString());
	}
}
