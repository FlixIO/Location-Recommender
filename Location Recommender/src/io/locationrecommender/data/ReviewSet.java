package io.locationrecommender.data;

import java.util.Map;
import java.util.Set;

public class ReviewSet {
	private Map<String, Polarity> reviewData;
	private Set<String> ignoredWords;
	
	public Map<String, Polarity> getReviewDataMap() {
		return reviewData;
	}
	
	public Set<String> getIgnoredWordSet() {
		return ignoredWords;
	}
	
	public void setIgnoredWordSet(Set<String> set) {
		this.ignoredWords = set;
	}
	
	public void setReviewDataMap(Map<String, Polarity> map) {
		this.reviewData = map;
	}
}
