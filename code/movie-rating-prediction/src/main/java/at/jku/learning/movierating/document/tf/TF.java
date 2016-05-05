package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

public interface TF {
	
	/**
	 * Calculates the r<sub>d,t</sub> of a term t in document d, given all other
	 * documents.
	 * 
	 * @param docTerms All terms in the document <b>d</b> of r<sub><b>d</b>,t</sub>
	 * @param term The term <b>t</b> of r<sub>d,<b>t</b></sub> to calculate the weight
	 *        for.
	 * @param movieTerms All other documents (value of the map), identified by their movie
	 *        ID (key of the map).
	 * @return r<sub>d,t</sub>
	 */
	double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms);
	
}
