package at.jku.learning.movierating;

import java.io.IOException;
import java.util.List;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;

public class Main {

	public static void main(String[] args) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		
		ItemBasedPredictor predictor = new ItemBasedPredictor(20);
		PredictorTester tester = new PredictorTester(trainingData, 0.9);
		
		Double rsme = tester.calculateRSME(predictor);
		System.out.println(rsme);
	}
	
}
