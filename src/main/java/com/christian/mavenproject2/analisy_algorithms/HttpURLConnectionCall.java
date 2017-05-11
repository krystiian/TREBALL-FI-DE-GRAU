package com.christian.mavenproject2.analisy_algorithms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpURLConnectionCall {
		private final String USER_AGENT = "Mozilla/5.0";

		// HTTP GET request
		public static String main(String args) throws Exception {

			HttpURLConnectionCall http = new HttpURLConnectionCall();

			System.out.println("Testing 1 - Send Http GET request");
			return http.sendGet(args);


		}
		
		private String sendGet(String url) throws Exception {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			request.addHeader("User-Agent", USER_AGENT);

			HttpResponse response = client.execute(request);

			int response_code = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(
	                       new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			return result.toString();
		}

	}