package at.jku.learning.movierating;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import at.jku.learning.movierating.crawler.ImdbMovieCrawler;
import at.jku.learning.movierating.document.VectorSpaceModel;
import at.jku.learning.movierating.document.idf.IDF;
import at.jku.learning.movierating.document.idf.IDF_B;
import at.jku.learning.movierating.document.similarity.SIM_COS;
import at.jku.learning.movierating.document.tf.TF;
import at.jku.learning.movierating.document.tf.TF_B;
import at.jku.learning.movierating.document.tf.TF_D;
import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.plot.GnuPlot;
import at.jku.learning.movierating.prediction.MathRoundPrecisePredictor;
import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.Predictor;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictorMovieData;
import at.jku.learning.movierating.prediction.test.ConfigTester;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;
import at.jku.learning.movierating.reader.MovieReader;
import at.jku.learning.movierating.reader.WeightedDocumentReader;
import at.jku.learning.movierating.writer.MovieWriter;
import at.jku.learning.movierating.writer.WeightedDocumentWriter;

public class Main {
	
	public static void main(String[] args) throws IOException {
//		performWebCrawling("movies_imdb.dat");
		
		TF[] tfs = { new TF_B(), new TF_D() };
		IDF[] idfs = { new IDF_B()/*, new IDF_D(), new IDF_H()*/ };
		createWeightedDocuments(tfs, idfs, "weightedDoc");
		
		WeightedDocumentReader wdReader = new WeightedDocumentReader();
		Map<Long, Map<String, Double>> weightedDocsTF_B_IDF_B = wdReader.readWeightedDocuments(new FileInputStream("weightedDoc1-TF_B-IDF_B.dat"));
		Map<Long, Map<String, Double>> weightedDocsTF_D_IDF_B = wdReader.readWeightedDocuments(new FileInputStream("weightedDoc2-TF_D-IDF_B.dat"));
		Predictor[] configs = {
				// TODO: all configs that should be tested; each one will pe displayed as a single column in one plot
				new ItemBasedPredictor(20),
				new ItemBasedPredictorMovieData(20, weightedDocsTF_B_IDF_B, new SIM_COS()),
				new ItemBasedPredictorMovieData(20, weightedDocsTF_D_IDF_B, new SIM_COS()),
		};
		double[] percentagesTrainingData = { 0.99999/*, 0.99995*/ };
		long[] rndSeeds =                  { 1000L/*, 5312L*/ };
		createMovieRatings(configs, percentagesTrainingData, rndSeeds);
	}
	
	private static void createMovieRatings(Predictor[] configs, double[] percentagesTrainingData, long[] rndSeeds) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		// change if output is to be stored e.g. in a file
		PrintStream out = System.out;
		
		out.println("Main createMovieRatings: total rounds = " + percentagesTrainingData.length);
		long startTime = System.currentTimeMillis();
		// create several plots for different percentage sizes of training data
		for (int i = 0; i < percentagesTrainingData.length; i++) {
			Random rnd = new Random(rndSeeds[i]);
			PredictorTester pTester = new PredictorTester(trainingData, percentagesTrainingData[i], rnd/*, out*/);
			ConfigTester cTester = new ConfigTester(out);
			List<Entry<Predictor, Double>> result = cTester.testConfigs(pTester, configs);
			result.forEach(e -> out.println(e.getKey() + " " + e.getValue()));
			GnuPlot.writeGnuPlot(result, "config" + percentagesTrainingData[i], String.format("RMSE for different configurations with training size %2.3f%%", 100 * percentagesTrainingData[i]));
			out.println("Main createMovieRatings: " + (i + 1) + "/" + percentagesTrainingData.length);
		}
		long duration = System.currentTimeMillis() - startTime;
		out.println("Main createMovieRatings: completed in " + duration + " ms");
	}
	
	private static void performWebCrawling(String filename) throws IOException {
		MovieReader reader = new MovieReader();
		List<Movie> movies = reader.readMovies(Main.class.getResourceAsStream("/movies.dat"));
		
		List<Movie> outMovies = new ArrayList<>();
		ImdbMovieCrawler crawler = new ImdbMovieCrawler();
		for (int i = 0 ; i < movies.size(); i++) {
			try {
				outMovies.add(crawler.gatherMoreInformation(movies.get(i)));
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		
		MovieWriter writer = new MovieWriter();
		File file = new File(filename);
		
		writer.writeMovies(outMovies, new FileOutputStream(file));

		System.out.println();
		System.out.println();
		for (Movie notFound : crawler.getNotFoundMovies()) {
			System.out.println(notFound);
		}
	}
	
	private static void createWeightedDocuments(TF[] tfs, IDF[] idfs, String filenamePrefix) throws IOException {
		// change if output is to be stored e.g. in a file
		PrintStream out = System.out;
		long startTime = System.currentTimeMillis();
		
		MovieReader reader = new MovieReader();
		Map<Long, List<String>> movieTokens = reader.readMovieTokens(Main.class.getResourceAsStream("/movies_imdb_test.dat"));
		out.println("Main createWeightedDocuments: tokenization done");
		VectorSpaceModel vsm = new VectorSpaceModel(movieTokens, out);
		out.println("Main createWeightedDocuments: preprocessing done");
		
		WeightedDocumentWriter wdWriter = new WeightedDocumentWriter();
		
		for (int tf = 0; tf < tfs.length; tf++) {
			for (int idf = 0; idf < idfs.length; idf++) {
				Map<Long, Map<String, Double>> weightedDocs = vsm.getWeightedDocuments(tfs[tf], idfs[idf]);
				wdWriter.writeWeightedDocuments(weightedDocs, new FileOutputStream(new File(filenamePrefix + (idfs.length * tf + idf + 1) + "-" + tfs[tf].getClass().getSimpleName() + "-" + idfs[idf].getClass().getSimpleName() + ".dat")));
				out.println("Main createWeightedDocuments: completed " + (idfs.length * tf + idf + 1) + "/" + (tfs.length * idfs.length));
			}
		}
		
		long duration = System.currentTimeMillis() - startTime;
		out.println("Main createWeightedDocuments: completed in " + duration + " ms");
	}
	
}
