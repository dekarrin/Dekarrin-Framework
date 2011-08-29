package com.dekarrin.db;

import java.sql.SQLException;

/**
 * The DatabaseManager connects to some kind of RDBM system and
 * exchanges information.
 */
public interface DatabaseManager {
	
	/**
	 * Establishes a connection to the host database. This must
	 * be called before any queries are called.
	 * 
	 * @param host
	 * The URL of the database host.
	 * 
	 * @param port
	 * The port to establish the connection with.
	 * 
	 * @param user
	 * The username to authenticate with.
	 * 
	 * @param password
	 * The password to authenticate with.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem connecting to the host.
	 */
	public DatabaseManager open(String host, int port, String user, String password) throws SQLException;
	
	/**
	 * Gets the last query that the DatabaseManager attempted to execute.
	 * 
	 * @return
	 * The query.
	 */
	public String getLastQuery();
	
	/**
	 * Gets the id of the last row to be inserted into a table.
	 * 
	 * @return
	 * The last id.
	 */
	public long getInsertId();
	
	/**
	 * Gets the number of rows that were affected by the last DDL query.
	 * 
	 * @return
	 * The number of affected rows.
	 */
	public int getAffected();
	
	/**
	 * Establishes a database on the host to use for queries. This database will
	 * be used until the connection is closed or until {@code use()} is called
	 * again.
	 * 
	 * @param db
	 * The name of the database to use.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem with setting the default database.
	 */
	public DatabaseManager use(String db) throws SQLException;
	
	/**
	 * Gets the names of the databases accessible to this DatabaseManager on
	 * the host. This method does not require that {@link use() use()} be
	 * called first; it may even be used to decide which database to use as
	 * the default.
	 * 
	 * @return
	 * The names of the databases.
	 * 
	 * @throws SQLException
	 * If there was a problem with showing the databases.
	 */
	public String[] showDatabases() throws SQLException;
	
	/**
	 * Gets the names of the tables in the current database. This method
	 * requires a default database to be set, and so {@link use() use()}
	 * must be called before this method is.
	 * 
	 * @return
	 * The names of the tables.
	 * 
	 * @throws SQLException
	 * If there was a problem with showing the tables.
	 */
	public String[] showTables() throws SQLException;
	
	/**
	 * Gets the names of the columns in a table. This method requires a
	 * default database to be set, and so {@link use() use()} must be called
	 * before this method is.
	 * 
	 * @param table
	 * The table to get the columns from. This must be a table on the
	 * currently selected database.
	 * 
	 * @return
	 * The names of the columns.
	 * 
	 * @throws SQLException
	 * If there was a problem with showing the columns.
	 */
	public String[] showColumns(String table) throws SQLException;
	
	/**
	 * Inserts rows of data into a table. This method requires a default
	 * database to be set, and so {@link use() use()} must be called before
	 * this method is.
	 * 
	 * @param data
	 * A TableData containing the table to insert into and the data to
	 * be inserted.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem inserting the data.
	 */
	public DatabaseManager insert(TableData data) throws SQLException;
	
	/**
	 * Inserts a row using only the default database values. The only data
	 * the row will contain will be the data that is entered by the
	 * database itself. This method requires a default database to be set,
	 * and so {@link use() use()} must be called before this method is.
	 * 
	 * @param table
	 * The table to insert the empty row into.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem inserting the row.
	 */
	public DatabaseManager insertEmpty(String table) throws SQLException;
	
	/**
	 * Updates rows in a table. This method requires a default database to
	 * be set, and so {@link use() use()} must be called before this method
	 * is.
	 * 
	 * @param data
	 * A TableData containing the table to update as well as the columns to
	 * be changed. The TableData only needs to contain the columns that are
	 * to be modified; columns that are not included are not changed.
	 * 
	 * @param where
	 * The condition that must evaluate to true for each row that is
	 * updated.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem updating the data.
	 */
	public DatabaseManager update(TableData data, String where) throws SQLException;
	
	/**
	 * Deletes rows from a table. This method requires a default database to
	 * be set, and so {@link use() use()} must be called before this method is.
	 * 
	 * @param table
	 * The table to delete from.
	 * 
	 * @param where
	 * The condition that must evaluate to true for each row that is deleted.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem deleting the data.
	 */
	public DatabaseManager delete(String table, String where) throws SQLException;
	
	/**
	 * Passes a query directly to the RDBM. This should be avoided, as it causes
	 * reliance on particular SQL.
	 * 
	 * @param query
	 * The query to execute.
	 * 
	 * @param table
	 * The table that the query is to be executed on. This is necessary in order
	 * to properly set up the TableData result.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem executing the query.
	 */
	public DatabaseManager executeQuery(String query, String table) throws SQLException;
	
	/**
	 * Gets a TableData that represents the result of the last query. This will
	 * give the result of the last query to SELECT data, which is not necessarily
	 * the last query executed. For example, if a user calls
	 * {@link executeQuery(String) executeQuery("SELECT * FROM `table1`")}, and
	 * then calls {@link insert(TableData) insert()}, {@code getResult()} will
	 * give the result from the SELECT query, since INSERT queries do not select
	 * data.
	 * 
	 * @return
	 * The result TableData.
	 * 
	 * @throws SQLException
	 * If there was a problem converting the result into a TableData.
	 */
	public TableData getResult() throws SQLException;
	
	/**
	 * Gets a single cell's value from the database. This method requires a
	 * default database to be set, and so {@link use() use()} must be called
	 * before this method is.
	 * 
	 * @param column
	 * The name of the column to get the cell from.
	 * 
	 * @param table
	 * The name of the table that the cell is in. This must be a table that
	 * exists on the current database.
	 * 
	 * @param where
	 * A condition that the row containing the cell must fulfill. Only the
	 * first row to fulfill this condition is used, even if there are
	 * multiple rows that fulfill it.
	 * 
	 * @return
	 * The cell's value as a String.
	 * 
	 * @throws SQLException
	 * If the there was a problem with selecting the item.
	 */
	public String selectItem(String column, String table, String where) throws SQLException;
	
	/**
	 * Updates a single cell's value in the database. This method requires a
	 * default database to be set, and so {@link use() use()} must be called
	 * before this method is.
	 * 
	 * @param table
	 * The name of the table that the cell is in. This must be a table that
	 * exists on the current database.
	 * 
	 * @param column
	 * The column whose value is to be updated.
	 * 
	 * @param value
	 * The value to set the cell to.
	 * 
	 * @param where
	 * A condition that the row containing the cell must fulfill. Only the
	 * first row to fulfill this condition is used, even if there are
	 * multiple rows that fulfill it.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem updating the cell.
	 */
	public DatabaseManager updateItem(String table, String column, String value, String where) throws SQLException;
	
	/**
	 * Selects rows of data from a table. All columns are selected. This method
	 * requires a default database to be set, and so {@link use() use()} must be
	 * called before this method is.
	 * 
	 * @param table
	 * The table to select data from. This must be a table that exists on the
	 * current database.
	 * 
	 * @param where
	 * A condition that evaluates to true on all rows selected.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem selecting the data.
	 */
	public DatabaseManager selectRows(String table, String where) throws SQLException;
	
	/**
	 * Calls a stored procedure on a database. This method requires a default
	 * database to be set, and so {@link use() use()} must be called before this
	 * method is.
	 * 
	 * @param proc
	 * The name of the stored procedure.
	 * 
	 * @return
	 * This DatabaseManager.
	 * 
	 * @throws SQLException
	 * If there was a problem calling the stored procedure.
	 */
	public DatabaseManager call(String proc) throws SQLException;
	
	/**
	 * Closes the connection and releases the resources.
	 * 
	 * @return
	 * This DatabaseManager.
	 */
	public DatabaseManager close() throws SQLException;
	
	/**
	 * Gets the number of rows in a table. This method requires a default
	 * database to be set, and so {@link use() use()} must be called before
	 * this method is.
	 * 
	 * @param table
	 * The table to count rows in. This must be a table that exists on the
	 * current database.
	 * 
	 * @param where
	 * A condition that evaluates to true on all rows counted.
	 * 
	 * @return
	 * The number of rows.
	 * 
	 * @throws SQLException
	 * If there is a problem with getting the row count.
	 */
	public int count(String table, String where) throws SQLException;
	
	/**
	 * Gets the number of non-null rows in a table. This method requires a
	 * default database to be set, and so {@link use() use()} must be called
	 * before this method is.
	 * 
	 * @param column
	 * The column to count. Only rows that contain a value in this column will
	 * be counted.
	 * 
	 * @param table
	 * The table to count rows in. This must be a table that exists on the
	 * current database.
	 * 
	 * @param where
	 * A condition that evaluates to true on all rows counted.
	 * 
	 * @return
	 * The number of rows.
	 * 
	 * @throws SQLException
	 * If there is a problem with getting the row count.
	 */
	public int count(String column, String table, String where) throws SQLException;
}
