package net.fourbytes.shadow.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

public final class HTTPClient {
	
	private HTTPClient() {
	}
	
	/**
	 * Requests and sends data to URL 
	 * @param targetURL URL to request data from / send data to
	 * @param requestMethod POST, GET, ...
	 * @param type Content-Type, default (when null or empty) is application/x-www-form-urlencoded
	 * @param data Data to send
	 * @param type Application type, leave null or "" to use default 
	 * @return Data received
	 * @throws IOException 
	 */
	public static String execute(String targetURL, String requestMethod, String type, String data) throws IOException {
		targetURL = targetURL.replace("https://", "http://"); //Force ignoring security checks
		
		HttpURLConnection con = null;
		String str = "";
		
		DataOutputStream wr = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			URL url = new URL(targetURL);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(requestMethod);
			con.setRequestProperty("Content-Type", (type == null || type.isEmpty())?"application/x-www-form-urlencoded":type);
			
			con.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");
			
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			
			con.connect();
			
		    wr = new DataOutputStream(con.getOutputStream());
		    wr.writeBytes(data);
		    wr.flush();
		    wr.close();
		    
		    is = con.getInputStream();
		    rd = new BufferedReader(new InputStreamReader(is));
		    
		    StringBuffer response = new StringBuffer();
		    String line;
		    while ((line = rd.readLine()) != null) {
		        response.append(line);
		        response.append('\r');
		    }
		    rd.close();
		    
		    str = response.toString();
		    
		    con.disconnect();
		} catch (IOException e) {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    	try {
	    		if (wr != null) {
	    			wr.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
	    	try {
	    		if (is != null) {
	    			is.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
	    	try {
	    		if (rd != null) {
	    			rd.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
			throw e;
		}
		
		return str;
	}
	
	/**
	 * Requests and sends data to URL via HTTPS
	 * @param targetURL URL to request data from / send data to
	 * @param requestMethod POST, GET, ...
	 * @param type Content-Type, default (when null or empty) is application/x-www-form-urlencoded
	 * @param data Data to send
	 * @param type Application type, leave null or "" to use default 
	 * @param key SSH Key input stream
	 * @return Data received 
	 * @throws IOException 
	 */
	public static String executeSecure(String targetURL, String requestMethod, String type, String data, InputStream key) throws IOException {
		targetURL = targetURL.replace("http://", "https://"); //Force security checks
		
		HttpsURLConnection con = null;
		String str = "";
		
		DataOutputStream wr = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			URL url = new URL(targetURL);
			con = (HttpsURLConnection) url.openConnection();
			
			con.setRequestMethod(requestMethod);
			con.setRequestProperty("Content-Type", (type == null || type.isEmpty())?"application/x-www-form-urlencoded":type);
			
			con.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");
			
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			
			con.connect();
			
			DataInputStream dis = new DataInputStream(key);
			byte[] bytes = new byte[dis.available()];
			dis.readFully(bytes);
			dis.close();
			
			Certificate c = con.getServerCertificates()[0];
			PublicKey pk = c.getPublicKey();
			byte[] pkdata = pk.getEncoded();
			
			for (int i = 0; i < pkdata.length; i++) {
				if (pkdata[i] == bytes[i]) continue;
				throw new IOException("Public key mismatch");
			}
			
		    wr = new DataOutputStream(con.getOutputStream());
		    wr.writeBytes(data);
		    wr.flush();
		    wr.close();
		    
		    is = con.getInputStream();
		    rd = new BufferedReader(new InputStreamReader(is));
		    
		    StringBuffer response = new StringBuffer();
		    String line;
		    while ((line = rd.readLine()) != null) {
		        response.append(line);
		        response.append('\r');
		    }
		    rd.close();
		    
		    str = response.toString();
		    
		    con.disconnect();
		} catch (IOException e) {
	    	if (con != null) {
	    		con.disconnect();
	    	}
	    	try {
	    		if (wr != null) {
	    			wr.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
	    	try {
	    		if (is != null) {
	    			is.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
	    	try {
	    		if (rd != null) {
	    			rd.close();
	    		}
	    	} catch (IOException e1) {
	    		e1.printStackTrace();
	    	}
			throw e;
		}
		
		return str;
	}
	
}
