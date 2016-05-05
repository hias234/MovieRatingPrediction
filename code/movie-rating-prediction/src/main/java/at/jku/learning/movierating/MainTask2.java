package at.jku.learning.movierating;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.document.VectorSpaceModel;
import at.jku.learning.movierating.document.idf.IDF;
import at.jku.learning.movierating.document.idf.IDF_B;
import at.jku.learning.movierating.document.idf.IDF_D;
import at.jku.learning.movierating.document.idf.IDF_H;
import at.jku.learning.movierating.document.tf.TF;
import at.jku.learning.movierating.document.tf.TF_B;
import at.jku.learning.movierating.document.tf.TF_D;
import at.jku.learning.movierating.reader.MovieReader;

public class MainTask2 {
	
	public static void main(String[] args) throws IOException {
		// change if output is to be stored e.g. in a file
		PrintStream out = System.out;
		
		MovieReader reader = new MovieReader();
		Map<Long, List<String>> movieTokens = reader.readMovieTokens(Main.class.getResourceAsStream("/movies_imdb.dat"));
		out.println("Tokenization done!");
		VectorSpaceModel vsm = new VectorSpaceModel(movieTokens, out);
		out.println("Preprocessing done!");
		
		TF[] tfs = { new TF_B(), new TF_D() };
		IDF[] idfs = { new IDF_B(), new IDF_D(), new IDF_H() };
		
		for (int tf = 0; tf < tfs.length; tf++) {
			for (int idf = 0; idf < idfs.length; idf++) {
				Map<Long, Map<String, Double>> weightedDocs = vsm.getWeightedDocuments(tfs[tf], idfs[idf]);
//				weightedDocs.forEach((id, map) -> {
//					out.println("ID = " + id);
//					map.forEach((term, weight) -> out.println(term + " = " + weight));
//					out.println();
//				});
				out.println("Completed " + (idfs.length * tf + idf + 1) + "/" + (tfs.length * idfs.length));
			}
		}
	}
	
}
