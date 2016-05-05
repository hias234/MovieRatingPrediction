package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

public interface IDF {
	
	/**
	 * Calculates the w<sub>t</sub> of a term t, given all other documents.
	 * 
	 * @param term The term <b>t</b> of w<sub><b>t</b></sub> to calculate the weight for.
	 * @param movieTerms All other documents (value of the map), identified by their movie
	 *        ID (key of the map).
	 * @return w<sub>t</sub>
	 */
	double getWt(String term, Map<Long, List<String>> movieTerms);
	
}
