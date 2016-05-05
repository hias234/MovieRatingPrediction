package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_D implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		long fm = 0;
		for (List<String> docTerms : movieTerms.values()) {
			long max = docTerms.stream().mapToLong(t -> Util.ft(t, movieTerms)).max().orElse(0);
			if (max > fm) {
				fm = max;
			}
		}
		return Math.log(1 + ((double) fm / Util.ft(term, movieTerms)));
	}
	
}
