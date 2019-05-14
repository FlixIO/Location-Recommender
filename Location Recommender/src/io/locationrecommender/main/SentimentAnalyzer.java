package io.locationrecommender.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Future;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import io.locationrecommender.data.Polarity;
import io.locationrecommender.data.SentimentProbability;
import io.locationrecommender.persist.JSONAccesser;

public class SentimentAnalyzer {
	private Map<String, Polarity> reviewMap = new HashMap<String, Polarity>();
	private Set<String> ignoredWords = new HashSet<String>();
	private final Set<String> uniqueWordSet = new HashSet<String>();
	private final List<String> negativeWordSet = new ArrayList<String>();
	private final List<String> positiveWordSet = new ArrayList<String>();
	private Future<Boolean> queryResult;
	private boolean init = false;
	private final float positiveProbability;
	private final float negativeProbability;

	public SentimentAnalyzer() {
		queryResult = DataHandler.threadPool.submit(new JSONAccesser(this));
		while (!init) {
			if (queryResult.isDone()) {
				init = true;
				break;
			}
		}
//		int pos = 0, neg = 0;
//		for(Polarity p : reviewMap.values()) {
//			if (p == Polarity.POSITIVE) {
//				pos++;
//			} else {
//				neg++;
//			}
//		}
//		positiveProbability = (float) pos / reviewMap.size();
//		negativeProbability = (float) neg / reviewMap.size();
//		
//		Set<String> wordTokens = new HashSet<String>();
//		for(Map.Entry<String, Polarity> entry : reviewMap.entrySet()) {
//			Set<String> data = tokenizeString(entry.getKey());
//			if (entry.getValue() == Polarity.POSITIVE) {
//				positiveWordSet.addAll(data);
//			} else {
//				negativeWordSet.addAll(data);
//			}
//			for (String s : data) { //adds to unique list and will also remove duplicates
//				uniqueWordSet.add(s);
//			}
//		}
		Scanner sc;
		try {
			sc = new Scanner(new FileReader(new File("positive.txt")));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				positiveWordSet.add(line);
			}
			sc.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			sc = new Scanner(new FileReader(new File("negative.txt")));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				negativeWordSet.add(line);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		uniqueWordSet.addAll(positiveWordSet);
		uniqueWordSet.addAll(negativeWordSet);
		
		positiveProbability = (float) positiveWordSet.size() / uniqueWordSet.size();
		negativeProbability = (float) negativeWordSet.size() / uniqueWordSet.size();
		
		positiveWordSet.removeAll(ignoredWords);
		negativeWordSet.removeAll(ignoredWords);
		uniqueWordSet.removeAll(ignoredWords);
	}

	public SentimentProbability performSentimentAnalysis(String review) {
		if (init) {
			Set<String> tokenizedString = tokenizeString(review);
			tokenizedString.removeAll(ignoredWords);
			Set<Float> positiveProbabilities = new HashSet<>();
			Set<Float> negativeProbabilities = new HashSet<>();
			for (String s : tokenizedString) {
				positiveProbabilities.add(((float) Collections.frequency(positiveWordSet, s) + 1)/(positiveWordSet.size() + uniqueWordSet.size()));
				negativeProbabilities.add(((float) Collections.frequency(negativeWordSet, s) + 1)/(negativeWordSet.size() + uniqueWordSet.size()));
			}
			return new SentimentProbability(positiveProbability * computeProbabilitySum(positiveProbabilities), negativeProbability * computeProbabilitySum(negativeProbabilities));
		}
		System.out.println("Training is still in progress");
		return null;
	}
	
	private Set<String> tokenizeString(String s) {
		return Sets.newHashSet(Splitter.on(CharMatcher.whitespace()).trimResults(CharMatcher.anyOf(",.!?:;")).split(s.toLowerCase()));
	}
	
	private float computeProbabilitySum(Set<Float> data) {
		float sum = 0;
		for (float f : data) {
			sum += f;
		}
		return sum;
	}
	
	public void setReviewMap(Map<String, Polarity> map) {
		this.reviewMap = map;
	}
	
	public void setIgnoredWords(Set<String> set) {
		this.ignoredWords = set;
	}
	
	public double computeRating(SentimentProbability p) {
		if (p.getPositive() > p.getNegative()) {
			if (p.getPositive() * 1000 - p.getNegative() * 1000 >= 0.061) {
				return 5;
			} else if (p.getPositive() * 1000 - p.getNegative() * 1000 < 0.061 && p.getPositive() * 1000 - p.getNegative() * 1000 >= 0.029) {
				return 4;
			} else {
				return 3;
			}
		} else if (p.getNegative() > p.getPositive()) {
			if (p.getPositive() * 1000 - p.getNegative() * 1000 >= 0.061) {
				return 1;
			} else if (p.getNegative() * 1000 - p.getPositive() * 1000 < 0.061 && p.getNegative() * 1000 - p.getPositive() * 1000 > 0.019) {
				return 2;
			} else {
				return 3;
			}
		} else {
			return 3;
		}
	}
}
