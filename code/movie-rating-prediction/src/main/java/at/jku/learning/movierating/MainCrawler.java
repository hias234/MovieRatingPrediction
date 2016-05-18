package at.jku.learning.movierating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.jku.learning.movierating.crawler.ImdbMovieCrawler;
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
}
