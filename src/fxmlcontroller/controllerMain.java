package fxmlcontroller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import database.Jdbc;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import model.Customer;
import model.Appointment;
import java.io.File;
/**
 * Main window controller class. Implements variables and methods for updating appearance and data from the server.
 *
 * @author James Watson - 001520415
 */
public class controllerMain implements Initializable {
	/** log file object */
	public File logFile = new File("login_activity.txt");
	/** currently selected customer object */
	public static Customer customer;
	/** currently selected appointment object */
	public static Appointment appointment;
	/** gui text variables */
	public static String message, messageColor, user, loginMessage = "";
	/** table's selected index */
	public static int customerIndex = -1, appointmentIndex = -1;
	/** array list of all customers */
	public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
	/** array list of all appointments */
	public static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
	/** gui labels */
	public Label labelLoginZone, labelStatusZone, labelLoginMessage, labelUsername, labelPassword, labelCustomerTotal,
		labelCustomerMessage, labelAppointmentTotal, labelAppointmentMessage, labelCurrentUser, labelTitle,
		labelMainMessage, labelLanguage;
	/** gui buttons */
	public Button buttonSubmit, buttonCustomerAdd, buttonCustomerUpdate, buttonCustomerDelete, buttonAppointmentAdd,
			buttonAppointmentEdit, buttonAppointmentDelete;
	/** gui hyperlinks */
	public Hyperlink linkCustomer, linkAppointment, linkRefresh, linkExit, linkEnglish, linkFrench;
	/** gui anchor panes */
	public AnchorPane anchorLogin, anchorMain, anchorCustomer, anchorAppointment;
	/** gui customer tables */
	public TableView<Customer> tableCustomer;
	/** gui appointment table */
	public TableView<Appointment> tableAppointment;
	/** gui table columns that stores customer strings */
	public TableColumn<String, Customer> columnCustomerId, columnCustomerDivision, columnCustomerName,
		columnCustomerAddress, columnCustomerPostal, columnCustomerPhone;
	/** gui table column that stores appointment strings */
	public TableColumn<String, Appointment> columnAppointmentId, columnAppointmentTitle, columnAppointmentDescription,
		columnAppointmentLocation, columnAppointmentContact, columnAppointmentType, columnAppointmentCustomer,
		columnAppointmentUser;
	/** array list of all appointment months */
	public static ObservableList<Appointment> monthAppointments = FXCollections.observableArrayList();
	/** array list of all appointment weeks */
	public static ObservableList<Appointment> weekAppointments = FXCollections.observableArrayList();
	/** gui table column that stores appointment timestamps */
	public TableColumn<Timestamp, Appointment> columnAppointmentStart, columnAppointmentEnd;
	/** gui text fields */
	public TextField textUsername, textPassword;
	/** object to hold translations */
	public ResourceBundle translate;
	/** toggle group for radio buttons */
	public ToggleGroup appointmentFilter;
	/** gui radio buttons */
	public RadioButton radioAll, radioMonth, radioWeek;
	/**
	* LAMBDA_EXPRESSION2.
	* For each event listener I used lambda expressions. This alleviated having to create a new object for
	* each listener and allowed for simpler and more readable code.
	*
	 * Initial setup for class. Sets formatting and adds data from the server that will be needed.
	 *
	 * @param url - javafx variable
	 * @param resourceBundle - javafx variable
	 */
	public Image icon = new Image("/res/icon.png");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	setLanguage(Main.language);
		anchorLogin.setVisible(true);
    	anchorMain.setVisible(false);
    	anchorAppointment.setVisible(false);
    	anchorCustomer.setVisible(true);
    	columnCustomerName.setResizable(false);
    	columnCustomerName.setReorderable(false);
    	labelLoginZone.setText(ZoneId.systemDefault().toString());
    	labelStatusZone.setText(ZoneId.systemDefault().toString());
		columnCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnCustomerPostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
        columnCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnCustomerDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
		columnAppointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnAppointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		columnAppointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		columnAppointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
		columnAppointmentContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
		columnAppointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
		columnAppointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
		columnAppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
		columnAppointmentCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
		columnAppointmentUser.setCellValueFactory(new PropertyValueFactory<>("user"));
		tableCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, newSelected) -> {
			if (newSelected != null) {
				customer = tableCustomer.getSelectionModel().getSelectedItem();
				customerIndex = allCustomers.indexOf(customer);
				buttonCustomerUpdate.setDisable(false);
				buttonCustomerDelete.setDisable(false);
			} else {
				buttonCustomerUpdate.setDisable(true);
				buttonCustomerDelete.setDisable(true);
			}
		});
		tableAppointment.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, newSelected) -> {
			if (newSelected != null) {
				appointment = tableAppointment.getSelectionModel().getSelectedItem();
				appointmentIndex = allAppointments.indexOf(appointment);
				buttonAppointmentEdit.setDisable(false);
				buttonAppointmentDelete.setDisable(false);
			} else {
				buttonAppointmentEdit.setDisable(true);
				buttonAppointmentDelete.setDisable(true);
			}
		});
		appointmentFilter.selectedToggleProperty().addListener((observable, oldSelected, newSelected) -> {
			LocalDateTime localNow = LocalDateTime.now();
			monthAppointments.clear();
			weekAppointments.clear();
			if (radioAll.isSelected()) {
				tableAppointment.setItems(allAppointments);
			} else {
				for (Appointment appointment : allAppointments) {
					if (radioMonth.isSelected()) {
						if (localNow.getMonth() == appointment.getStart().getMonth()) {
							monthAppointments.add(appointment);
						}
						tableAppointment.setItems(monthAppointments);
					} else if (radioWeek.isSelected()) {
						if (appointment.getStart().getDayOfWeek().getValue() >= localNow.getDayOfWeek().getValue()) {
							weekAppointments.add(appointment);
						}
						tableAppointment.setItems(weekAppointments);
					}
				}
			}
		});
    }
   	/**
	 * Sets the login screen's language. Set up to use operating system language settings but can be manually changed.
	 *
	 * @param language - language to translate to
	 */
   	public void setLanguage(String language) {
	    Locale.setDefault(new Locale(language));
     	translate = ResourceBundle.getBundle("language/Language", Locale.getDefault());
    	buttonSubmit.setText(translate.getString("submit"));
    	labelUsername.setText(translate.getString("username"));
    	labelPassword.setText(translate.getString("password"));
    	labelTitle.setText(translate.getString("title"));
    	linkExit.setText(translate.getString("exit"));
    	labelLanguage.setText(translate.getString("language"));
    	linkEnglish.setText(translate.getString("english"));
    	linkFrench.setText(translate.getString("french"));
    	if (!loginMessage.isEmpty()) {
    		labelLoginMessage.setText(translate.getString(loginMessage));
		}
	}
   	/**
	 * Sets the login screen's language to english.
	 */
	public void linkEnglish() {
		setLanguage("en");
	}
   	/**
	 * Sets the login screen's language to french.
	 */
	public void linkFrench() {
		setLanguage("fr");
	}
   	/**
	 * Closes the application. Called when exit is clicked.
	 */
	public void linkExit() {
		((Stage)linkExit.getScene().getWindow()).close();
	}
   	/**
	 * Switches view to main scene. Will call methods to verify login credentials first.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonSubmit() throws SQLException {
		if (Jdbc.isConnected()) {
			if (textUsername.getText().trim().equals("") || textPassword.getText().equals("")) {
				loginMessage = "error_empty";
				labelLoginMessage.setText(translate.getString(loginMessage));
			} else if (loginSuccess()) {
				labelLoginMessage.setText("");
				textPassword.setText("");
				anchorLogin.setVisible(false);
				anchorMain.setVisible(true);
				setupAppointment();
				if (checkAppointment()) {
					linkAppointment();
					tableAppointment.getSelectionModel().select(appointmentIndex);
				} else {
					linkCustomer();
				}
				labelCurrentUser.setText("Currently logged in as " + user);
			}
		} else {
			loginMessage = "server_down";
			labelLoginMessage.setText(translate.getString("server_down"));
		}
	}
   	/**
	 * Refreshes all data in case of outside manipulation of data on server.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void linkRefresh() throws SQLException {
		if (Jdbc.isConnected()) {
			tableCustomer.getItems().clear();
			tableAppointment.getItems().clear();
			setupCustomer();
			setupAppointment();
			checkAppointment();
		} else {
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
		labelCustomerMessage.setText("");
		labelAppointmentMessage.setText("");
	}
   	/**
	 * Logs user out of application. Returns to log in screen and clears password field. Records logout to log file.
	 *
	 * @throws IOException - throws if cannot write to log file
	 */
	public void linkLogout() throws IOException {
		anchorAppointment.setVisible(false);
		anchorCustomer.setVisible(true);
		anchorMain.setVisible(false);
		anchorLogin.setVisible(true);
		tableAppointment.getItems().clear();
		tableCustomer.getItems().clear();
		loginMessage = "logged_out";
		labelLoginMessage.setText(translate.getString(loginMessage));
		labelCustomerMessage.setText("");
		labelAppointmentMessage.setText("");
		Timestamp timeLocal = Timestamp.valueOf(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDateTime());
		if (logFile.createNewFile()) {
			FileWriter logWriter = new FileWriter("login_activity.txt");
			String outputText = "[" + timeLocal + "] User logged out: " + user;
			logWriter.write(outputText);
			logWriter.close();
		} else {
			FileWriter logWriter = new FileWriter("login_activity.txt", true);
			String outputText = "\n[" + timeLocal + "] User logged out: " + user;
			logWriter.append(outputText);
			logWriter.close();
		}
		radioAll.setSelected(true);
	}
   	/**
	 * Switches scene to customer tab.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void linkCustomer() throws SQLException {
		linkCustomer.setStyle("-fx-background-color: #666666; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		linkAppointment.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		anchorCustomer.setVisible(true);
		anchorAppointment.setVisible(false);
		setupCustomer();
	}
   	/**
	 * Switches scene to appointment tab.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void linkAppointment() throws SQLException {
		linkCustomer.setStyle("-fx-background-color: #333333; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		linkAppointment.setStyle("-fx-background-color: #666666; -fx-underline: false; -fx-focus-color: transparent; " +
		 	"-fx-background-radius: 8 8 0 0;");
		anchorCustomer.setVisible(false);
		anchorAppointment.setVisible(true);
		setupAppointment();
	}
   	/**
	 * Opens window for adding a customer.
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonCustomerAdd() throws IOException, SQLException {
		if (Jdbc.isConnected()) {
			setMessage(labelCustomerMessage, "Adding customer...", "white");
			labelMainMessage.setText("");
			Stage stage = createStage("/fxmlscene/CustomerAdd.fxml", 276, 376);
			stage.showAndWait();
			setMessage(labelCustomerMessage, message, messageColor);
			tableCustomer.getSelectionModel().select(customerIndex);
			labelCustomerTotal.setText(Integer.toString(allCustomers.size()));
		} else {
			labelCustomerMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Opens window for updating a customer.
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonCustomerUpdate() throws IOException, SQLException {
		if (Jdbc.isConnected()) {
			setMessage(labelCustomerMessage, "Updating customer...", "white");
			labelMainMessage.setText("");
			Stage stage = createStage("/fxmlscene/CustomerUpdate.fxml", 276, 376);
			stage.showAndWait();
			setMessage(labelCustomerMessage, message, messageColor);
			tableCustomer.getSelectionModel().select(customerIndex);
			tableCustomer.refresh();
		} else {
			labelCustomerMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Deletes a customer and all associated appointments from the database.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonCustomerDelete() throws SQLException {
		if (Jdbc.isConnected()) {
			Customer customer = tableCustomer.getSelectionModel().getSelectedItem();
			for (Appointment appointment : allAppointments) {
				if (appointment.getCustomer().equals(customer.getId())) {
					Jdbc.updateDB("" +
						"DELETE FROM appointments " +
						"WHERE Customer_ID=\"" + customer.getId() + "\"");
					allAppointments.remove(appointment);
					tableAppointment.refresh();
				}
			}
			Jdbc.updateDB("" +
				"DELETE FROM customers " +
				"WHERE Customer_ID=\"" + customer.getId() + "\"");
			allCustomers.remove(customer);
			setMessage(labelCustomerMessage, "Customer and appointment(s) deleted", "red");
			labelMainMessage.setText("");
			labelCustomerTotal.setText(Integer.toString(allCustomers.size()));
		} else {
			labelCustomerMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Opens window for adding an appointment.
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonAppointmentAdd() throws SQLException, IOException {
		if (Jdbc.isConnected()) {
			setMessage(labelAppointmentMessage, "Adding appointment...", "white");
			labelMainMessage.setText("");
			Stage stage = createStage("/fxmlscene/AppointmentAdd.fxml", 328, 526);
			stage.showAndWait();
			setMessage(labelAppointmentMessage, message, messageColor);
			tableAppointment.getSelectionModel().select(appointmentIndex);
			labelAppointmentTotal.setText(Integer.toString(allAppointments.size()));
		} else {
			labelAppointmentMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Opens window for updating an appointment.
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonAppointmentEdit() throws SQLException, IOException {
		if (Jdbc.isConnected()) {
			setMessage(labelAppointmentMessage, "Updating appointment...", "white");
			labelMainMessage.setText("");
			Stage stage = createStage("/fxmlscene/AppointmentEdit.fxml", 328, 526);
			stage.showAndWait();
			setMessage(labelAppointmentMessage, message, messageColor);
			labelAppointmentTotal.setText(Integer.toString(allAppointments.size()));
			tableAppointment.getSelectionModel().select(appointmentIndex);
			tableAppointment.refresh();
		} else {
			labelAppointmentMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Deletes an appointment from the database.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonAppointmentDelete() throws SQLException {
		if (Jdbc.isConnected()) {
			Appointment appointment = tableAppointment.getSelectionModel().getSelectedItem();
			Jdbc.updateDB("" +
				"DELETE FROM appointments " +
				"WHERE Appointment_ID=\"" + appointment.getId() + "\"");
			allAppointments.remove(appointment);
			setMessage(labelAppointmentMessage, "Appointment [ " + appointment.getId() + " ][ " +
				appointment.getType() + " ] deleted", "red");
			labelMainMessage.setText("");
			labelCustomerTotal.setText(Integer.toString(allAppointments.size()));
		} else {
			labelAppointmentMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
   	/**
	 * Verifies credentials on server.
	 *
	 * @return - returns true if credentials are validated
	 * @throws SQLException - throws if error communicating with server
	 */
	public boolean loginSuccess() throws SQLException {
		user = textUsername.getText().trim();
		ResultSet login = Jdbc.queryDB("SELECT * FROM users WHERE User_Name=\"" + textUsername.getText().trim() + "\"");
		if (login.next()) {
			if (login.getString("Password").equals(textPassword.getText())) {
				String logMessage = "Success";
				updateLog(user, logMessage);
				return true;
			} else {
				loginMessage = "invalid_password";
				labelLoginMessage.setText(translate.getString(loginMessage));
				String logMessage = "Invalid Password";
				updateLog(user, logMessage);
				return false;

			}
		} else {
			loginMessage = "invalid_user";
			labelLoginMessage.setText(translate.getString(loginMessage));
			String logMessage = "Invalid User";
			updateLog(user, logMessage);
			return false;
		}
	}
   	/**
	 * Retrieves all customer data from server.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void setupCustomer() throws SQLException {
		tableCustomer.getItems().clear();
		ResultSet customers = Jdbc.queryDB("SELECT * FROM customers");
		while (customers.next()) {
			String id = customers.getString("Customer_ID");
			String name = customers.getString("Customer_Name");
			String address = customers.getString("Address");
			String postal = customers.getString("Postal_Code");
			String phone = customers.getString("Phone");
			String dbDivision = customers.getString("Division_ID");
			ResultSet divisionSet = Jdbc.queryDB("" +
				"SELECT Division " +
				"FROM first_level_divisions " +
				"WHERE Division_ID=\"" + dbDivision + "\"");
			divisionSet.next();
			String division = divisionSet.getString("Division");
			allCustomers.add(new Customer(id, name, address, postal, phone, division));
		}
		tableCustomer.setItems(allCustomers);
		labelCustomerTotal.setText(Integer.toString(allCustomers.size()));
	}
   	/**
	 * Retrieves all appointment data from the server.
	 *
	 * @throws SQLException - throws if error communicating with server
	 */
	public void setupAppointment() throws SQLException {
		tableAppointment.getItems().clear();
		ResultSet appointments = Jdbc.queryDB("SELECT * FROM appointments");
		while (appointments.next()) {
			ZoneId zoneUTC = ZoneId.of("UTC");
			String id = appointments.getString("Appointment_ID");
			String title = appointments.getString("Title");
			String description = appointments.getString("Description");
			String location = appointments.getString("Location");
			String contactId = appointments.getString("Contact_ID");
			String type = appointments.getString("Type");
			ZonedDateTime zonedStartUTC = ZonedDateTime.of(appointments.getTimestamp("Start").toLocalDateTime(), zoneUTC);
			ZonedDateTime zonedStartLocal = ZonedDateTime.ofInstant(zonedStartUTC.toInstant(), ZoneId.systemDefault());
			ZonedDateTime zonedEndUTC = ZonedDateTime.of(appointments.getTimestamp("End").toLocalDateTime(), zoneUTC);
			ZonedDateTime zonedEndLocal = ZonedDateTime.ofInstant(zonedEndUTC.toInstant(), ZoneId.systemDefault());
			String customerId = appointments.getString("Customer_ID");
			String userId = appointments.getString("User_ID");
			ResultSet contactSet = Jdbc.queryDB("" +
				"SELECT Contact_Name " +
				"FROM contacts " +
				"WHERE Contact_ID =\"" + contactId + "\"");
			contactSet.next();
			String contact = contactSet.getString("Contact_Name");
			ResultSet customerSet = Jdbc.queryDB("" +
				"SELECT Customer_Name " +
				"FROM customers " +
				"WHERE Customer_ID =\"" + customerId + "\"");
			customerSet.next();
			String customer = customerSet.getString("Customer_Name");
			ResultSet userSet = Jdbc.queryDB("" +
				"SELECT User_Name " +
				"FROM users " +
				"WHERE User_ID =\"" + userId + "\"");
			userSet.next();
			String user = userSet.getString("User_Name");
			allAppointments.add(new Appointment(id, title, description, location, contact, type,
				zonedStartLocal.toLocalDateTime(), zonedEndLocal.toLocalDateTime(), customer, user));
		}
		tableAppointment.setItems(allAppointments);
		labelAppointmentTotal.setText(Integer.toString(allAppointments.size()));
	}
   	/**
	 * Creates a new window.
	 *
	 * @param file - fxml file for scene
	 * @param width - width of window
	 * @param height - height of window
	 *
	 * @return - returns created stage
	 * @throws IOException - throws if cannot find fxml scene file
	 */
	public Stage createStage(String file, int width, int height) throws IOException {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("QAM2 - Java Application Development");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(file)));
        stage.setScene(new Scene(fxml, width, height));
        return stage;
	}
   	/**
	 * Updates a label's text and color.
	 *
	 * @param label - label to change
	 * @param message - text for label to display
	 * @param color - color of the label's text
	 */
	public void setMessage(Label label, String message, String color) {
		label.setStyle("-fx-text-fill: " + color);
		label.setText(message);
	}
   	/**
	 * Updates the log file. Called every attempt to login excluding if text fields are empty.
	 *
	 * @param attemptUser - user who tried to login
	 * @param attemptMessage - activity description
	 */
	public void updateLog(String attemptUser, String attemptMessage) {
		Timestamp timeLocal = Timestamp.valueOf(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDateTime());
		try  {
			if (logFile.createNewFile()) {
				FileWriter logWriter = new FileWriter("login_activity.txt");
				String outputText = "\n[" + timeLocal + "] Attempted login: " + attemptUser + " [" + attemptMessage + "]";
				//logWriter.write(outputText);
				//logWriter.close();
			} else {
				FileWriter logWriter = new FileWriter("login_activity.txt", true);
				String outputText = "\n[" + timeLocal + "] Attempted login: " + attemptUser + " [" + attemptMessage + "]";
				//logWriter.append(outputText);
				//logWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   	/**
	 * Checks if there is an appointment within 15 minutes of logging in. Will alert the user with a dialog box.
	 * Displays this information on the main screen.
	 *
	 * @return - returns true if there is an upcoming appointment
	 */
	public boolean checkAppointment() {
		for (Appointment appointment : allAppointments) {
			LocalDateTime localNow = LocalDateTime.now();
			Duration duration = Duration.between(localNow, appointment.getStart());
			if (duration.toMinutes() > 0 && duration.toMinutes() <16) {
				labelMainMessage.setStyle("-fx-text-fill: white");
				labelMainMessage.setText("Upcoming appointment ID: " + appointment.getId() + " Title: " +
				appointment.getTitle() + " at " + appointment.getStart().toLocalTime());
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Appointment ID: " + appointment.getId() + " Title: " + appointment.getTitle() +
				" Description: " + appointment.getDescription() + " Time: " + appointment.getStart().toLocalTime());
				alert.setTitle("Upcoming appointment");
				alert.setHeaderText("Upcoming Appointment");
				alert.showAndWait();
				appointmentIndex = allAppointments.indexOf(appointment);

				return true;
			}
		}
		labelMainMessage.setStyle("-fx-text-fill: white");
		labelMainMessage.setText("No upcoming appointments");
		return false;
	}
   	/**
	 * Opens window for generating a report
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 * @throws SQLException - throws if error communicating with server
	 */
	public void buttonReports() throws SQLException, IOException {
		if (Jdbc.isConnected()) {
			setMessage(labelAppointmentMessage, "Generating report...", "white");
			labelMainMessage.setText("");
			Stage stage = createStage("/fxmlscene/Reports.fxml", 480, 600);
			stage.showAndWait();
			setMessage(labelAppointmentMessage, "", messageColor);
			labelAppointmentTotal.setText(Integer.toString(allAppointments.size()));
			tableAppointment.getSelectionModel().select(appointmentIndex);
			tableAppointment.refresh();
		} else {
			labelAppointmentMessage.setText("");
			labelMainMessage.setStyle("-fx-text-fill: red");
			labelMainMessage.setText("Cannot connect to server");
		}
	}
}