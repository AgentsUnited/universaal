/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL Relay

Agents United universAAL Relay is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL Relay is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United universAAL Relay.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.hbaf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class HBAFClient {
    private static final String CHAR_CRLF = "\r\n";
    private static final String CHAR_HYPHYP = "--";
    private static final String CHAR_BOUNDS = "*****";
    private static final String HEADER_COOKIES = "Set-Cookie";
    public static final String LOGIN_USR = "m.h.h.weusthof@utwente.nl";
    public static final String LOGIN_PWD = "marcel@couch";
    public static final String FILE_WEIGHT="weightfile";
    public static final String FILE_BP="BloodpressureFile";
    public static final String URL_BASE = System.getProperty("eu.councilofcoaches.uaal.hbaf.url", "https://couch.utwente.nl");
    public static final String URL_WEIGHT = URL_BASE+"/upload_weightscale_data";
    public static final String URL_BP = URL_BASE+"/upload_bloodpressure_data";
    public static final String URL_WEIGHT_NEW = URL_BASE+"/upload_weightscale_data_new";
    public static final String URL_BP_NEW = URL_BASE+"/upload_bloodpressure_data_new";
    public static final String URL_LOGIN = URL_BASE+"/login";
//  private static CookieManager cookies = new CookieManager();
    private static DecodedJWT jwt = null;
    
    public static String postFile(String url, String body, String filename)
	    throws Exception {
	HttpURLConnection conn = null;
	try {
	    // Build body
	    byte[] data = null;
	    if (body != null) {
		data = body.getBytes(Charset.forName("US-ASCII"));
	    }
	    // Make connection
	    URL server = new URL(url);
	    conn = (HttpURLConnection) server.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setInstanceFollowRedirects(false);
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setReadTimeout(30000);
	    conn.setRequestProperty("Connection", "Keep-Alive");
	    conn.setRequestProperty("Cache-Control", "no-cache");
	    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + CHAR_BOUNDS);
	    conn.setRequestProperty("charset", "us-ascii");
	    if (data != null) {
		conn.setRequestProperty("Content-Length",
			"" + Integer.toString(data.length));
	    }
	    // Send request to the connection
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    // delimiter - content desc - delimiter
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_CRLF);
	    wr.writeBytes("Content-Disposition: form-data; name=\""
		    + filename + "\";filename=\"uaaldatafile.json\"" + CHAR_CRLF);
	    // delimiter
	    wr.writeBytes(CHAR_CRLF);
	    // data
	    if (data != null) {
		wr.write(data);
	    }
	    // delimiter
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_HYPHYP + CHAR_CRLF);
	    // end
	    wr.flush();
	    wr.close();
	    // Get response from the connection
	    BufferedReader rd = new BufferedReader(
		    new InputStreamReader(conn.getInputStream(), "US-ASCII"));
	    String line = rd.readLine();
	    StringBuilder result = new StringBuilder();
	    while (line != null) {
		result.append(line);
		line = rd.readLine();
	    }
	    if (!result.toString().isEmpty()) {
		return result.toString();
	    }
	} finally {
	    // close the connection and set all objects to null
	    if (conn != null) {
		conn.disconnect();
	    }
	}
	return null;
    }

    public static void postLogin(String usr, String pwd) throws Exception {
	HttpURLConnection conn = null;
	try {
	    // Build body
	    String body = "email=" + usr + "&password=" + pwd;
	    byte[] data = null;
	    data = body.getBytes(Charset.forName("US-ASCII")); //TODO UTF-8 ?
	    // Make connection
	    URL server = new URL(URL_LOGIN);
	    conn = (HttpURLConnection) server.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setInstanceFollowRedirects(false);
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setReadTimeout(30000);
//	    conn.setRequestProperty("Connection", "Keep-Alive");
	    conn.setRequestProperty("Cache-Control", "no-cache");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("charset", "us-ascii"); //TODO UTF-8 ?
	    if (data != null) {
		conn.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
	    }
	    // Send request to the connection
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    if (data != null) {
		wr.write(data);
	    }
	    wr.flush();
	    wr.close();
	    // Get response from the connection  
	    Map<String, List<String>> headers = conn.getHeaderFields();
	    List<String> cookies = headers.get(HEADER_COOKIES);
	    if (cookies != null) {
		for (String cookie : cookies) {
//		    cookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0)); // and skip rest
		    List<HttpCookie> subcookies = HttpCookie.parse(cookie);
		    if (cookies != null) {
			for (HttpCookie subcookie : subcookies) {
			    if(subcookie.getName().equals("couch_cookie_user")){
				jwt = JWT.decode(subcookie.getValue());
			    }
			}
		    }
		}
	    }
	} finally {
	    // close the connection and set all objects to null
	    if (conn != null) {
		conn.disconnect();
	    }
	}
    }
    
    public static String postFileSecure(String url, String body, String name, String user, String pass)
	    throws Exception {
	// Check if token is valid...
	if(jwt==null || jwt.getExpiresAt().before(Date.from(Instant.now()))){
	    postLogin(user, pass); // Expired or not exists. Get new token...
	}
	if (jwt==null) throw new Exception("Could not get auth token");
	// ...And prepare the connection
	HttpURLConnection conn = null;
	try {
	    // Build body
	    byte[] data = null;
	    if (body != null) {
		data = body.getBytes(Charset.forName("US-ASCII"));
	    }
	    // Make connection
	    URL server = new URL(url);
	    conn = (HttpURLConnection) server.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setInstanceFollowRedirects(false);
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setReadTimeout(30000);
	    conn.setRequestProperty("Connection", "Keep-Alive");
	    conn.setRequestProperty("Cache-Control", "no-cache");
	    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + CHAR_BOUNDS);
	    conn.setRequestProperty("charset", "us-ascii");
	    conn.setRequestProperty("Authorization", "Bearer " + jwt.getToken());
	    if (data != null) {
		conn.setRequestProperty("Content-Length",
			"" + Integer.toString(data.length));
	    }
	    // Send request to the connection
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    // delimiter - content desc - delimiter
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_CRLF);
	    wr.writeBytes("Content-Disposition: form-data; name=\""
		    + name + "\";filename=\"uaaldatafile.json\"" + CHAR_CRLF);
	    // delimiter
	    wr.writeBytes(CHAR_CRLF);
	    // data
	    if (data != null) {
		wr.write(data);
	    }
	    // delimiter
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_HYPHYP + CHAR_CRLF);
	    // end
	    wr.flush();
	    wr.close();
	    // Get response from the connection
	    BufferedReader rd = new BufferedReader(
		    new InputStreamReader(conn.getInputStream(), "US-ASCII"));
	    String line = rd.readLine();
	    StringBuilder result = new StringBuilder();
	    while (line != null) {
		result.append(line);
		line = rd.readLine();
	    }
	    if (!result.toString().isEmpty()) {
		return result.toString();
	    }
	} finally {
	    // close the connection and set all objects to null
	    if (conn != null) {
		conn.disconnect();
	    }
	}
	return null;
    }
    
    public static String postLoginAndFileSecure(String url, String body, String name, String user, String pass)
	    throws Exception {
	HttpURLConnection conn = null;
	try {
	    // Check auth
	    if (user == null || pass == null){
		throw new SecurityException("Credentials not set");
	    }
	    // Build body
	    byte[] data = null;
	    if (body != null) {
		data = body.getBytes(Charset.forName("US-ASCII"));
	    }
	    // Make connection
	    URL server = new URL(url);
	    conn = (HttpURLConnection) server.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setInstanceFollowRedirects(false);
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setReadTimeout(30000);
	    conn.setRequestProperty("Connection", "Keep-Alive");
	    conn.setRequestProperty("Cache-Control", "no-cache");
	    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + CHAR_BOUNDS);
	    conn.setRequestProperty("charset", "us-ascii");
	    if (data != null) {
		conn.setRequestProperty("Content-Length",
			"" + Integer.toString(data.length));
	    }
	    // Send request to the connection
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    // === FILE ===
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_CRLF);
	    wr.writeBytes("Content-Disposition: form-data; name=\""
		    + name + "\";filename=\"uaaldatafile.json\"" + CHAR_CRLF);
	    wr.writeBytes(CHAR_CRLF);
	    if (data != null) {
		wr.write(data);
	    }
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_HYPHYP + CHAR_CRLF);
	    // === EMAIL ===
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_CRLF);
	    wr.writeBytes("Content-Disposition: form-data; name=\"email\"" + CHAR_CRLF);
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(user); // <- EMAIL
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_HYPHYP + CHAR_CRLF);
	    // === PASSWORD ===
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_CRLF);
	    wr.writeBytes("Content-Disposition: form-data; name=\"password\"" + CHAR_CRLF);
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(pass); // <- PASSWORD
	    wr.writeBytes(CHAR_CRLF);
	    wr.writeBytes(CHAR_HYPHYP + CHAR_BOUNDS + CHAR_HYPHYP + CHAR_CRLF);
	    // === END ===
	    wr.flush();
	    wr.close();
	    // Get JWT token
	    Map<String, List<String>> headers = conn.getHeaderFields();
	    List<String> cookies = headers.get(HEADER_COOKIES);
	    if (cookies != null) {
		for (String cookie : cookies) {
		    List<HttpCookie> subcookies = HttpCookie.parse(cookie);
		    if (cookies != null) {
			for (HttpCookie subcookie : subcookies) {
			    if(subcookie.getName().equals("couch_cookie_user")){
				jwt = JWT.decode(subcookie.getValue());
			    }
			}
		    }
		}
	    }
	    // Get response from the connection
	    BufferedReader rd = new BufferedReader(
		    new InputStreamReader(conn.getInputStream(), "US-ASCII"));
	    String line = rd.readLine();
	    StringBuilder result = new StringBuilder();
	    while (line != null) {
		result.append(line);
		line = rd.readLine();
	    }
	    if (!result.toString().isEmpty()) {
		return result.toString();
	    }
	} finally {
	    // close the connection and set all objects to null
	    if (conn != null) {
		conn.disconnect();
	    }
	}
	return null;
    }
    
    /*
    public static String postBody(String url, String usr, String pwd, String body) throws Exception {
	HttpURLConnection conn = null;
	try {
	    // Build body
	    byte[] data = null;
	    if (body != null) {
		data = body.getBytes(Charset.forName("UTF-8"));
	    }
	    // Make connection
	    URL server = new URL(url);
	    conn = (HttpURLConnection) server.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setInstanceFollowRedirects(false);
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setReadTimeout(30000);
	    conn.setRequestProperty("Content-Type", "application/json");
	    conn.setRequestProperty("charset", "utf-8");
	    if (data != null) {
		conn.setRequestProperty("Content-Length",
			"" + Integer.toString(data.length));
	    }
	    // Send request to the connection
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    if (data != null) {
		wr.write(data);
	    }
	    wr.flush();
	    wr.close();
	    // Get response from the connection
	    BufferedReader rd = new BufferedReader(
		    new InputStreamReader(conn.getInputStream(), "UTF-8"));
	    String line = rd.readLine();
	    StringBuilder result = new StringBuilder();
	    while (line != null) {
		result.append(line);
		line = rd.readLine();
	    }
	    if (!result.toString().isEmpty()) {
		return result.toString();
	    }
	} finally {
	    // close the connection and set all objects to null
	    if (conn != null) {
		conn.disconnect();
	    }
	}
	return null;
    }
    */
}
