package at.jku.learning.movierating.document;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.idf.IDF;
import at.jku.learning.movierating.document.tf.TF;
import at.jku.learning.movierating.preprocessing.TokenPreprocessing;

public class VectorSpaceModel {
	
	private final Map<Long, List<String>> movieTerms;
	private final PrintStream out;
	
	public VectorSpaceModel(Map<Long, List<String>> movieTokens) {
		this(movieTokens, null);
	}
	
	public VectorSpaceModel(Map<Long, List<String>> movieTokens, PrintStream out) {
		this.out = out;
		// convert raw tokens to processed terms
		movieTerms = new HashMap<>();
		movieTokens.forEach((id, tokens) -> {
			List<String> terms = TokenPreprocessing.stopping(tokens);
			terms = TokenPreprocessing.caseFolding(terms);
			terms = TokenPreprocessing.stemming(terms);
			movieTerms.put(id, terms);
		});
	}
	
	/**
	 * <pre>
	 * Map (Long = movieId
	 *      Map  = term-weight-table)
	 *      	(String = term
	 *      	 Double = weight of the token)
	 * </pre>
	 * @return
	 */
	// all documents
	public Map<Long, Map<String, Double>> getWeightedDocuments(TF tf, IDF idf) {
		Map<Long, Map<String, Double>> weightedDocuments = new HashMap<>();
		int count = 0;
		for (Long movieId : movieTerms.keySet()) {
			Map<String, Double> weightedDocument = getWeightedDocument(movieId, tf, idf);
			weightedDocuments.put(movieId, weightedDocument);
			if (out != null) {
				count++;
				out.println("movie " + count + "/" + movieTerms.size());
			}
		}
		return weightedDocuments;
	}
	
	// single document
	private Map<String, Double> getWeightedDocument(Long movieId, TF tf, IDF idf) {
		Map<String, Double> weightedDocument = new HashMap<>();
		List<String> docTerms = movieTerms.get(movieId);
		docTerms.forEach(term -> {
			double rdt = tf.getRdt(docTerms, term, movieTerms);
			double wt = idf.getWt(term, movieTerms);
			double weight = rdt * wt;
			weightedDocument.put(term, weight);
		});
		return weightedDocument;
	}
	
}
