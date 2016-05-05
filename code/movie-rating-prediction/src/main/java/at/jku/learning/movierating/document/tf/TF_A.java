package at.jku.learning.movierating.document.tf;

import java.util.List;
import java.util.Map;

public class TF_A implements TF {
	
	@Override
	public double getRdt(List<String> docTerms, String term, Map<Long, List<String>> movieTerms) {
		boolean contained = docTerms.stream()
				.distinct()
				.anyMatch(distinctTerm -> term.equals(distinctTerm));
		return contained ? 1.0 : 0.0;
	}
	
}
