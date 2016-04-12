package at.jku.learning.movierating.prediction.baseline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class UserAveragePredictor implements Predictor {

	private List<Rating> ratings;
	private Map<Long, Double> userAverages = new HashMap<>();
	
	@Override
	public Double predictRating(Long userId, Long movieId) {
		Double rating = userAverages.get(userId);
		if (rating == null) {
			rating = ratings.stream().filter(r -> r.getUserId().equals(userId))
					.mapToDouble(r -> Double.valueOf(r.getRating()))
					.average()
					.orElse(4.0);
			
			userAverages.put(userId, rating);
		}
		
		return rating;
	}

	@Override
	public void setTrainingSet(List<Rating> trainingSet) {
		this.ratings = trainingSet;
	}

	@Override
	public String toString() {
		return "UserAveragePred";
	}
}
