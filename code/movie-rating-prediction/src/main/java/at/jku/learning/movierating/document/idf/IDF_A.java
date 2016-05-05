package at.jku.learning.movierating.document.idf;

import java.util.List;
import java.util.Map;

public class IDF_A implements IDF {
	
	@Override
	public double getWt(String term, Map<Long, List<String>> movieTerms) {
		return 1.0;
	}
	
}
