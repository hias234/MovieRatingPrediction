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

public class MovieReader {

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
		String fullTitle = titleAndYear.substring(0, titleAndYear.length() - 6).trim();
		int indexParanthesis = fullTitle.indexOf('(');
		if (indexParanthesis == -1) {
			movie.setTitle(processTitle(fullTitle));
		}
		else {
			movie.setTitle(processTitle(fullTitle.substring(0, indexParanthesis - 1).trim()));
			movie.setTitle2(processTitle(fullTitle.substring(indexParanthesis).replaceAll("\\(|\\)", "").trim()));
		}
		movie.setYear(Integer.valueOf(titleAndYear.substring(titleAndYear.length() - 5, titleAndYear.length() - 1)));
		
		movie.setGenres(Arrays.asList(lineParts[2].split("\\|")));
		
		if (lineParts.length > 3) {
			movie.setImdbText(lineParts[3]);
		}
		if (lineParts.length > 4) {
			movie.setShortDescription(lineParts[4]);
		}
		if (lineParts.length > 5) {
			movie.setStoryLine(lineParts[5]);
		}
		if (lineParts.length > 6) {
			movie.setActors(Arrays.asList(lineParts[6].split("\\|")));
		}
		if (lineParts.length > 7) {
			movie.setDirectors(Arrays.asList(lineParts[7].split("\\|")));
		}
		
		return movie;
	}
	
	private String processTitle(String title) {
		if (title.endsWith(", The")) {
			return "The " + title.substring(0, title.length() - 5).trim();
		}
		if (title.endsWith(", A")) {
			return "A " + title.substring(0, title.length() - 3).trim();
		}
		if (title.endsWith(", An")) {
			return "An " + title.substring(0, title.length() - 4).trim();
		}
		if (title.endsWith(", L'")) {
			return "L'" + title.substring(0, title.length() - 4).trim();
		}
		if (title.endsWith(", Les")) {
			return "Les " + title.substring(0, title.length() - 5).trim();
		}
		if (title.endsWith(", La")) {
			return "La " + title.substring(0, title.length() - 4).trim();
		}
		if (title.endsWith(", Le")) {
			return "Le " + title.substring(0, title.length() - 4).trim();
		}
		if (title.endsWith(", El")) {
			return "El " + title.substring(0, title.length() - 4).trim();
		}
		if (title.endsWith(", Der")) {
			return "Der " + title.substring(0, title.length() - 5).trim();
		}
		if (title.endsWith(", Die")) {
			return "Die " + title.substring(0, title.length() - 5).trim();
		}
		if (title.endsWith(", Das")) {
			return "Das " + title.substring(0, title.length() - 5).trim();
		}
		
		return title.trim();
	}
}
