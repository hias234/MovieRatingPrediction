package at.jku.learning.movierating.document.idf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.jku.learning.movierating.document.Util;

public class IDF_H implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		Map<Long, List<String>> distinctMovieTerms = new HashMap<>();
		for (Map.Entry<Long, List<String>> entry : movieTerms.entrySet()) {
			List<String> distinctDocTerms = entry.getValue().stream().distinct().collect(Collectors.toList());
			distinctMovieTerms.put(entry.getKey(), distinctDocTerms);
		}
		double nt = 0;
		for (List<String> distinctDocTerms : distinctMovieTerms.values()) {
			for (String distinctTerm : distinctDocTerms) {
				double max = Util.nt(distinctTerm, distinctMovieTerms);
				if (max > nt) {
					nt = max;
				}
			}
		}
		return nt - Util.nt(term, movieTerms);
	}
	
}
