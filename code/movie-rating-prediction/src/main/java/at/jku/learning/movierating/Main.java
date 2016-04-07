package at.jku.learning.movierating;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.Map.Entry;

import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.plot.GnuPlot;
import at.jku.learning.movierating.prediction.CombinedMeanPredictor;
import at.jku.learning.movierating.prediction.MathRoundPrecisePredictor;
import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.itembased.ItemBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.memory.userbased.UserBasedPredictor;
import at.jku.learning.movierating.prediction.collaborative.model.svd.SingularValueDecompositionPredictor;
import at.jku.learning.movierating.prediction.test.ConfigTester;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;

public class Main {
	
	private static final PrecisePredictor[] CONFIGS = {
			// TODO: all configs that should be tested; each one will pe displayed as a single column in one plot
			new MathRoundPrecisePredictor(new SingularValueDecompositionPredictor(2)),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(20)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(20)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(20), new UserBasedPredictor(20)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(25)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(25)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(25), new UserBasedPredictor(25)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(30)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(30)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(30), new UserBasedPredictor(30)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(35)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(35)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(35), new UserBasedPredictor(35)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(40)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(40)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(40), new UserBasedPredictor(40)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(45)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(45)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(45), new UserBasedPredictor(45)))),
			
			new MathRoundPrecisePredictor(new ItemBasedPredictor(50)),
			new MathRoundPrecisePredictor(new UserBasedPredictor(50)),
			new MathRoundPrecisePredictor(new CombinedMeanPredictor(Arrays.asList(new ItemBasedPredictor(50), new UserBasedPredictor(50))))
	};

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		// change if output is to be stored e.g. in a file
		PrintStream out = System.out;
		
		// create several plots for different percentage sizes of training data
		double[] percentagesTrainingData = { 0.99999, 0.99995 };
		
		out.println("Main: total rounds = " + percentagesTrainingData.length);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < percentagesTrainingData.length; i++) {
			Random rnd = new Random(System.currentTimeMillis());
			PredictorTester pTester = new PredictorTester(trainingData, percentagesTrainingData[i], rnd);
			ConfigTester cTester = new ConfigTester(out);
			List<Entry<PrecisePredictor, Double>> result = cTester.testConfigs(pTester, CONFIGS);
			GnuPlot.writeGnuPlot(result, "config" + percentagesTrainingData[i], String.format("RMSE for different configurations with training size %2.3f%%", 100 * percentagesTrainingData[i]));
			out.println("Main: " + (i + 1) + "/" + percentagesTrainingData.length);
		}
		long duration = System.currentTimeMillis() - startTime;
		out.println("Main: completed in " + duration + " ms");
	}
	
}
