package at.spot.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public class HttpUtil {
	public enum RequestType {
		Post,
		Get,
		Put,
		Delete,
		Head
	}
	
	public static String request(RequestType type, URL url, Map<String, String> headerParams, Map<String, String> params, String username, 
			String password) throws Exception {
		
		return request(type, url, params, null, username, password);
	}
	
	public static String request(RequestType type, URL url, Map<String, String> headerParams, Map<String, String> params, String content, 
			String username, String password) throws Exception {
		
		String ret = null;
		
		HttpURLConnection connection = null;
		OutputStream os = null;
		BufferedWriter writer  = null;
		
		if (type == RequestType.Get && params != null) {
			connection = getConnection(new URL(url, getQueryString(params)), username, password, headerParams);
			
			connection.setRequestMethod("GET");
		} else {
			connection = getConnection(url, username, password, headerParams);

			os = connection.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQueryString(params));
		}
		
		if (StringUtil.check(content)) {
			writer.write(content);
		}
		
		writer.flush();
		
		InputStream response = null;
		
		if (connection.getResponseCode() >= 400) {
			response = connection.getErrorStream();
		} else {
			response = connection.getInputStream();
		}
		
		ret = StringUtil.toString(response);
		
		return ret;
	}
	
	protected static String getQueryString(Map<String, String> params) throws UnsupportedEncodingException {
	    if (params == null)
	    	return "";
		
		StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (String k : params.keySet()) {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(k, "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(params.get(k), "UTF-8"));
	    }

	    return result.toString();
	}
	
	protected static HttpURLConnection getConnection(URL url, String username, String password, Map<String, String> headerParams) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		if (StringUtil.check(username) && StringUtil.check(password)) {
			String userpass = username + ":" + password;
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			connection.setRequestProperty ("Authorization", basicAuth);
		}

		if (headerParams != null) {
			for (String k : headerParams.keySet()) {
				String v = headerParams.get(k);
				
				connection.setRequestProperty(k, v);
			}
		}
		
		return connection;
	}
}