package at.jku.learning.movierating.preprocessing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenPreprocessing {
	
	private static final List<String> STOPPING_BACKLIST = Arrays.asList("I, a, about, an, are, as, at, be, by, com, de, en, for, from, how, in, is, it, la, of, on, or, that, the, this, to, was, what, when, where, who, will, with, und, the, www".split(", "));
	
	public static List<String> stopping(List<String> tokens) {
		return tokens.stream()
				.filter(tkn -> !STOPPING_BACKLIST.stream()
						.anyMatch(blacklistTerm -> tkn.equalsIgnoreCase(blacklistTerm)))
				.collect(Collectors.toList());
	}
	
	public static List<String> caseFolding(List<String> tokens) {
		return tokens.stream()
				.map(tkn -> tkn.toLowerCase())
				.collect(Collectors.toList());
	}
	
	public static List<String> stemming(List<String> tokens) {
		Stemmer stemmer = new Stemmer();
		return tokens.stream()
				.map(tkn -> {
					char[] ch = tkn.toCharArray();
					stemmer.add(ch, ch.length);
					stemmer.stem();
					return stemmer.toString();
				})
				.collect(Collectors.toList());
	}
	
}
