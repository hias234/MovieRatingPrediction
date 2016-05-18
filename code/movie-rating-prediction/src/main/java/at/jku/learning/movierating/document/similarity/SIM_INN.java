package at.jku.learning.movierating.document.similarity;

import java.util.Map;

public class SIM_INN implements Similarity {
	
	@Override
	public double getSimilarity(Map<String, Double> doc1, Map<String, Double> doc2) {
		double sim = 0;
		for (Map.Entry<String, Double> e : doc1.entrySet()) {
			double w_d1_t = e.getValue();
			Double doc2weight = doc2.get(e.getKey());
			double w_d2_t = doc2weight == null ? 0 : doc2weight;
			sim += w_d1_t * w_d2_t;
		}
		return sim;
	}
	
}
