package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.Util;

public class TF_F implements TF {
	
	@Override
	public double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms) {
		long fdt = Util.fdt(docTerms, term);
		return fdt / (fdt + (docTerms.size() / Util.averageWd(movieTerms)));
	}
	
}
