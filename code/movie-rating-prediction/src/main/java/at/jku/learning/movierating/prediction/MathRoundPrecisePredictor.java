package at.jku.learning.movierating.prediction;

import java.util.List;

import at.jku.learning.movierating.model.Rating;

public class MathRoundPrecisePredictor implements PrecisePredictor {

	private Predictor predictor;
	
	public MathRoundPrecisePredictor(Predictor predictor) {
		this.predictor = predictor;
	}

	@Override
	public Integer predictRating(Long userId, Long movieId) {
		return Long.valueOf(Math.round(predictor.predictRating(userId, movieId))).intValue();
	}

	@Override
	public void setTrainingSet(List<Rating> trainingSet) {
		predictor.setTrainingSet(trainingSet);
	}
	
}
