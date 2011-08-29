package com.dekarrin.db;

import java.sql.*;
import java.util.Properties;

public class MySqlEngine implements DatabaseManager {
	
	private Connection connection;
	private Statement statement;
	private String query = null;
	private ResultSet result;
	private int affectedRows;
	private long insertId = 0L;
	private String lastTable = null;
	
	/**
	 * Creates a newly-allocated MySqlEngine.
	 */
	public MySqlEngine() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine open(String host, int port, String user, String password) throws SQLException {
		Properties props = new Properties();
		props.put("user", user);
		props.put("password", password);
		String dbUrl = "jdbc:mysql://"+host+":"+port+"/";
		connection = DriverManager.getConnection(dbUrl, props);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getLastQuery() {
		return query;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long getInsertId() {
		return insertId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine use(String db) throws SQLException {
		query = "USE `"+db+"`;";
		runQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] showDatabases() throws SQLException {
		query = "SHOW DATABASES;";
		getQuery();
		TableData td = getResult();
		String[] databases = td.getArray(0);
		return databases;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] showTables() throws SQLException {
		query = "SHOW TABLES;";
		getQuery();
		TableData td = getResult();
		String[] tables = td.getArray(0);
		return tables;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] showColumns(String table) throws SQLException {
		query = "SHOW COLUMNS FROM `"+table+"`;";
		getQuery();
		TableData td = getResult();
		String[] columns = td.getArray(0);
		return columns;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine insert(TableData data) throws SQLException {
		lastTable = data.getTable();
		query = "INSERT INTO `"+data.getTable()+"` (";
		for(int i = 0; i < data.columns(); i++) {
			query += "`"+data.columnName(i)+"`";
			query += (i + 1 < data.columns()) ? "," : "";
		}
		query += ") VALUES ";
		for(int i = 0; i < data.rows(); data.jump(i++)) {
			query += "(";
			for(int j = 0; j < data.columns(); data.jumpColumn(j++)) {
				query += "'"+data.get()+"'";
				query += (data.hasNextColumn()) ? "," : "";
			}
			query += ")";
			query += (i + 1 < data.rows()) ? "," : "";
		}
		query += ";";
		insertQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine insertEmpty(String table) throws SQLException {
		lastTable = table;
		query = "INSERT INTO `"+table+"` () VALUES ();";
		insertQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine update(TableData data, String where) throws SQLException {
		lastTable = data.getTable();
		query = "UPDATE `"+data.getTable()+"` SET ";
		data.reset();
		for(int i = 0; i < data.columns(); i++) {
			query += String.format("`%s`='%s'", data.columnName(i), data.get(i));
			query += (i + 1 < data.columns()) ? "," : "";
		}
		query += " WHERE "+where+";";
		runQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine delete(String table, String where) throws SQLException {
		lastTable = table;
		query = "DELETE FROM `"+table+"` WHERE "+where+";";
		runQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine executeQuery(String query, String table) throws SQLException {
		lastTable = table;
		this.query = query;
		getQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String selectItem(String column, String table, String where) throws SQLException {
		lastTable = table;
		query = "SELECT `"+column+"` FROM `"+table+"` WHERE "+where+";";
		getQuery();
		TableData td = getResult();
		String item = null;
		if(!td.isEmpty()) {
			item = td.get(column);
		}
		return item;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine call(String proc) throws SQLException {
		query = String.format("CALL %s();", proc);
		getQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine selectRows(String table, String where) throws SQLException {
		lastTable = table;
		query = String.format("SELECT * FROM `%s` WHERE %s;", table, where);
		getQuery();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine updateItem(String table, String column, String value, String where) throws SQLException {
		query = "UPDATE `"+table+"` SET `"+column+"`='"+value+"' WHERE "+where+";";
		runQuery();
		return this;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public TableData getResult() throws SQLException {
		ResultSetMetaData md = result.getMetaData();
		TableData tableResult = new TableData(lastTable);
		// The Java SQL package uses index 1 as the first column, NOT index 0.
		for(int i = 1; i <= md.getColumnCount(); i++) {
			tableResult.addColumn(md.getColumnLabel(i));
		}
		while(result.next()) {
			tableResult.addRow();
			for(int i = 1; i <= tableResult.columns(); i++) {
				// TableData column index not set explicitly;
				// it will automatically 0-index them.
				tableResult.set(result.getString(i));
			}
		}
		return tableResult;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getAffected() {
		return affectedRows;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public MySqlEngine close() throws SQLException {
		connection.close();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int count(String table, String where) throws SQLException {
		query = "SELECT COUNT(*) FROM `"+table+"` WHERE "+where+";";
		getQuery();
		int rowCount = getResult().getInt(0);
		return rowCount;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int count(String column, String table, String where) throws SQLException {
		query = "SELECT COUNT(`"+column+"`) FROM `"+table+"` WHERE "+where+";";
		getQuery();
		int rowCount = getResult().getInt(0);
		return rowCount;
	}
	
	/**
	 * Runs a query on the database and saves the output.
	 * 
	 * @param query
	 * The query to run.
	 */
	private void getQuery() throws SQLException {
		createSelectStatement();
		runSelectQuery();
	}
	
	/**
	 * Runs a query on the database.
	 * 
	 * @param query
	 * The query to run.
	 */
	private void runQuery() throws SQLException {
		createUpdateStatement();
		runUpdateQuery();
	}
	
	/**
	 * Runs a query specialized for insertions on the database.
	 */
	private void insertQuery() throws SQLException {
		createInsertStatement();
		affectedRows = ((PreparedStatement)statement).executeUpdate();
		ResultSet keys = statement.getGeneratedKeys();
		if(keys.next()) {
			insertId = keys.getLong(1);
		}
	}
	
	/**
	 * Creates the statement for a query string.
	 */
	private void createUpdateStatement() throws SQLException {
		statement = connection.createStatement();
	}
	
	/**
	 * Prepares the statement for an INSERT query.
	 */
	private void createInsertStatement() throws SQLException {
		statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}
	
	/**
	 * Creates the statement for a query string that is to return
	 * a result set.
	 */
	private void createSelectStatement() throws SQLException {
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
	
	/**
	 * Executes the current statement as an INSERT, UPDATE, DELETE,
	 * or some other SQL statement that returns nothing.
	 * 
	 * @param query
	 * The query to run.
	 */
	private void runUpdateQuery() throws SQLException {
		affectedRows = statement.executeUpdate(query);
	}
	
	/**
	 * Executes the current statement as a SQL statement that returns
	 * a row set.
	 * 
	 * @param query
	 * The query to run.
	 */
	private void runSelectQuery() throws SQLException {
		result = statement.executeQuery(query);
	}
}