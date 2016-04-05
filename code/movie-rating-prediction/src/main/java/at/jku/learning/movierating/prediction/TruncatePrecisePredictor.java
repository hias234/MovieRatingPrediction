package at.jku.learning.movierating.prediction;

import java.util.List;

import at.jku.learning.movierating.model.Rating;

public class TruncatePrecisePredictor implements PrecisePredictor {
	
	private Predictor predictor;
	
	public TruncatePrecisePredictor(Predictor predictor) {
		this.predictor = predictor;
	}

	@Override
	public Integer predictRating(Long userId, Long movieId) {
		Double rating = predictor.predictRating(userId, movieId);
		Integer numberRating = rating.intValue();
		
		return Math.min(Math.max(1, numberRating), 5);
	}

	@Override
	public void setTrainingSet(List<Rating> trainingSet) {
		predictor.setTrainingSet(trainingSet);
	}

	public Predictor getPredictor() {
		return predictor;
	}

	public void setPredictor(Predictor predictor) {
		this.predictor = predictor;
	}
	
	@Override
	public String toString() {
		return "Truncate " + predictor.toString();
	}
}
