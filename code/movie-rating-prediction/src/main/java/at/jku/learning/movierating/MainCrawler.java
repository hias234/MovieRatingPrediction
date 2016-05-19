package at.jku.learning.movierating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.jku.learning.movierating.crawler.ImdbMovieCrawler;
import at.jku.learning.movierating.crawler.MovieCrawler;
import at.jku.learning.movierating.crawler.RottenTomatoesMovieCrawler;
import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.reader.MovieReader;
import at.jku.learning.movierating.writer.MovieWriter;

public class MainCrawler {

	public static void main(String[] args) throws IOException {
		performWebCrawling("C:\\Temp\\movies_imdb_rt.dat");
	}
	
	private static void performWebCrawling(String filename) throws IOException {
		MovieReader reader = new MovieReader();
		List<Movie> movies = reader.readMovies(Main.class.getResourceAsStream("/movies.dat"));
		
		List<Movie> outMovies = new ArrayList<>();
		MovieCrawler imdbCrawler = new ImdbMovieCrawler();
		MovieCrawler rtCrawler = new RottenTomatoesMovieCrawler();
		for (int i = 0 ; i < movies.size(); i++) {
			try {
				Movie moreInfoMovie = imdbCrawler.gatherMoreInformation(movies.get(i));
				moreInfoMovie = rtCrawler.gatherMoreInformation(moreInfoMovie);
				outMovies.add(moreInfoMovie);
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
		for (Movie notFound : imdbCrawler.getNotFoundMovies()) {
			System.out.println(notFound);
		}
		System.out.println();
		System.out.println();
		for (Movie notFound : rtCrawler.getNotFoundMovies()) {
			System.out.println(notFound);
		}
	}
}
