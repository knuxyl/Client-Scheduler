package database;
import java.sql.*;
/**
 * Database driver class. Implements variables and methods for communicating with the SQL database.
 *
 * @author James Watson - 001520415
 */
public class Jdbc {
	/** database driver */
	private static final String driver = "com.mysql.cj.jdbc.Driver";
	/** server ip address */
	private static final String ip = "localhost";
	/** server port number */
	private static final String port = "3306";
	/** database schema to use */
	private static final String database = "client_schedule";
	/** database username */
	private static final String username = "username";
	/** database password */
	private static final String password = "password";
	/** concatenated string url for the driver */
	private static final String url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
	/** connection object */
	private static Connection connection = null;
	/**
	 * Database query method for returning data.
	 *
	 * @param query - query to be performed on server
	 * @return - returns results from database query
	 * @throws SQLException - throws if error communicating with server
	 */
	public static ResultSet queryDB(String query) throws SQLException {
		Statement statement = connection.createStatement();
		return statement.executeQuery(query);
	}
	/**
	 * Database query method for inserting or updating data.
	 *
	 * @param query - query to be performed on server
	 * @throws SQLException - throws if error communicating with server
	 */
	public static void updateDB(String query) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}
	/**
	 * Makes connection to SQL server.
	 *
	 * @return - returns true if connected/can connect
	 */
	public static boolean connect() {
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, username, password);
			return true;
		}
		catch(ClassNotFoundException | SQLException e) {
			System.out.println("Error:" + e.getMessage());
			return false;
		}
	}
	/**
	 * Check if connected to server. If not connected, will try to connect.
	 *
	 * @return - returns false if connection cannot be made to server
	 * @throws SQLException - throws if error communicating with server
	 */
	public static boolean isConnected() throws SQLException {
		if (connection == null) {
			return connect();
		} else if (connection.isValid(0)) {
			return true;
		} else {
			return connect();
		}
	}
}