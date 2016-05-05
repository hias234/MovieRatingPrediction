package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class TF_C implements TF {
	
	@Override
	public double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms) {
		return 1 + Math.log(Util.fdt(docTerms, term));
	}
	
}
