package at.jku.learning.movierating.prediction;

public interface Predictor {

	Double predictRating(Long userId, Long movieId);
	
}
