package at.jku.learning.movierating.prediction.baseline;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;

public class GenrePredictor extends ItemBasedPredictor {

	private Map<Long, Movie> movies;
	
	public GenrePredictor(Integer highestSimilarityItemOffset, List<Movie> movies) {
		super(highestSimilarityItemOffset);
		
		this.movies = movies.stream().collect(Collectors.toMap(i -> i.getId(), i -> i));
	}

	@Override
	protected Double calculateSimilarity(Long itemId1, Map<Long, Rating> ratings1, Long itemId2,
			Map<Long, Rating> ratings2) {
		
		Double cachedSimilarity = itemSimilarityCache.get(new AbstractMap.SimpleEntry<>(itemId1, itemId2));
		if (cachedSimilarity != null) {
			return cachedSimilarity;
		}
		
		Movie movie1 = movies.get(itemId1);
		Movie movie2 = movies.get(itemId2);
		
		if (movie1 == null  || movie2 == null) {
			return 0.0;
		}
		
		Set<String> allGenres = new HashSet<>();
		allGenres.addAll(movie1.getGenres());
		allGenres.addAll(movie2.getGenres());
		
		Set<String> intersectinGenres = new HashSet<>();
		intersectinGenres.addAll(movie1.getGenres());
		intersectinGenres.retainAll(movie2.getGenres());
		
		Double similarity = 0.0;
		
		if (!allGenres.isEmpty()) {
			similarity = Double.valueOf(intersectinGenres.size()) / allGenres.size();
		}
		
		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId1, itemId2), similarity);
		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId2, itemId1), similarity);
		
		return similarity;
	}
	
	@Override
	public String toString() {
		return "GenrePredictor " + super.toString();
	}
}
