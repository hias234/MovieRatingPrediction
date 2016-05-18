package at.jku.learning.movierating.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WeightedDocumentReader {
	
	public Map<Long, Map<String, Double>> readWeightedDocuments(InputStream stream) throws IOException {
		Map<Long, Map<String, Double>> weightedDocs;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			weightedDocs = readMovieData(reader);
		}
		return weightedDocs;
	}
	
	private Map<Long, Map<String, Double>> readMovieData(BufferedReader reader) throws IOException {
		Map<Long, Map<String, Double>> weightedDocs = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split("\t");
			Long movieId = Long.parseLong(tokens[0]);
			Map<String, Double> weightedTerms = readWeightedTerms(tokens);
			weightedDocs.put(movieId, weightedTerms);
		}
		return weightedDocs;
	}

	private Map<String, Double> readWeightedTerms(String[] tokens) {
		// token[0] = movieId, start at index 1
		// token[x] = term
		// token[x+1] = weight
		Map<String, Double> weightedTerms = new HashMap<>();
		for (int i = 1; i < tokens.length; i += 2) {
			String term = tokens[i];
			Double weight = Double.valueOf(tokens[i + 1]);
			weightedTerms.put(term, weight);
		}
		return weightedTerms;
	}
	
}
