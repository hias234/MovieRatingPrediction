package at.jku.learning.movierating.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import at.jku.learning.movierating.model.Movie;
import at.jku.learning.movierating.model.Rating;

public class MovieRatingReaderTest2 {

	private MovieRatingReader2 reader = new MovieRatingReader2();
	
	@Test
	public void testReadRatings() throws IOException {
		try (InputStream stream = getClasspathStream("/movies.dat")) {
			assertNotNull(stream);
			
			List<Movie> movies = reader.readMovies(stream);
			assertFalse(movies.isEmpty());
			
			System.out.println(movies.get(0));
			
			Movie movie1 = movies.get(0);
			assertEquals(movie1.getId().longValue(), 1L);
			assertEquals(movie1.getTitle(), "Toy Story");
			assertEquals(movie1.getYear().intValue(), 1995);
			assertTrue(movie1.getGenres().contains("Animation"));
			
			Movie movie5 = movies.get(4);
			System.out.println(movie5);
			assertEquals(movie5.getId().longValue(), 1001L);
			assertEquals(movie5.getTitle(), "The Associate");
			assertEquals(movie5.getTitle2(), "L'Associe");
			assertEquals(movie5.getYear().intValue(), 1982);
			assertTrue(movie5.getGenres().contains("Comedy"));
		}
	}
	
	private InputStream getClasspathStream(String path) {
		return this.getClass().getResourceAsStream(path);
	}
}
