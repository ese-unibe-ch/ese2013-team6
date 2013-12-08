package com.ese2013.mub.map.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

/**
 * Allows to parse a Google Maps JSONObject to a list of LatLng (geographical
 * points).
 * 
 * Created after tutorial on:
 * http://wptrafficanalyzer.in/blog/drawing-driving-route
 * -directions-between-two-
 * locations-using-google-directions-in-google-map-android-api-v2/
 */
public class DirectionsJSONParser {

	/**
	 * Receives a JSONObject and returns a list of lists containing latitude and
	 * longitude
	 */
	public List<LatLng> parse(JSONObject jObject) {
		List<LatLng> path = new ArrayList<LatLng>();
		try {
			JSONArray jRoutes = jObject.getJSONArray("routes");
			// We only need 1 route
			JSONArray jLegs = jRoutes.getJSONObject(0).getJSONArray("legs");
			for (int j = 0; j < jLegs.length(); j++) {
				JSONArray jSteps = jLegs.getJSONObject(j).getJSONArray("steps");
				for (int k = 0; k < jSteps.length(); k++) {
					String polyline = jSteps.getJSONObject(k).getJSONObject("polyline").getString("points");
					List<LatLng> list = decodePoly(polyline);
					path.addAll(list);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * Method to decode polyline points Courtesy :
	 * http://jeffreysambells.com/2010
	 * /05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
			poly.add(p);
		}
		return poly;
	}
}