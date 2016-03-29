package at.jku.learning.movierating;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.MathRoundPrecisePredictor;
import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.TruncatePrecisePredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.userbased.UserBasedPredictor;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;

public class Main {

	public static void main(String[] args) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		Random rnd = new Random(System.currentTimeMillis());
		
		ItemBasedPredictor predictor = new ItemBasedPredictor(20);
		UserBasedPredictor userPredictor = new UserBasedPredictor(20);
		PredictorTester tester = new PredictorTester(trainingData, 0.9999, rnd);
		
		List<Map.Entry<PrecisePredictor, Double>> result = tester.comparePredictors(
				new MathRoundPrecisePredictor(predictor),
				new MathRoundPrecisePredictor(userPredictor),
				new TruncatePrecisePredictor(userPredictor),
				new TruncatePrecisePredictor(predictor));
		
		System.out.println();
		
		for (Map.Entry<PrecisePredictor, Double> item : result) {
			System.out.print(item.getKey().getClass().getSimpleName() + " " + item.getKey().getPredictor().getClass().getSimpleName());
			System.out.println(": " + item.getValue());
		}
		
//		long startTime = System.currentTimeMillis();
//		Double rsme = tester.calculateRSME(new MathRoundPrecisePredictor(predictor));
//		long duration = System.currentTimeMillis() - startTime;
//		System.out.println("RMSE item = " + rsme + " (" + duration + " ms)");
//		
//		System.out.println("\n------------------------------------------\n");
//		startTime = System.currentTimeMillis();
//		rsme = tester.calculateRSME(new MathRoundPrecisePredictor(userPredictor));
//		duration = System.currentTimeMillis() - startTime;
//		System.out.println("RMSE user = " + rsme + " (" + duration + " ms)");
	}
	
}
