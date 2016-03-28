package at.jku.learning.movierating;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.userbased.UserBasedPredictor;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;

public class Main {

	public static void main(String[] args) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		Random rnd = new Random(1L);
		
		ItemBasedPredictor predictor = new ItemBasedPredictor(20);
		PredictorTester tester = new PredictorTester(trainingData, 0.9999, rnd);
		
		long startTime = System.currentTimeMillis();
		Double rsme = tester.calculateRSME(predictor);
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("RMSE item = " + rsme + " (" + duration + " ms)");
		
		System.out.println("\n------------------------------------------\n");
		UserBasedPredictor userPredictor = new UserBasedPredictor(20);
		startTime = System.currentTimeMillis();
		rsme = tester.calculateRSME(userPredictor);
		duration = System.currentTimeMillis() - startTime;
		System.out.println("RMSE user = " + rsme + " (" + duration + " ms)");
	}
	
}
