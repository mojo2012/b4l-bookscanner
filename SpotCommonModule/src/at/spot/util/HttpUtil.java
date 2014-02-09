package at.spot.util;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpUtil {
	public enum RequestType {
		Post,
		Get,
		Put,
		Delete,
		Head
	}
	
	public static String request(RequestType type, URL url, Map<String, String> params, String username, String password) throws Exception {
		String ret = null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		
		if (params != null) {
			for (String k : params.keySet()) {
				String v = params.get(k);
			
				urlParameters.add(new BasicNameValuePair(k, v));
			}
		}
			
		HttpUriRequest request = null;
		
		if (type == RequestType.Post) {
			HttpPost r = new HttpPost(url.toExternalForm());
			
			if (params != null)
				r.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			request = r;
		} if (type == RequestType.Put) {
			HttpPut r = new HttpPut(url.toExternalForm());
			
			if (params != null)
			r.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			request = r;
		} if (type == RequestType.Delete) {
			HttpDelete r = new HttpDelete(url.toExternalForm());
			
			request = r;
		} if (type == RequestType.Head) {
			HttpHead r = new HttpHead(url.toExternalForm());
						
			request = r;
		} else {
			HttpGet r = new HttpGet(url.toExternalForm());
//			r.setp
			
//			r.setEntity(new UrlEncodedFormEntity(urlParameters));
			
			request = r;
		}
		
		
		if (StringUtil.check(username) && StringUtil.check(password)) {
			HttpContext context = new BasicHttpContext();
			Credentials credentials = new UsernamePasswordCredentials(username, password);
//			httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
			
			request.addHeader(new BasicScheme().authenticate(credentials, request, context));
		}
		
			 	
		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();
		
		if (response.getStatusLine().getStatusCode() == 200 && entity != null) {
//			long len = entity.getContentLength();
			InputStream inputStream = entity.getContent();

			ret = StringUtil.toString(inputStream);
		} else {
			String msg = String.format("Could not download url content. (Error %s)", response != null ? response.getStatusLine().getStatusCode() : "<null>");
			throw new Exception(msg);
		}
		
		return ret;
	}
}