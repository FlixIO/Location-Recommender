package io.locationrecommender.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.locationrecommender.data.Location;
import io.locationrecommender.data.UserProfile;
import io.locationrecommender.data.WebJSONReview;
import io.locationrecommender.persist.WebDataAccesser;

public class DataHandler {
	private int numUsers;
	private SentimentAnalyzer sAnalyzer;
	private Map<String, UserProfile> activeUsers = new HashMap<>();
	public static ExecutorService threadPool = Executors.newFixedThreadPool(16);
	private Set<Future<WebDataAccesser>> dataTasks = new HashSet<>();
	private Map<Location, WebJSONReview[]> loadedData = new HashMap<>();
	private Set<String> inUseUserReviews = new HashSet<>();
	private boolean init = false;
	
	public DataHandler() {};
	
	public DataHandler(int numUsers) {
		this.numUsers = numUsers;
		for (int i = 0; i < Location.values().length; i++) {
			dataTasks.add(threadPool.submit(new WebDataAccesser(Location.values()[i])));
		}
		while(!init) {
			boolean active = false;
			for (Future<WebDataAccesser> entry : dataTasks) {
				if (entry.isDone()) {
					try {
						loadedData.put(entry.get().getLocation(), entry.get().getData());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else {
					active = true;
				}
			}
			if (!active) {
				init = true;
				break;
			}
		}
		this.sAnalyzer = new SentimentAnalyzer();
	}
	
	public void addUser(UserProfile p) {
		activeUsers.put(p.getName(), p);
	}
	
	public void addInUseReview(String s) {
		inUseUserReviews.add(s);
	}
	
	public Map<Location, WebJSONReview[]> getReviewDataMap() {
		return loadedData;
	}
	
	public Map<String, UserProfile> getActiveUsers() {
		return activeUsers;
	}
	
	public Set<String> getInUseReviews() {
		return inUseUserReviews;
	}
	
	public SentimentAnalyzer getSentimentAnalyzer() {
		return sAnalyzer;
	}
}
