package at.jku.learning.movierating.prediction.collaborative.memory.itembased;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class ItemBasedPredictor implements Predictor {

	private List<Rating> trainingSet;
	private Map<Long, List<Rating>> trainingSetByItemId;
	private Map<Long, List<Rating>> trainingSetByUserId;
	private Integer highestSimilarityItemOffset; // in slides = k

	public ItemBasedPredictor(List<Rating> trainingSet, Integer highestSimilarityItemOffset) {
		super();
		this.trainingSet = trainingSet;
		this.trainingSetByItemId = trainingSet.stream().collect(Collectors.groupingBy(i -> i.getMovieId()));
		this.trainingSetByUserId = trainingSet.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
		this.highestSimilarityItemOffset = highestSimilarityItemOffset;

		init();
	}

	private void init() {
		// precalculate everything needed to precalculate
	}

	@Override
	public Double predictRating(Long userId, Long movieId) {
		Map<Long, Double> mostSimilarItems = getMostSimilarItems(movieId, userId);
		return calculatePredictedRating(userId, mostSimilarItems);
	}

	private Double calculatePredictedRating(Long userId, Map<Long, Double> mostSimilarItems) {
		List<Rating> userRatings = trainingSetByUserId.get(userId);
		
		Double a = 0.0;
		Double b = 0.0;
		for (Long item : mostSimilarItems.keySet()) {
			Integer userRating = userRatings.stream().filter(r -> r.getMovieId().equals(item)).findFirst().get().getRating();
			
			a += mostSimilarItems.get(item) * userRating;
			b += mostSimilarItems.get(item);
		}
		
		return a / b;
	}

	private Map<Long, Double> getMostSimilarItems(Long movieId, Long userId) {
		List<Rating> userRatings = trainingSetByUserId.get(userId);
		
		List<Rating> ratings1 = trainingSetByItemId.get(movieId);
		
		List<Map.Entry<Long, Double>> similarItems = new ArrayList<>();
		for (Long item : trainingSetByItemId.keySet()) {
			if (!item.equals(movieId) && userRatings.stream().anyMatch(r -> r.getMovieId().equals(item))) {
				List<Rating> ratings2 = trainingSetByItemId.get(item);
				Double similarity = calculateSimilarity(ratings1, ratings2);
				
				similarItems.add(new AbstractMap.SimpleEntry<>(item, similarity));
			}
		}

		return similarItems.stream()
				.sorted((i, j) -> -i.getValue().compareTo(j.getValue()))
				.limit(highestSimilarityItemOffset)
				.collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
	}

	/**
	 * formula slide 26
	 */
	protected Double calculateSimilarity(List<Rating> ratings1, List<Rating> ratings2) {
		Set<Long> userBase = getUserBase(ratings1, ratings2);
		
		Double a = 0.0;
		Double b1 = 0.0;
		Double b2 = 0.0;
		
		for (Long user : userBase) {
			Double userBaseAverage = calculateUserBaseAverage(user);
			Integer rating1 = ratings1.stream().filter(r -> r.getUserId().equals(user)).findFirst().get().getRating();
			Integer rating2 = ratings2.stream().filter(r -> r.getUserId().equals(user)).findFirst().get().getRating();
			
			a += (rating1 - userBaseAverage) * (rating2 - userBaseAverage);
			b1 += (rating1 - userBaseAverage) * (rating1 - userBaseAverage);
			b2 += (rating2 - userBaseAverage) * (rating2 - userBaseAverage);
		}

		return a / (Math.sqrt(b1) * Math.sqrt(b2));
	}

	// TODO make better solution...
	private Double calculateUserBaseAverage(Long userId) {
		return trainingSetByUserId.get(userId).stream().mapToInt(r -> r.getRating()).average().getAsDouble();
	}

	/**
	 * Returns users which both rated a specific item (all items of ratings1 and
	 * ratings2 have to be from the same movie)
	 * 
	 * in slides = U
	 */
	private Set<Long> getUserBase(List<Rating> ratings1, List<Rating> ratings2) {
		Set<Long> userBase = new HashSet<>();
		userBase.addAll(ratings1.stream().map(m -> m.getUserId()).collect(Collectors.toList()));
		userBase.retainAll(ratings2.stream().map(m -> m.getUserId()).collect(Collectors.toList()));

		return userBase;
	}

}
