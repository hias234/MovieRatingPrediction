package at.jku.learning.movierating.document;

import java.util.List;
import java.util.Map;

public class Util {
	
	public static double log2(double a) {
		return Math.log(a) / Math.log(2);
	}
	
	public static long fdt(List<String> docTerms, String term) {
		return docTerms.stream().filter(t -> term.equals(t)).count();
	}
	
	public static long ft(String term, Map<Long, List<String>> movieTerms) {
		long numOfDocsContainingTerm = 0;
		for (List<String> docTerms : movieTerms.values()) {
			if (docTerms.contains(term)) {
				numOfDocsContainingTerm++;
			}
		}
		return numOfDocsContainingTerm;
	}
	
	public static long Ft(String term, Map<Long, List<String>> movieTerms) {
		long numOfOccurrencesOfTerm = 0;
		for (List<String> docTerms : movieTerms.values()) {
			numOfOccurrencesOfTerm += docTerms.stream().filter(t -> term.equals(t)).count();
		}
		return numOfOccurrencesOfTerm;
	}
	
	public static long fmd(List<String> docTerms) {
		return docTerms.stream().mapToLong(term -> fdt(docTerms, term)).max().orElse(0);
	}
	
	public static double averageWd(Map<Long, List<String>> movieTerms) {
		long sumWd = 0;
		for (List<String> terms : movieTerms.values()) {
			sumWd += terms.size();
		}
		return (double) sumWd / movieTerms.size();
	}
	
	public static double nt(String term, Map<Long, List<String>> movieTerms) {
//		List<List<String>> filtered = movieTerms.values().stream()
//				.filter(docTerms -> docTerms.contains(term))
//				.collect(Collectors.toList());
//		return filtered.stream().mapToDouble(docTerms -> {
//			double temp = (double) fdt(docTerms, term) / Ft(term, movieTerms);
//			return -temp * log2(temp);
//		}).sum();
		return movieTerms.values().stream()
				.filter(docTerms -> docTerms.contains(term))
				.mapToDouble(docTerms -> {
					double temp = (double) fdt(docTerms, term) / Ft(term, movieTerms);
					return -temp * log2(temp);
				}).sum();
	}
	
	public static double st(String term, Map<Long, List<String>> movieTerms) {
		return log2(Ft(term, movieTerms) - nt(term, movieTerms));
	}
	
}
