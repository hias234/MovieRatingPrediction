package at.jku.learning.movierating.prediction;

import java.util.List;

import at.jku.learning.movierating.model.Rating;

public interface PrecisePredictor {

	Integer predictRating(Long userId, Long movieId);

	void setTrainingSet(List<Rating> trainingSet);
	
	Predictor getPredictor();
}
