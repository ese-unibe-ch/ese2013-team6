package com.ese2013.mub.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;

import com.ese2013.mub.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Asynchronously downloads and parses the directions from some point to another
 * using Google Maps.
 */
public class DirectionsDownloadTask extends AsyncTask<Void, Void, Void> {
	private MapFragment mapFragment;
	private PolylineOptions polyline;
	private String url;

	public DirectionsDownloadTask(LatLng origin, LatLng dest, String transportMode, MapFragment mapFragment) {
		this.mapFragment = mapFragment;
		this.url = getDirectionsUrl(origin, dest, transportMode);
	}

	@Override
	protected Void doInBackground(Void... args) {
		List<HashMap<String, String>> path = null;
		try {
			JSONObject data = new JsonDataRequest(url).execute();
			DirectionsJSONParser parser = new DirectionsJSONParser();
			path = parser.parse(data);
			polyline = createPolyLine(path);
		} catch (IOException e) {
			polyline = null;
		}
		return null;
	}

	public PolylineOptions getPolyline() {
		return polyline;
	}

	public boolean wasSuccesful() {
		return polyline != null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mapFragment.onDirectionsDownloadFinished(this);
	}

	private static PolylineOptions createPolyLine(List<HashMap<String, String>> path) {
		PolylineOptions lineOptions = new PolylineOptions();
		List<LatLng> points = getPointsInPath(path);

		lineOptions.addAll(points);
		lineOptions.width(5);
		lineOptions.color(Color.RED);

		return lineOptions;
	}

	private static List<LatLng> getPointsInPath(List<HashMap<String, String>> path) {
		List<LatLng> points = new ArrayList<LatLng>();
		for (int j = 0; j < path.size(); j++) {
			HashMap<String, String> point = path.get(j);
			double lat = Double.parseDouble(point.get("lat"));
			double lng = Double.parseDouble(point.get("lng"));
			LatLng position = new LatLng(lat, lng);
			points.add(position);
		}
		return points;
	}

	private static String getDirectionsUrl(LatLng origin, LatLng dest, String transportMode) {
		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		String sensor = "sensor=false";
		String mode = "mode=" + transportMode;
		String webServiceParams = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
		String outputFormat = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/" + outputFormat + "?" + webServiceParams;
		return url;
	}
}