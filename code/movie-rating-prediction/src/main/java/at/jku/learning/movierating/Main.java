package at.jku.learning.movierating;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.CombinedMeanPredictor;
import at.jku.learning.movierating.prediction.MathRoundPrecisePredictor;
import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.TruncatePrecisePredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.userbased.UserBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.model.svd.SingularValueDecompositionPredictor;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;

public class Main {

	public static void main(String[] args) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		Random rnd = new Random(System.currentTimeMillis());
		
		ItemBasedPredictor itempredictor20 = new ItemBasedPredictor(20);
		UserBasedPredictor userPredictor20 = new UserBasedPredictor(20);
		ItemBasedPredictor itempredictor23 = new ItemBasedPredictor(23);
		UserBasedPredictor userPredictor23 = new UserBasedPredictor(23);
		ItemBasedPredictor itempredictor27 = new ItemBasedPredictor(27);
		UserBasedPredictor userPredictor27 = new UserBasedPredictor(27);
		SingularValueDecompositionPredictor svdPredictor = new SingularValueDecompositionPredictor();
		
		PredictorTester tester = new PredictorTester(trainingData, 0.999, rnd);
		
		List<Map.Entry<PrecisePredictor, Double>> result = tester.comparePredictors(
				//new MathRoundPrecisePredictor(new ItemBasedPredictor(15)),
				//new MathRoundPrecisePredictor(new UserBasedPredictor(15)),
				new MathRoundPrecisePredictor(itempredictor20),
				new MathRoundPrecisePredictor(itempredictor23),
				new MathRoundPrecisePredictor(itempredictor27),
				new MathRoundPrecisePredictor(userPredictor20),
				new MathRoundPrecisePredictor(userPredictor23),
				new MathRoundPrecisePredictor(userPredictor27),
				//new MathRoundPrecisePredictor(new SingularValueDecompositionPredictor(100)),
				new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(20), new UserBasedPredictor(20)))),
				new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(23), new UserBasedPredictor(23)))),
				new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(27), new UserBasedPredictor(27))))
				// TODO add more params to test for svd
				//new TruncatePrecisePredictor(svdPredictor)
				);
		
		System.out.println();
		
		for (Map.Entry<PrecisePredictor, Double> item : result) {
			System.out.println(item.getKey() + ": " + item.getValue());
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
