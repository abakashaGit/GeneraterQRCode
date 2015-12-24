package com.vmoksha.mysamplecode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class HTTP_GET extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpURLConnection urlConnection = null;
		String result = "";
		String api_url = params[0];

		try {
			
			URL url = new URL(api_url);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection
					.addRequestProperty("Content-Type", "application/json");
			urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection
					.addRequestProperty("x-access-token", "x-access-token");
			urlConnection.setConnectTimeout(5000);
			
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			result = readStream(in);

		} catch (Exception e) {
			Log.i("HTTP_Post", e.getMessage());

		} finally {
			urlConnection.disconnect();
		}
		return result;
	}

	private static String readStream(InputStream in) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String nextLine = "";
			while ((nextLine = reader.readLine()) != null) {
				sb.append(nextLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
