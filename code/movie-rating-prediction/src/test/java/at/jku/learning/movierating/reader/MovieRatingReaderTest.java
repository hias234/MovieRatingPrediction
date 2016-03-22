package at.jku.learning.movierating.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import at.jku.learning.movierating.model.Rating;

public class MovieRatingReaderTest {

	private MovieRatingReader reader = new MovieRatingReader();
	
	@Test
	public void testReadRatings() throws IOException {
		try (InputStream stream = getClasspathStream("/training.dat")) {
			assertNotNull(stream);
			
			List<Rating> ratings = reader.readRatings(stream);
			assertFalse(ratings.isEmpty());
			
			Rating rating1 = ratings.get(1);
			assertEquals(1L, rating1.getUserId().longValue());
			assertEquals(1036L, rating1.getMovieId().longValue());
			assertEquals(3, rating1.getRating().intValue());
		}
	}
	
	private InputStream getClasspathStream(String path) {
		return this.getClass().getResourceAsStream(path);
	}
}
