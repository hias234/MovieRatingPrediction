package at.jku.learning.movierating.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;

import at.jku.learning.movierating.model.Movie;

public abstract class MovieCrawler {

	protected String baseUrl;
	protected String baseSearchUrl;
	protected String baseSearchUrlQueryPattern;

	protected List<Movie> notFoundMovies = new ArrayList<>();
	
	public MovieCrawler(String baseUrl, String baseSearchUrl, String baseSearchUrlQueryPattern) {
		super();
		this.baseUrl = baseUrl;
		this.baseSearchUrl = baseSearchUrl;
		this.baseSearchUrlQueryPattern = baseSearchUrlQueryPattern;
	}

	public Movie gatherMoreInformation(Movie movie) throws IOException {
		List<CrawlerSearchResult> results = findSearchResults(movie);
		CrawlerSearchResult bestResult = findBestSearchResult(movie, results);
		if (bestResult == null) {
			System.out.println("NOTHING FOUND  " + movie.getTitle() + " (" + movie.getYear() + ")");
			System.out.println(getSearchUrl(movie.getTitle()));
			notFoundMovies.add(movie);
			return movie;
		}
		
		int levenstein = bestResult.getLevenstein(movie);
		int score = bestResult.computeScore(movie);
		if (levenstein > 1) {
			System.out.println(levenstein + " \t " + score + " \t " + movie.getTitle() + " (" + movie.getYear() + ")" + " - " + bestResult.title + " (" + bestResult.year + ")");
		}
		
		return gatherMoreInformationFromDetailPage(movie, getMoviePageUrl(bestResult));
	}
	
	private String getMoviePageUrl(CrawlerSearchResult bestResult) {
		if (bestResult.href.startsWith("http://")) {
			return bestResult.href;
		}
		return baseUrl + bestResult.href;
	}

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
		
		if (results.isEmpty() || !results.get(0).isDetailPage) {
			results.addAll(findSearchResults(movie, movie.getTitle() + " (" + movie.getYear() + ")"));
		}
		
		if (results.isEmpty() && movie.getTitle2() != null) {
			results.addAll(findSearchResults(movie, movie.getTitle2()));
		}
		
		if (results.isEmpty()) {
			String[] splitters = new String[] { ":", " - ", "..." };
			for (int i = 0; i < splitters.length && results.isEmpty(); i++) {
				String splitter = splitters[i];
				if (movie.getTitle().contains(splitter)) {
					String[] parts = movie.getTitle().split(splitter);
					
					for (String part : parts) {
						results.addAll(findSearchResults(movie, part.trim()));
					}
				}
			}
		}
		
		return results;
	}
	
	protected abstract List<CrawlerSearchResult> findSearchResults(Movie movie, String query) throws UnsupportedEncodingException, IOException;

	protected abstract Movie gatherMoreInformationFromDetailPage(Movie movie, String moviePageUrl) throws IOException;
	
	protected Document getSearchPageDocument(String query) throws IOException, UnsupportedEncodingException {
		Document searchPage = getDocument(getSearchUrl(query));
		return searchPage;
	}

	protected String getSearchUrl(String query) throws UnsupportedEncodingException {
		String encodedQuery = URLEncoder.encode(query, "UTF-8")
                //.replaceAll("\\+", "%20")
                //.replaceAll("\\%21", "!")
                //.replaceAll("\\%27", "'")
                //.replaceAll("\\%28", "(")
                //.replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
		
		return baseSearchUrl.replace(baseSearchUrlQueryPattern, encodedQuery);
	}
	
	protected Document getDocument(String url) throws IOException {
		return new CrawlerPage(url).getDocument();
	}
	

	public List<Movie> getNotFoundMovies() {
		return notFoundMovies;
	}
}
