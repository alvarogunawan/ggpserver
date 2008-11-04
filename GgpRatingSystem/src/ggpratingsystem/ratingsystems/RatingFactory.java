package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class RatingFactory {
//	private static HashMap<String, Double> initialRatings = new HashMap<String, Double>();
//	
//	static {
//		initialRatings.put("U-TEXAS-LARG", 1149.521816533276);
//		initialRatings.put("CLUNEPLAYER", 1894.2681939518773);
//		initialRatings.put("JIGSAWBOT", 761.3162874298564);
//		initialRatings.put("FLUXPLAYER", 1567.9864873802828);
//		initialRatings.put("O-GRABME", 962.5372358694761);
//		initialRatings.put("LUCKY-LEMMING", 637.308902572719);
//		initialRatings.put("RANDOM4", 1000.0);
//		initialRatings.put("W-WOLFE", -689.5230242719155);
//		initialRatings.put("CADIA-PLAYER", 2354.223947158014);
//		initialRatings.put("ARY", 1687.9994202988808);
//		initialRatings.put("RANDOM3", 1031.875);
//		initialRatings.put("RANDOM2", 936.25);
//		initialRatings.put("RANDOM", 615.7172571053981);
//		initialRatings.put("THE-PIRATE", 380.38194444444446);
//	}
	
	public static Rating makeRating(RatingSystemType type, Player player) {
		Rating result;
		
//		double initialRating;
//		if (initialRatings.containsKey(player.getName())) {
//			initialRating = initialRatings.get(player.getName());
//		}
//		else {
//			initialRating = 1000.0;
//		}
	
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
//			result = new LinearRegressionRating(player, initialRating);
			result = new LinearRegressionRating(player);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
//			result = new LinearRegressionRating(player, initialRating);
			result = new LinearRegressionRating(player);
			break;
			
		case DIRECT_SCORES:
			result = new Rating(player, 0.0);
			break;
			
			/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
		default:
			throw new IllegalArgumentException("unknown RatingSystemType: " + type);
		}
		
		return result;
	}

	public static void initializeRatings(RatingSystemType type, File previousRatings) throws IOException {
		CSVReader reader;
		reader = new CSVReader(new FileReader(previousRatings));

		// first line: player names
		String[] playerNames = reader.readNext();
		
		// last line: final ratings
		String[] finalRatings = null;
		for (String [] next = reader.readNext(); next != null; next = reader.readNext()) {
			finalRatings = next;
		}
		
		if (playerNames == null || finalRatings == null || playerNames.length != finalRatings.length) {
			throw new IllegalArgumentException("Wrong format of revious CSV output file.");
		}
		
		// set initial ratings
		for (int i = 0; i < playerNames.length; i++) {
			String playerName = playerNames[i];
	    	double finalRating = Double.parseDouble(finalRatings[i]);
			Player.getInstance(playerName).getRating(type).setCurRating(finalRating);
		}
	}
}
