package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_E implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		long ft = Util.ft(term, movieTerms);
		return Math.log((movieTerms.size() - ft) / ft);
	}
	
}
