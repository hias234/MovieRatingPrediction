package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class IDF_G implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		return Util.st(term, movieTerms) / Util.nt(term, movieTerms);
	}
	
}
