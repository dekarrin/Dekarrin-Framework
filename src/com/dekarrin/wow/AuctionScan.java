package com.dekarrin.wow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Security;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.dekarrin.program.ConsoleProgram;

/**
 * Grabs data from the WOW auction house.
 * @author Michael Nelson
 *
 */
public class AuctionScan extends ConsoleProgram {
	
	private class AuctionData {
		public String name;
		public String owner;
		public int count;
		public int level;
		public AuctionTime time;
		public int bid;
		public int buyout;
	}
	
	private class LoginData {
		public String username;
		public String password;
	}
	
	private AuctionData[] auctionData;
	
	private HttpClient client;
	
	private static final String BLIZZARD_HOST = "us.battle.net";
	private static final String LOGIN_PATH = "/login/en/login.frag";
	
	public static void main(String[] args) {
		new AuctionScan(args);
	}
	
	public AuctionScan(String[] args) {
		super(args);
		setupClient();
		downloadAuctionData();
		processAuctionData();
	}
	
	/**
	 * Sets the HTTP Client information.
	 */
	private void setupClient() {
		client = new DefaultHttpClient();
		Scheme tls = new Scheme("https", 443, SSLSocketFactory.getSocketFactory());
		client.getConnectionManager().getSchemeRegistry().register(tls);
	}
	
	/**
	 * Gets the auction house data.
	 */
	private void downloadAuctionData() {
		LoginData loginData = getLoginInfo();
		loginToBlizzard(loginData);
	//	String home = downloadPage(BLIZZARD_HOST);
		//System.out.print(home);
//		String[] rawAuctionData = getAuctionPages();
	//	auctionData = processRawData(rawAuctionData);
	}
	
	/**
	 * Saves the information.
	 */
	private void processAuctionData() {
//		for(AuctionData ad: actionData) {
	//		saveAuctionData(ad);
		}
//	}
	
	private LoginData getLoginInfo() {
		LoginData userData = new LoginData();
		ui.println("Your battle.net information is needed to access the auctionhouse.");
		ui.println("It is only used to access the website, and is not saved.");
		userData.username = ui.read("Email:");
		userData.password = ui.readPassword("Password:");
		return userData;
	}
	
	private void loginToBlizzard(LoginData loginData) {
		// Spoof a complete browser interaction for now.
		downloadPage("http://" + BLIZZARD_HOST + "/en/");
		downloadPage("https://" + BLIZZARD_HOST + LOGIN_PATH);
		String response = sendSecureForm(BLIZZARD_HOST + LOGIN_PATH,
				"accountName", loginData.username,
				"app", "com-sc2",
				"password", loginData.password,
				"persistLogin", "on"
		);
		System.out.println(response);
	}
	
	/**
	 * Sends a form through SSL/TLS.
	 * 
	 * @param host
	 * The host to send the form to.
	 * 
	 * @param formData
	 * A list of form names and values. Each name is given before
	 * its associated value. This must be an even number of entries.
	 */
	private String sendSecureForm(String host, String... formData) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		for(int i = 0; i < formData.length; i += 2) {
			formParams.add(new BasicNameValuePair(formData[i], formData[i+1]));
		}
		UrlEncodedFormEntity ent = null;
		try {
			ent = new UrlEncodedFormEntity(formParams, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			giveFatalError("Bad encoding in query");
		}
		HttpPost post = new HttpPost("https://" + host);
		post.setEntity(ent);
		return executeTextRequest(post);
	}
	
	/**
	 * Executes a request and returns the response as a String.
	 * 
	 * @param request
	 * The request to execute.
	 * 
	 * @return
	 * The response as a String.
	 */
	private String executeTextRequest(HttpUriRequest request) {
		byte[] response = executeRequest(request);
		String responseAsString = new String(response);
		return responseAsString;
	}
	
	/**
	 * Executes a request and returns the response as an array
	 * of bytes.
	 * 
	 * @param request
	 * The request to execute.
	 * 
	 * @return
	 * The response contents.
	 */
	private byte[] executeRequest(HttpUriRequest request) {
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			giveFatalError("Bad request");
		} catch (IOException e) {
			e.printStackTrace();
			giveFatalError(e.getMessage());
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
				giveFatalError("Bad IO!");
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					giveFatalError("Can't close response!");
				}
			}
			responseContent = buffer.toByteArray();
		}
		return responseContent;
	}
	
	/**
	 * Downloads a text document from a url.
	 * 
	 * @param location
	 * The location of the text document to download.
	 * 
	 * @return
	 * The document as a String.
	 */
	private String downloadPage(String location) {
		HttpGet get = new HttpGet(location);
		return executeTextRequest(get);
	}
}