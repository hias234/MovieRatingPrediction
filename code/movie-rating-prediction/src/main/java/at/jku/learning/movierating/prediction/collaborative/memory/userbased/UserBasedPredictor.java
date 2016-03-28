package at.jku.learning.movierating.prediction.collaborative.memory.userbased;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

public class UserBasedPredictor implements Predictor {
	
	private final int k;
	private List<Rating> trainingSet;
	
	public UserBasedPredictor(int k) {
		this.k = k;
	}
	
	public UserBasedPredictor(List<Rating> trainingSet, int k) {
		this(k);
		setTrainingSet(trainingSet);
	}
	
	@Override
	public Double predictRating(Long userId, Long movieId) {
		/*
		 * 1. Calculate similarity (=weight) of active user to all users that have rated the item to predict
		 */
		
		// user A
		List<Rating> ratingsA = trainingSet.stream().filter(r -> r.getUserId().equals(userId)).collect(Collectors.toList());
		final double rAMean = ratingsA.stream().collect(Collectors.summingLong(r -> r.getRating())) / (double) ratingsA.size();
		
		// all users who rated the movie with movieId
		Set<Long> sameUsers = new HashSet<>();
		for (Rating r : trainingSet) {
			if (r.getMovieId().equals(movieId)) {
				sameUsers.add(r.getUserId());
			}
		}
		List<Rating> filteredSet = trainingSet.stream().filter(r -> sameUsers.contains(r.getUserId())).collect(Collectors.toList());
		Map<Long, List<Rating>> filteredSetByUserId = filteredSet.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
		
		final Map<Long, Double> similarities = new TreeMap<>();
		filteredSetByUserId.forEach((user, ratingsU) -> {
			long sumUSame = 0;
			long count = 0;
			for (Rating rU : ratingsU) {
				for (Rating rA : ratingsA) {
					if (rA.getMovieId().equals(rU.getMovieId())) {
						sumUSame += rU.getRating();
						count++;
					}
				}
			}
			// only continue if user u has at least one common movie
			if (count > 0) {
				double rUMeanSame = sumUSame / (double) count;
				double numerator = 0;
				double divisor1 = 0;
				double divisor2 = 0;
				
				for (Rating rU : ratingsU) {
					for (Rating rA : ratingsA) {
						if (rA.getMovieId().equals(rU.getMovieId())) {
							double varianceA = rA.getRating() - rAMean;
							double varianceU = rU.getRating() - rUMeanSame;
							
							numerator += varianceA * varianceU;
							divisor1 += varianceA * varianceA;
							divisor2 += varianceU * varianceU;
						}
					}
				}
				if (divisor1 > 0 && divisor2 > 0) {
					double simAU = numerator / (Math.sqrt(divisor1) * Math.sqrt(divisor2));
					if (simAU > 0) {
						similarities.put(user, simAU);
					}
				}
			}
		});
		
		/*
		 * 2. Select k users that have highest similarity (neighborhood)
		 */
		
		SortedSet<Entry<Long, Double>> sortedSimAU = sortByValueDesc(similarities);
		Set<Entry<Long, Double>> simAULimited = sortedSimAU.stream().limit(k).collect(Collectors.toSet());
		
		/*
		 * 3. Compute prediction for item from a weighted combination of the item's ratings of users in neighborhood
		 */
		
		double numerator = 0;
		double divisor = 0;
		
		for (Entry<Long, Double> e : simAULimited) {
			List<Rating> ratingsU = filteredSetByUserId.get(e.getKey());
			long sumU = 0;
			long movieRating = 0;
			for (Rating r : ratingsU) {
				sumU += r.getRating();
				if (r.getMovieId().equals(movieId)) {
					movieRating = r.getRating();
				}
			}
			double rUMean = sumU / (double) ratingsU.size();
			numerator += e.getValue() * (movieRating - rUMean);
			divisor += e.getValue();
		}
		
		return rAMean + numerator / divisor;
	}
	
	private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortByValueDesc(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e2.getValue().compareTo(e1.getValue()); // descending
						return res != 0 ? res : 1; // Special fix to preserve items with equal values
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
	
	@Override
	public void setTrainingSet(List<Rating> trainingSet) {
		this.trainingSet = trainingSet;
	}
	
}
