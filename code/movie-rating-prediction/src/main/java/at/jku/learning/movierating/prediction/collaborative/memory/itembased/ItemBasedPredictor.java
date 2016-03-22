package at.jku.learning.movierating.prediction.collaborative.memory.itembased;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class ItemBasedPredictor implements Predictor {

	private List<Rating> trainingSet;
	private Integer highestSimilarityItemOffset; // in slides = k

	public ItemBasedPredictor(List<Rating> trainingSet, Integer highestSimilarityItemOffset) {
		super();
		this.trainingSet = trainingSet;
		this.highestSimilarityItemOffset = highestSimilarityItemOffset;
		
		init();
	}

	private void init() {
		// precalculate everything needed to precalculate
	}

	@Override
	public Double predictRating(Long userId, Long movieId) {
		List<Rating> movieRatings = getRatingsByMovie(movieId);
		Map<Rating, Double> mostSimilarItems = getMostSimilarItems(movieRatings);
		
		
		return null;
	}

	private Map<Rating, Double> getMostSimilarItems(List<Rating> movieRatings) {
		for (Rating rating : movieRatings) {
			
		}
		
		return null;
	}
	
	private Double calculateSimilarity(List<Rating> ratings1, List<Rating> ratings2) {
		
		
		return null;
	}

	private List<Rating> getRatingsByMovie(Long movieId) {
		return trainingSet.stream()
						  .filter(r -> r.getMovieId().equals(movieId))
						  .collect(Collectors.toList());
	}

}
