package at.jku.learning.movierating;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.baseline.GenrePredictor;
import at.jku.learning.movierating.prediction.test.PredictorTester;
import at.jku.learning.movierating.reader.MovieRatingReader;
import at.jku.learning.movierating.reader.MovieReader;

public class MainGenrePredictorTest {

	public static void main(String[] args) throws IOException {
		MovieRatingReader reader = new MovieRatingReader();
		List<Rating> trainingData = reader.readRatings(Main.class.getResourceAsStream("/training.dat"));
		PredictorTester tester = new PredictorTester(trainingData, 0.99, new Random(0));
		
		MovieReader movieReader = new MovieReader();
		List<Movie> movies = movieReader.readMovies(new FileInputStream("C:\\Temp\\movies_imdb_rt.dat"));
		
		Double rsme = tester.calculateRSME(new GenrePredictor(20, movies)
				);
		
		System.out.println(rsme);
	}
	
}
