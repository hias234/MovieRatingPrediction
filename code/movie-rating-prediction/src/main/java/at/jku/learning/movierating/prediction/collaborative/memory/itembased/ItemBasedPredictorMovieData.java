package at.jku.learning.movierating.prediction.collaborative.memory.itembased;

import java.util.AbstractMap;
import java.util.Map;

import at.jku.learning.movierating.document.similarity.Similarity;
import at.jku.learning.movierating.model.Rating;

public class ItemBasedPredictorMovieData extends ItemBasedPredictor {
	
	private final Map<Long, Map<String, Double>> weightedDocuments;
	private final Similarity sim;
	
	public ItemBasedPredictorMovieData(Integer highestSimilarityItemOffset, Map<Long, Map<String, Double>> weightedDocuments, Similarity sim) {
		super(highestSimilarityItemOffset);
		this.weightedDocuments = weightedDocuments;
		this.sim = sim;
	}
	
	@Override
	protected Double calculateSimilarity(Long itemId1, Map<Long, Rating> ratings1, Long itemId2, Map<Long, Rating> ratings2) {
		Double cachedSimilarity = itemSimilarityCache.get(new AbstractMap.SimpleEntry<>(itemId1, itemId2));
		if (cachedSimilarity != null) {
			return cachedSimilarity;
		}
		
		Map<String, Double> doc1 = weightedDocuments.get(itemId1);
		Map<String, Double> doc2 = weightedDocuments.get(itemId2);
		Double similarity = (doc1 == null || doc2 == null) ? 0 : sim.getSimilarity(doc1, doc2);
		
		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId1, itemId2), similarity);
		itemSimilarityCache.put(new AbstractMap.SimpleEntry<>(itemId2, itemId1), similarity);

		return similarity;
	}
	
	@Override
	public String toString() {
		return "MovieData " + super.toString();
	}
	
}
