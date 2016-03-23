package at.jku.learning.movierating.prediction.test;

import java.util.Collections;
import java.util.List;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class PredictorTester {

	private List<Rating> ratings;
	
	private List<Rating> trainingSet;
	private List<Rating> testSet;
	
	public PredictorTester(List<Rating> ratings, Double percentageTrainingData) {
		super();
		this.ratings = ratings;
		Collections.shuffle(this.ratings);
		
		
	}
	
	public Double calculateRSME(Predictor predictor) {
		return null;
	}
}
