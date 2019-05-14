package io.locationrecommender.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import io.locationrecommender.main.PL;

public class UserProfile {
	private String name;
	private Map<Location, String> personalReviewMap = new LinkedHashMap<>();
	private Map<Location, Double> generatedRatingMap = new LinkedHashMap<>();
	private Random ran = new Random();
	
	public UserProfile(String name) {
		this.name = name;
		for (int i = 0; i < Location.values().length; i++) {
			WebJSONReview[] data = PL.getDataHandler().getReviewDataMap().get(Location.values()[i]);
			int rng = ran.nextInt(data.length);
			String s = data[rng].getText();
			if (PL.getDataHandler().getInUseReviews().contains(s)) {
				i--;
			} else {
				personalReviewMap.put(Location.values()[i], s);
				PL.getDataHandler().getInUseReviews().add(s);
			}
		}
		for(Map.Entry<Location, String> entry : personalReviewMap.entrySet()) {
			if (entry.getValue() != Double.NaN + "") {
				SentimentProbability sP = PL.getDataHandler().getSentimentAnalyzer().performSentimentAnalysis(entry.getValue());
				generatedRatingMap.put(entry.getKey(), PL.getDataHandler().getSentimentAnalyzer().computeRating(sP));
			} else {
				generatedRatingMap.put(entry.getKey(), Double.NaN);
			}
		}
		System.out.println(name);
		for(Map.Entry<Location, String> entry : personalReviewMap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		for(Map.Entry<Location, Double> entry : generatedRatingMap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println();
	}
	
	public String getName() {
		return this.name;
	}
	
	public Map<Location, String> getPersonalReviewMap() {
		return this.personalReviewMap;
	}
	
	public Map<Location, Double> getPersonalRatingMap() {
		return this.generatedRatingMap;
	}
}
