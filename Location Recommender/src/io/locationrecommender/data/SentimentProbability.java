package io.locationrecommender.data;

public class SentimentProbability {
	private float positive;
	private float negative;
	
	public SentimentProbability(float positive, float negative) {
		super();
		this.positive = positive;
		this.negative = negative;
	}
	public float getPositive() {
		return positive;
	}
	public float getNegative() {
		return negative;
	}
}
