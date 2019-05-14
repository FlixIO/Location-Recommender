package io.locationrecommender.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.locationrecommender.data.Location;
import io.locationrecommender.data.UserProfile;
import smile.imputation.MissingValueImputationException;
import smile.imputation.SVDImputation;

public class PL {
	private static DataHandler dataHandler;
	private static Random ran = new Random();
	private static List<String> nanLocations = new ArrayList<>();
	
	public static DataHandler getDataHandler() { return dataHandler; }
	
	public static void main (String [] args) {
		dataHandler = new DataHandler(500);
		for (int i = 1; i < 501; i++) {
			dataHandler.addUser(new UserProfile("User" + i));
		}
		double [][] matrix = new double [500][8];
		int x = 0;
		for (Map.Entry<String, UserProfile> entry : dataHandler.getActiveUsers().entrySet()) {
			int y = 0;
			for (Map.Entry<Location, Double> entry2 : entry.getValue().getPersonalRatingMap().entrySet()) {
				matrix[x][y] = entry2.getValue();
				y++;
			}
			x++;
		}
		double [][] nanMatrix = new double[500][8];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (ran.nextInt(101) >= 67) {
					nanMatrix[i][j] = Double.NaN;
					nanLocations.add(i + "," + j);
				}
				else {
					nanMatrix[i][j] = matrix[i][j];
				}
			}
		}
		printLocations();
		for (int i = 0; i < matrix.length; i++) {
			System.out.print("USER " + i + "\t");
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix [i][j] + "\t");
			}
			System.out.println("\n");
		}
		printLocations();
		for (int i = 0; i < nanMatrix.length; i++) {
			System.out.print("USER " + i + "\t");
			for (int j = 0; j < nanMatrix[0].length; j++) {
				System.out.print(nanMatrix [i][j] + "\t");
			}
			System.out.println("\n");
		}
		SVDImputation svd = new SVDImputation(2);
		try {
			svd.impute(nanMatrix);
//			printLocations();
//			for (int i = 0; i < matrix.length; i++) {
//				System.out.print("USER " + i + "\t");
//				for (int j = 0; j < matrix[0].length; j++) {
//					System.out.print(matrix [i][j] + "\t");
//				}
//				System.out.println("\n");
//			}
		} catch (MissingValueImputationException e) {
			e.printStackTrace();
		}
		printLocations();
		for (int i = 0; i < nanMatrix.length; i++) {
			System.out.print("USER " + i + "\t");
			for (int j = 0; j < nanMatrix[0].length; j++) {
				nanMatrix[i][j] = Math.ceil(Math.abs(nanMatrix[i][j]));
				if (nanMatrix[i][j] > 5) {
					nanMatrix[i][j] = 5;
				}
				System.out.print(nanMatrix [i][j] + "\t");
			}
			System.out.println("\n");
		}
		x = 0;
		for (Map.Entry<String, UserProfile> entry : dataHandler.getActiveUsers().entrySet()) {
			int y = 0;
			for (Map.Entry<Location, Double> entry2 : entry.getValue().getPersonalRatingMap().entrySet()) {
				if (entry2.getValue().toString().equals(Double.NaN + "")) {
					if (matrix[x][y] >= 3) {
						System.out.println(entry2.getKey() + " IS recommended for " + entry.getValue().getName());
					} else if (matrix[x][y] < 3) {
						System.out.println(entry2.getKey() + " IS NOT recommended for " + entry.getValue().getName());
					}
				}
				y++;
			}
			x++;
		}
		float correct = 0;
		for (String s : nanLocations) {
			String [] data = s.split(",");
			if (matrix[Integer.parseInt(data[0])][Integer.parseInt(data[1])] == nanMatrix[Integer.parseInt(data[0])][Integer.parseInt(data[1])]) {
				correct++;
			}
		}
		float total = nanLocations.size();
		System.out.println((correct/total * 100) + "% accurate");
	}
	
	private static void printLocations() {
		for (int h = 0; h < Location.values().length; h++) {
			System.out.print(Location.values()[h] + "\t");
		}
		System.out.println();
	}
}
