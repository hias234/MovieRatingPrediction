package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_J implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		long ft = Util.ft(term, movieTerms);
		return Math.log10((movieTerms.size() - ft + 0.5) / (ft + 0.5));
	}
	
}
