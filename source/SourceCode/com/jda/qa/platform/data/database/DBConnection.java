package SourceCode.com.jda.qa.platform.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
/**
 * @Author Santosh Medchalam & Vinay Tangella
 * @Description This class contains methods to connect and interact with the database
 */
public class DBConnection {

	String dbServerName;
	String dbServerPort;
	String instanceName;
	String userName;
	String password;
	Connection connection;
	
	/** 
	 * @Description This is the default constructor of this class.
	 */

	public DBConnection(String dbServerName, String dbServerPort,
			String instanceName, String userName, String password) {

		this.dbServerName = dbServerName;
		this.dbServerPort = dbServerPort;
		this.instanceName = instanceName;
		this.userName = userName;
		this.password = password;

	}

	/** 
	 * @Description This method is establishes a connection to the database and returns the conenction object.
	 */
	public synchronized Connection getConnection() throws Exception {

		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		this.connection = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbServerName + ":" + dbServerPort + ":" + instanceName,
				userName, password);

		// jdbc:oracle:thin:@localhost:1521:AUTO

		return connection;

	}

	/** 
	 * @Description This method executes the db query and returns a ResultSet.
	 */
	public synchronized ResultSet executeQuery(String query) throws Exception {

		Statement crntStmt = null;
		ResultSet resultSet = null;

		try {
			if (this.connection == null || this.connection.isClosed() == true) {
				getConnection();
			}

			crntStmt = connection.createStatement();
			resultSet = crntStmt.executeQuery(query);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultSet;

	}

	/** 
	 * @Description This method takes result set as an argument and returns a arry list.
	 */
	public synchronized ArrayList<HashMap<String, String>> getResultsasArrayList(
			ResultSet resultSet) throws SQLException {

		ResultSetMetaData md = resultSet.getMetaData();
		int columns = md.getColumnCount();
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();

		while (resultSet.next()) {
			HashMap<String, String> resultMap = new HashMap<String, String>();
			results.add(resultMap);
			for (int i = 1; i <= columns; i++) {

				if (resultSet.getObject(i) != null) {
					resultMap.put(md.getColumnName(i).toString(), resultSet
							.getObject(i).toString());
				} else {
					resultMap.put(md.getColumnName(i).toString(), "NULL");
				}

			}
		}

		return results;
	}
	
	/** 
	 * @Description This method takes result set as an argument and returns a arry list.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized ArrayList<ArrayList<String>> getDataMap(ArrayList list) {

		Iterator itr = list.iterator();

		ArrayList<ArrayList<String>> comp = new ArrayList<ArrayList<String>>();

		boolean colNamesAdded = false;
		Object[] colNames = null;
		ArrayList<String> colsArrayLst = new ArrayList<String>();

		while (itr.hasNext()) {

			HashMap<String, String> h = (HashMap<String, String>) itr.next();

			colNames = h.keySet().toArray();

			Object[] colValues = h.values().toArray();

			if (!colNamesAdded) {
				ArrayList<String> lll = new ArrayList<String>();

				Collection c = Arrays.asList(colValues);

				lll.addAll(c);
				comp.add(lll);

				Collection c1 = Arrays.asList(colNames);

				colsArrayLst.addAll(c1);

				colNamesAdded = true;

			} else {

				ArrayList<String> lll = new ArrayList<String>();

				Collection c = Arrays.asList(colValues);

				lll.addAll(c);
				comp.add(lll);

			}

		}
		comp.add(0, colsArrayLst);
		return comp;
	}

	/** 
	 * @Description This method closes the established db connection.
	 */
	public synchronized void closeConnection() {

		if (this.connection != null) {

			try {
				if (!this.connection.isClosed()) {
					try {
						this.connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("Failed to Close the connection");
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}

	}

}
