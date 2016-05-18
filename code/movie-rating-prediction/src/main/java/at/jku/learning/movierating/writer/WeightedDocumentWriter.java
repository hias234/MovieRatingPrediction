package at.jku.learning.movierating.writer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class WeightedDocumentWriter {
	
	public void writeWeightedDocuments(Map<Long, Map<String, Double>> weightedDocs, OutputStream out) {
		try (PrintWriter pw = new PrintWriter(out)) {
			writeWeightedDocuments(weightedDocs, pw);
		}
	}

	private void writeWeightedDocuments(Map<Long, Map<String, Double>> weightedDocs, PrintWriter pw) {
		for (Map.Entry<Long, Map<String, Double>> weightedDoc : weightedDocs.entrySet()) {
			writeWeightedDocument(weightedDoc, pw);
			pw.print('\n');
		}
		pw.flush();
	}
	
	private void writeWeightedDocument(Map.Entry<Long, Map<String, Double>> weightedDoc, PrintWriter pw) {
		long movieId = weightedDoc.getKey();
		Map<String, Double> weightedTerms = weightedDoc.getValue();
		pw.print(movieId);
		pw.print('\t');
		for (Map.Entry<String, Double> weightedTerm : weightedTerms.entrySet()) {
			pw.print(weightedTerm.getKey());
			pw.print('\t');
			pw.print(weightedTerm.getValue());
			pw.print('\t');
		}
	}
	
}
