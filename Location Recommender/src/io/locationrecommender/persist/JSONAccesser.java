package io.locationrecommender.persist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.google.gson.Gson;

import io.locationrecommender.data.ReviewSet;
import io.locationrecommender.main.SentimentAnalyzer;

public class JSONAccesser implements Callable<Boolean> {
	private File dataFile;
	private SentimentAnalyzer handler;
	private Gson gsonInstance = new Gson();

	public JSONAccesser(SentimentAnalyzer handler) {
		dataFile = new File("trainingData.json");
		this.handler = handler;
	}

	@Override
	public Boolean call() throws Exception {
		long init = System.currentTimeMillis();
		try {
			Scanner sc = new Scanner(new FileReader(dataFile));
			while (sc.hasNext()) {
				String line = sc.nextLine();
				ReviewSet data = gsonInstance.fromJson(line, ReviewSet.class);
				handler.setReviewMap(data.getReviewDataMap());
				handler.setIgnoredWords(data.getIgnoredWordSet());
			}
			sc.close();
			System.out.println("trainingData.json imported in [" + (System.currentTimeMillis() - init) + "ms]");
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Data file missing: " + dataFile.getName());
		}
		return false;
	}

}
