package at.jku.learning.movierating.prediction.collaborative.memory.itembased;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class ItemBasedPredictor implements Predictor {

	private List<Rating> trainingSet;
	private Map<Long, Double> userRatingAverages;
	protected Map<Map.Entry<Long, Long>, Double> itemSimilarityCache;
	private Map<Long, Map<Long, Rating>> trainingSetByItemAndUser;
	private Map<Long, List<Rating>> trainingSetByUserId;
	private Integer highestSimilarityItemOffset; // in slides = k

	public ItemBasedPredictor(Integer highestSimilarityItemOffset) {
		super();
		this.highestSimilarityItemOffset = highestSimilarityItemOffset;
	}

	public ItemBasedPredictor(List<Rating> trainingSet, Integer highestSimilarityItemOffset) {
		super();

		this.highestSimilarityItemOffset = highestSimilarityItemOffset;
		setTrainingSet(trainingSet);
	}

	public void setTrainingSet(List<Rating> trainingSet) {
		this.trainingSet = trainingSet;
		this.userRatingAverages = new HashMap<>();
		this.itemSimilarityCache = new HashMap<>();
		this.trainingSetByItemAndUser = this.trainingSet.stream()
				.collect(Collectors.groupingBy(i -> i.getMovieId(), Collectors.toMap(i -> i.getUserId(), i -> i)));
		this.trainingSetByUserId = this.trainingSet.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
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
			Integer userRating = userRatings.stream().filter(r -> r.getMovieId().equals(item)).findFirst().get()
					.getRating();

			a += mostSimilarItems.get(item) * userRating;
			b += mostSimilarItems.get(item);
		}

		return a / b;
	}

	private Map<Long, Double> getMostSimilarItems(Long unknownItemId, Long userId) {
		List<Rating> userRatings = trainingSetByUserId.get(userId);

		Map<Long, Rating> ratings1 = trainingSetByItemAndUser.get(unknownItemId);

		List<Map.Entry<Long, Double>> similarItems = new ArrayList<>();
		for (Long item : trainingSetByItemAndUser.keySet()) {
			if (!item.equals(unknownItemId) && userRatings.stream().anyMatch(r -> r.getMovieId().equals(item))) {
				Map<Long, Rating> ratings2 = trainingSetByItemAndUser.get(item);
				Double similarity = calculateSimilarity(unknownItemId, ratings1, item, ratings2);

				similarItems.add(new AbstractMap.SimpleEntry<>(item, similarity));
			}
		}

		return similarItems.stream().sorted((i, j) -> -i.getValue().compareTo(j.getValue()))
				.limit(highestSimilarityItemOffset).collect(Collectors.toMap(i -> i.getKey(), i -> i.getValue()));
	}

	/**
	 * formula slide 26
	 */
	protected Double calculateSimilarity(Long itemId1, Map<Long, Rating> ratings1, Long itemId2,
			Map<Long, Rating> ratings2) {
		Double cachedSimilarity = itemSimilarityCache.get(new AbstractMap.SimpleEntry<>(itemId1, itemId2));
		if (cachedSimilarity != null) {
			return cachedSimilarity;
		}

		Set<Long> userBase = getUserBase(ratings1.values(), ratings2.values());

		Double a = 0.0;
		Double b1 = 0.0;
		Double b2 = 0.0;

		for (Long user : userBase) {
			Double userRatingAverage = getUserRatingAverage(user);
			Integer rating1 = ratings1.get(user).getRating();
			Integer rating2 = ratings2.get(user).getRating();

			a += (rating1 - userRatingAverage) * (rating2 - userRatingAverage);
			b1 += (rating1 - userRatingAverage) * (rating1 - userRatingAverage);
			b2 += (rating2 - userRatingAverage) * (rating2 - userRatingAverage);
		}

		// TODO check if isEmptyCheck is right and how to handle this case
		Double b = (Math.sqrt(b1) * Math.sqrt(b2));
		Double similarity = userBase.isEmpty() || b == 0.0 ? 1.0 : a / b;

		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId1, itemId2), similarity);
		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId2, itemId1), similarity);

		return similarity;
	}

	private Double getUserRatingAverage(Long userId) {
		Double userRatingAverage = userRatingAverages.get(userId);
		if (userRatingAverage == null) {
			userRatingAverage = trainingSetByUserId.get(userId).stream().mapToInt(r -> r.getRating()).average()
					.getAsDouble();
			userRatingAverages.put(userId, userRatingAverage);
		}
		return userRatingAverage;
	}

	/**
	 * Returns users which both rated a specific item (all items of ratings1 and
	 * ratings2 have to be from the same movie)
	 * 
	 * in slides = U
	 */
	private Set<Long> getUserBase(Collection<Rating> ratings1, Collection<Rating> ratings2) {
		Set<Long> userBase = new HashSet<>();
		userBase.addAll(ratings1.stream().map(m -> m.getUserId()).collect(Collectors.toList()));
		userBase.retainAll(ratings2.stream().map(m -> m.getUserId()).collect(Collectors.toList()));

		return userBase;
	}

	@Override
	public String toString() {
		return "ItemBasedPredictor k=" + highestSimilarityItemOffset;
	}
}
