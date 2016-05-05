package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_B2 implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		return Math.log(movieTerms.size() / Util.ft(term, movieTerms));
	}
	
}
