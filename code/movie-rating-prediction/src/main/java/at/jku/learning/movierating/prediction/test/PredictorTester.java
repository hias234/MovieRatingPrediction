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
		
		assert percentageTrainingData > 0.0 && percentageTrainingData < 1.0;
		
		Integer trainingSetCount = Double.valueOf(ratings.size() * percentageTrainingData).intValue();
		this.trainingSet = ratings.subList(0, trainingSetCount);
		this.testSet = ratings.subList(trainingSetCount + 1, ratings.size() - 1);
	}
	
	public Double calculateRSME(Predictor predictor) {
		predictor.setTrainingSet(trainingSet);
		
		Double innerSum = 0.0;
		int i = 0;
		for (Rating rating : testSet) {
			Double predictedRating = predictor.predictRating(rating.getUserId(), rating.getMovieId());
			innerSum += (predictedRating - rating.getRating()) * (predictedRating - rating.getRating());
			
			System.out.println((i++) + "  Real Rating: " + rating.getRating() + ", predicted: " + predictedRating);
		}
		
		return Math.sqrt(1.0 / testSet.size() * innerSum);
	}
}
