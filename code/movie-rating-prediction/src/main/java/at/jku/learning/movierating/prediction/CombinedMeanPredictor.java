package at.jku.learning.movierating.prediction;

import java.util.List;

import at.jku.learning.movierating.model.Rating;

public class CombinedMeanPredictor implements Predictor {

	private List<Predictor> predictors;
	
	public CombinedMeanPredictor(List<Predictor> predictors) {
		super();
		this.predictors = predictors;
	}

	@Override
	public Double predictRating(Long userId, Long movieId) {
		return predictors.stream()
				.map(p -> p.predictRating(userId, movieId))
				.mapToDouble(d -> d)
				.average()
				.getAsDouble();
	}

	@Override
	public void setTrainingSet(List<Rating> trainingSet) {
		predictors.forEach(p -> p.setTrainingSet(trainingSet));
	}

	@Override
	public String toString() {
		String s = "CombinedMeanPred (";
		int i = 0;
		for (Predictor p : predictors) {
			if (i > 0) {
				s += ", ";
			}
			s += p.toString();
			i++;
		}
		s += ")";
		return s;
	}
}
