package com.ese2013.mub.map.util;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;

import com.ese2013.mub.map.MapFragment;
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
		List<LatLng> path = null;
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

	private static PolylineOptions createPolyLine(List<LatLng> path) {
		PolylineOptions lineOptions = new PolylineOptions();
		lineOptions.addAll(path);
		lineOptions.width(5);
		lineOptions.color(Color.RED);
		return lineOptions;
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