package at.jku.learning.movierating.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import at.jku.learning.movierating.model.Rating;

public class MovieRatingReader {
	
	public List<Rating> readRatings(InputStream stream) throws IOException{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			return readRatings(reader);
		}
	}

	private List<Rating> readRatings(BufferedReader reader) throws IOException{
		List<Rating> ratings = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			ratings.add(readRating(line));
		}
		return ratings;
	}

	private Rating readRating(String line) {
		String[] lineParts = line.split("\t");
		assert lineParts.length >= 2 : "Corrupted input file";
		
		Rating rating = new Rating();
		rating.setUserId(Long.valueOf(lineParts[0]));
		rating.setMovieId(Long.valueOf(lineParts[1]));
		if (lineParts.length > 2) {
			rating.setRating(Integer.valueOf(lineParts[2]));
		}
		
		return rating;
	}
	
}
