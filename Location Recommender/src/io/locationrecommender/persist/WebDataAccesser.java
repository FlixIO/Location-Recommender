package io.locationrecommender.persist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.text.WordUtils;

import com.google.gson.Gson;

import io.locationrecommender.data.Location;
import io.locationrecommender.data.WebJSONReview;

public class WebDataAccesser implements Callable<WebDataAccesser> {
	private Gson gsonHandler = new Gson();
	private URL search;
	private Location location;
	private WebJSONReview[] data;
	
	public WebDataAccesser(Location loc) {
		this.location = loc;
	}
	
	@Override
	public WebDataAccesser call() {
		try {
			long init = System.currentTimeMillis();
			search = new URL("http://tour-pedia.org/api/getReviews?location=" + WordUtils.capitalize(location.toString().toLowerCase()) + "&language=en&category=attraction");
			BufferedReader in = new BufferedReader(new InputStreamReader(search.openStream()));
			data = gsonHandler.fromJson(in.readLine(), WebJSONReview[].class);
			System.out.println(location + " data loaded in [" + (System.currentTimeMillis() - init) + "ms]");
			in.close();
			return this;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public WebJSONReview[] getData() {
		return data;
	}

}
