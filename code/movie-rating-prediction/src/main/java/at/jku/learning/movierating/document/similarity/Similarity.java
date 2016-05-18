package at.jku.learning.movierating.document.similarity;

import java.util.Map;

public interface Similarity {
	
	/**
	 * Calculates the similarity S<sub>d<sub>1</sub>,d<sub>2</sub></sub> of two documents
	 * d<sub>1</sub> and d<sub>2</sub>.
	 * 
	 * @param doc1 the document d<sub>1</sub>
	 * @param doc2 the document d<sub>2</sub>
	 * @return S<sub>d<sub>1</sub>,d<sub>2</sub></sub>
	 */
	double getSimilarity(Map<String, Double> doc1, Map<String, Double> doc2);
	
}
