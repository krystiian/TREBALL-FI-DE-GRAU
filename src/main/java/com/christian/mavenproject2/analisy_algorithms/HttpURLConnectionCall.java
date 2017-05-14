package com.christian.mavenproject2.analisy_algorithms;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpURLConnectionCall {
	private final String USER_AGENT = "Mozilla/5.0";

	// HTTP GET request
	public static String main(String args) throws Exception {

		HttpURLConnectionCall http = new HttpURLConnectionCall();
		return http.sendGet(args);
	}

	private String sendGet(String url) throws Exception {

		HttpClient client = new DefaultHttpClient();
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
		HttpGet request = new HttpGet(url);

		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		int response_code = response.getStatusLine().getStatusCode();

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		System.out.println(result.toString());
		return result.toString();
	}
}