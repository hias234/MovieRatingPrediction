package at.jku.learning.movierating.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.model.Rating;

public class MovieRatingReader2 {

	public List<Movie> readMovies(InputStream stream) throws IOException{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			return readMovies(reader);
		}
	}

	private List<Movie> readMovies(BufferedReader reader) throws IOException{
		List<Movie> ratings = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			ratings.add(readMovie(line));
		}
		return ratings;
	}

	private Movie readMovie(String line) {
		String[] lineParts = line.split("\t");
		assert lineParts.length >= 3 : "Corrupted input file";
		
		Movie movie = new Movie();
		movie.setId(Long.valueOf(lineParts[0]));
		
		String titleAndYear = lineParts[1];
		movie.setTitle(titleAndYear.substring(0, titleAndYear.length() - 7));
		movie.setYear(Integer.valueOf(titleAndYear.substring(titleAndYear.length() - 5, titleAndYear.length() - 1)));
		
		movie.setGenres(Arrays.asList(lineParts[2].split("\\|")));
		
		return movie;
	}
	
}
