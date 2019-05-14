package io.locationrecommender.data;

public class WebJSONReview {
	public String language;
    public String polarity;
    public String rating;
    public String source;
    public String text;
    public String time;
    public String wordsCount;
    public String details;
    
    public String getText() {
    	return text.toLowerCase();
    }
}
