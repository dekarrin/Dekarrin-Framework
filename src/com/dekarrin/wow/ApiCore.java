package com.dekarrin.wow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dekarrin.db.DatabaseManager;
import com.dekarrin.db.MySqlEngine;
import com.dekarrin.error.TrafficException;
import com.dekarrin.program.FatalErrorException;

/**
 * Handles API and database connectivity for programs using the WOW API.
 */
public class ApiCore {
	
	/**
	 * The host of the Blizzard server.
	 */
	private static final String BLIZZARD_HOST = "us.battle.net";
	
	/**
	 * The maximum number of requests allowed per day.
	 */
	private int maximumRequests;
	
	/**
	 * The number of requests made to the WOW server today.
	 */
	private int requestCount;
	
	/**
	 * The database module.
	 */
	public DatabaseManager db = new MySqlEngine();
	
	private HttpClient client = new DefaultHttpClient();
	
	public ApiCore() throws SQLException {
		setupDatabase();
		loadSettings();
	}
	
	public void close() throws SQLException {
		saveSettings();
		db.close();
	}
	
	/**
	 * Generates an HTTP GET request.
	 * 
	 * @param location
	 * The location to connect to.
	 * 
	 * @param addHost
	 * Whether the host should be automatically included.
	 * 
	 * @throws TrafficException
	 * If the maximum number of requests is exceeded.
	 * 
	 * @throws FatalErrorException
	 */
	public String getRequest(String location, boolean addHost) throws TrafficException, FatalErrorException {
		if(requestLimitReached()) {
			throw new TrafficException("Request limit reached.");
		}
		requestCount++;
		String urlLocation = "";
		if(addHost) {
			urlLocation += "http://"+BLIZZARD_HOST;
		}
		urlLocation += location;
		HttpGet request = new HttpGet(urlLocation);
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new FatalErrorException("Bad request");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FatalErrorException(e.getMessage());
		}
		byte[] responseContent = null;
		HttpEntity ent = response.getEntity();
		if(ent != null) {
			InputStream is = null;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				is = ent.getContent();
				int next;
				while((next = is.read()) != -1) {
					buffer.write(next);
				}
				buffer.flush();
			} catch(IOException e) {
				throw new FatalErrorException("Bad IO!");
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					throw new FatalErrorException("Can't close response!");
				}
			}
			responseContent = buffer.toByteArray();
		}
		return new String(responseContent);
	}
	
	/**
	 * Starts up the database connection.
	 * @throws SQLException 
	 */
	private void setupDatabase() throws SQLException {
		db.open("localhost", 3306, "wow", "greeneconomyapple");
		db.use("auctionscan");
	}
	
	/**
	 * Loads the settings necessary to connect to the WOW API.
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 */
	private void loadSettings() throws NumberFormatException, SQLException {
		maximumRequests = Integer.parseInt(getSetting("maximum_requests"));
		requestCount = Integer.parseInt(getSetting("requests"));
	}
	
	/**
	 * Writes the state to the database.
	 * @throws SQLException 
	 */
	private void saveSettings() throws SQLException {
		setSetting("requests", Integer.toString(requestCount));
	}
	
	/**
	 * Sets a setting in the table.
	 */
	public void setSetting(String key, String value) throws SQLException {
		db.updateItem("settings", "value", value, "`key`='"+key+"'");
	}
	
	/**
	 * Gets a setting from the db.
	 */
	public String getSetting(String key) throws SQLException {
		return db.selectItem("value", "settings", "`key`='"+key+"'");
	}
	
	/**
	 * Checks if the maximum number of requests has already been made for
	 * the day.
	 * 
	 * @return
	 * Whether the maximum number of requests have been made.
	 */
	private boolean requestLimitReached() {
		return (requestCount >= maximumRequests);
	}
}