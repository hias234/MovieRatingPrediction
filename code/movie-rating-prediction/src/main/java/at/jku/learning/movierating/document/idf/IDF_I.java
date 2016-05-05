package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_I implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		return 1 - (Util.nt(term, movieTerms) / Util.log2(movieTerms.size()));
	}
	
}
