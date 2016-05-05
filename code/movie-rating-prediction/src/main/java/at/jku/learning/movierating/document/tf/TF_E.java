package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class TF_E implements TF {
	
	private static final double K = 0.5;
	
	@Override
	public double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms) {
		return K + (1 - K) * (Util.fdt(docTerms, term) / Util.fmd(docTerms));
	}
	
}
