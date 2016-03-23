package at.jku.learning.movierating.prediction.collaborative.memory.itembased;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import at.jku.learning.movierating.model.Rating;

/**
 * 
 * @author marku
 *
 */
public class ItemBasedPredictorTest {

	@Test
	public void testFromSlides() {
		List<Rating> ratings = new ArrayList<>();
		ratings.add(new Rating(1L, 1L, 3));
		ratings.add(new Rating(1L, 3L, 2));
		ratings.add(new Rating(1L, 4L, 3));
		ratings.add(new Rating(1L, 5L, 3));
		ratings.add(new Rating(2L, 1L, 4));
		ratings.add(new Rating(2L, 2L, 3));
		ratings.add(new Rating(2L, 3L, 4));
		ratings.add(new Rating(2L, 4L, 3));
		ratings.add(new Rating(3L, 1L, 3));
		ratings.add(new Rating(3L, 2L, 2));
		ratings.add(new Rating(3L, 3L, 1));
		ratings.add(new Rating(3L, 4L, 4));
		ratings.add(new Rating(3L, 5L, 4));
		ratings.add(new Rating(4L, 2L, 5));
		ratings.add(new Rating(4L, 3L, 4));
		ratings.add(new Rating(4L, 4L, 3));
		ratings.add(new Rating(4L, 5L, 1));
		ratings.add(new Rating(5L, 1L, 5));
		ratings.add(new Rating(5L, 3L, 3));
		ratings.add(new Rating(5L, 4L, 4));
		
		ItemBasedPredictor predictor = new ItemBasedPredictor(ratings, 2);
		Double prediction = predictor.predictRating(5L, 5L);
		
		assertEquals(4.54, prediction, 0.01);
	}
}
