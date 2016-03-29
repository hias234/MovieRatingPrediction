package at.jku.learning.movierating.prediction.test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.Predictor;

public class PredictorTester {

	private List<Rating> ratings;
	
	private List<Rating> trainingSet;
	private List<Rating> testSet;
	
	public PredictorTester(List<Rating> ratings, Double percentageTrainingData, Random rnd) {
		super();
		this.ratings = ratings;
		Collections.shuffle(this.ratings, rnd);
		
		assert percentageTrainingData > 0.0 && percentageTrainingData < 1.0;
		
		Integer trainingSetCount = Double.valueOf(ratings.size() * percentageTrainingData).intValue();
		this.trainingSet = ratings.subList(0, trainingSetCount);
		this.testSet = ratings.subList(trainingSetCount + 1, ratings.size() - 1);
	}
	
	public Double calculateRSME(PrecisePredictor predictor) {
		predictor.setTrainingSet(trainingSet);
		
		Integer innerSum = 0;
		int i = 0;
		for (Rating rating : testSet) {
			Integer predictedRating = predictor.predictRating(rating.getUserId(), rating.getMovieId());
			innerSum += (predictedRating - rating.getRating()) * (predictedRating - rating.getRating());
			
			System.out.println((i++) + "  Real Rating: " + rating.getRating() + ", predicted: " + predictedRating);
		}
		
		return Math.sqrt(1.0 / testSet.size() * innerSum);
	}
	
	public List<Map.Entry<PrecisePredictor, Double>> comparePredictors(PrecisePredictor... predictors) {
		List<Map.Entry<PrecisePredictor, Double>> result = new ArrayList<>();
		
		for (PrecisePredictor predictor : predictors) {
			Double rsme = calculateRSME(predictor);
			
			result.add(new AbstractMap.SimpleEntry<PrecisePredictor, Double>(predictor, rsme));
		}
		
		result = result.stream().sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
					   .collect(Collectors.toList());
		
		return result;
	}
}
