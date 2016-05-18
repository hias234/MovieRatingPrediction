package at.jku.learning.movierating.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;

import at.jku.learning.movierating.model.Movie;

public abstract class MovieCrawler {

	protected String baseUrl;
	protected String baseSearchUrl;
	protected String baseSearchUrlQueryPattern;
	
	public MovieCrawler(String baseUrl, String baseSearchUrl, String baseSearchUrlQueryPattern) {
		super();
		this.baseUrl = baseUrl;
		this.baseSearchUrl = baseSearchUrl;
		this.baseSearchUrlQueryPattern = baseSearchUrlQueryPattern;
	}


	protected List<Movie> notFoundMovies = new ArrayList<>();
	
	protected CrawlerSearchResult findBestSearchResult(Movie movie, List<CrawlerSearchResult> results) {
		Optional<CrawlerSearchResult> result = results.stream()
				.sorted((r1, r2) -> -r1.computeScore(movie).compareTo(r2.computeScore(movie)))
				.findFirst();
		
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
	
	protected List<CrawlerSearchResult> findSearchResults(Movie movie) throws UnsupportedEncodingException, IOException {
		List<CrawlerSearchResult> results = findSearchResults(movie, movie.getTitle());
		
		/*for (int i = -1; i <= 1; i++) {
			results.addAll(findImdbSearchResults(movie, movie.getTitle() + " (" + (movie.getYear() + i) + ")"));
		}*/
		
		results.addAll(findSearchResults(movie, movie.getTitle() + " (" + movie.getYear() + ")"));
		
		return results;
	}
	
	protected abstract List<CrawlerSearchResult> findSearchResults(Movie movie, String query) throws UnsupportedEncodingException, IOException;

	protected abstract Movie gatherMoreInformationFromDetailPage(Movie movie, String moviePageUrl) throws IOException;
	
	protected Document getDocument(String url) throws IOException {
		return new CrawlerPage(url).getDocument();
	}
	

	public List<Movie> getNotFoundMovies() {
		return notFoundMovies;
	}
}
