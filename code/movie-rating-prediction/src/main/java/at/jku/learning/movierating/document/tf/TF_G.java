package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class TF_G implements TF {
	
	private static final double k1 = 1.2;
	private static final double b = 0.75;
	
	@Override
	public double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms) {
		long fdt = Util.fdt(docTerms, term);
		double numerator = (k1 + 1) * fdt;
		double denominator = fdt + k1 * ((1 - b) + b * (docTerms.size() / Util.averageWd(movieTerms)));
		return numerator / denominator;
	}
	
}
