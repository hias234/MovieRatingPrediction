package at.jku.learning.movierating.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import at.jku.learning.movierating.model.Movie;

public class MovieCrawler {

	private final String IMDB_BASE_URL = "http://www.imdb.com";
	private final String IMDB_SEARCH_URL = IMDB_BASE_URL + "/find?ref_=nv_sr_fn&q=###SEARCH_TITLE###&s=all";

	private List<Movie> notFoundMovies = new ArrayList<>();

	private Pattern yearPattern = Pattern.compile("(\\(\\d\\d\\d\\d\\))");
	
	public Movie gatherMoreInformationFromImdb(Movie movie) throws IOException {
		List<ImdbSearchResult> results = findImdbSearchResults(movie);
		ImdbSearchResult bestResult = findBestSearchResult(movie, results);
		if (bestResult == null) {
			System.out.println("NOTHING FOUND  " + movie.getTitle() + " (" + movie.getYear() + ")");
			notFoundMovies.add(movie);
			return movie;
		}
		
		/*for (Integer searchYearDelta : Arrays.asList(searchYearDeltas)) {
			String searchString = movie.getTitle() + " (" + (movie.getYear() + searchYearDelta) + ")";

			searchPage = getDocument(IMDB_SEARCH_URL.replace("###SEARCH_TITLE###", URLEncoder.encode(searchString, "UTF-8")));
			bestFitLink = findBestFitLinkOfSearchPage(movie, searchPage);
			if (bestFitLink != null) {
				break;
			}
		}
		
		if (bestFitLink == null) {
			searchPage = getDocument(IMDB_SEARCH_URL.replace("###SEARCH_TITLE###", URLEncoder.encode(movie.getTitle(), "UTF-8")));
			bestFitLink = findBestFitLinkOfSearchPage(movie, searchPage);
			
			if (bestFitLink == null) {
				notFoundMovies.add(movie);
				return movie;
			}
		}
		if (!bestFitLink.text().toLowerCase().contains(movie.getTitle().toLowerCase()) || !movie.getTitle().toLowerCase().contains(bestFitLink.text().toLowerCase())) {
			System.out.println("WRONG TITLE: " + movie.getTitle() + " (" + movie.getYear() + ")" + " - "  + bestFitLink.text());
		}*/
		
		int levenstein = bestResult.getLevenstein(movie);
		int score = bestResult.computeScore(movie);
		if (levenstein > 1) {
			System.out.println(levenstein + " \t " + score + " \t " + movie.getTitle() + " (" + movie.getYear() + ")" + " - " + bestResult.title + " (" + bestResult.year + ")");
		}
		
		String moviePageUrl = IMDB_BASE_URL + bestResult.href;
		return gatherMoreInformationFromImdbPage(movie, moviePageUrl);
	}

	private ImdbSearchResult findBestSearchResult(Movie movie, List<ImdbSearchResult> results) {
		Optional<ImdbSearchResult> result = results.stream()
				.sorted((r1, r2) -> -r1.computeScore(movie).compareTo(r2.computeScore(movie)))
				.findFirst();
		
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
	
	private List<ImdbSearchResult> findImdbSearchResults(Movie movie) throws UnsupportedEncodingException, IOException {
		List<ImdbSearchResult> results = findImdbSearchResults(movie, movie.getTitle());
		
		/*for (int i = -1; i <= 1; i++) {
			results.addAll(findImdbSearchResults(movie, movie.getTitle() + " (" + (movie.getYear() + i) + ")"));
		}*/
		
		results.addAll(findImdbSearchResults(movie, movie.getTitle() + " (" + movie.getYear() + ")"));
		
		return results;
	}
	
	private List<ImdbSearchResult> findImdbSearchResults(Movie movie, String query) throws UnsupportedEncodingException, IOException {
		Document searchPage = getDocument(IMDB_SEARCH_URL.replace("###SEARCH_TITLE###", URLEncoder.encode(query, "UTF-8")));
		
		List<ImdbSearchResult> results = new ArrayList<>();
		int rank = 1;
		for (Element item : searchPage.select(".findList .findResult .result_text")) {
			Element link = item.select("a").first();
			
			ImdbSearchResult result = new ImdbSearchResult();
			result.element = item;
			result.title = link.text();
			result.href = link.attr("href");

			Element aka = item.select("i").first();
			if (aka != null) {
				result.aka = aka.text().replaceAll("\"", "");
			}
			
			if (item.childNodeSize() > 1) {
				Node furtherInfoNode = item.childNode(2);
				String furtherInfoText = furtherInfoNode.outerHtml();
				if (!furtherInfoText.contains("(TV Series)")
					&& !furtherInfoText.contains("(TV Mini-Series)")
					&& !furtherInfoText.contains("(TV Episode)")) {
					Matcher m = yearPattern.matcher(furtherInfoText);
					if (m.find()) {
						result.year = Integer.parseInt(m.group(1).trim().replaceAll("\\(|\\)", ""));
						result.searchRank = rank++;
						results.add(result);
					}
				}
			}
		}
		
		return results;
	}
	
	public List<Movie> getNotFoundMovies() {
		return notFoundMovies;
	}

	private Movie gatherMoreInformationFromImdbPage(Movie movie, String moviePageUrl) throws IOException {
		Document moviePage = getDocument(moviePageUrl);

		String imdbText = moviePage.text();
		movie.setImdbText(imdbText);

		String shortDescription = moviePage.select(".summary_text").text();
		movie.setShortDescription(shortDescription.trim());

		Element storyLineElement = moviePage.select("#titleStoryLine p").first();
		if (storyLineElement != null) {
			String storyLine = storyLineElement.childNodes().get(0).outerHtml();
			movie.setStoryLine(storyLine.trim());
		}

		List<String> actors = new ArrayList<>();
		for (Element actorRow : moviePage.select(".cast_list tr td .itemprop")) {
			actors.add(actorRow.text());
		}
		movie.setActors(actors);

		List<String> directors = new ArrayList<>();
		for (Element directorRow : moviePage.select("span[itemprop=director]")) {
			directors.add(directorRow.text());
		}
		movie.setDirectors(directors);

		if (shortDescription == null || shortDescription.isEmpty())
			System.out.println(movie.getTitle() + "  shortDescription=NULL");
		if (storyLineElement == null)
			System.out.println(movie.getTitle() + "  storyLine=NULL");
		if (actors.isEmpty())
			System.out.println(movie.getTitle() + "  actors=[]");
		if (directors.isEmpty()) 
			System.out.println(movie.getTitle() + "  directors=[]");

		return movie;
	}

	private Document getDocument(String url) throws IOException {
		return new CrawlerPage(url).getDocument();
	}
	
	private static class ImdbSearchResult {
		public Element element;
		public Integer searchRank;
		public String title;
		public Integer year;
		public String aka;
		public String href;
		
		public Integer computeScore(Movie movie) {
			Integer score = 0;

			if (movie.getTitle().equalsIgnoreCase(title) || movie.getTitle().equalsIgnoreCase(aka)) {
				score += 1000;
			}
			else {
				int levenstein = getLevenstein(movie);
				score += 900 - levenstein * 30;
			}
			score -= searchRank * 10;
			
			score -= Math.abs(year - movie.getYear()) * 200;
			
			return score;
		}

		private int getLengthDiff(Movie movie) {
			int lengthDiff = Math.abs(movie.getTitle().length() - title.length());
			
			if (title.contains(": ") || title.contains(" - ")) {
				String shortTitle = title.split(": ")[0];
				shortTitle = shortTitle.split(" - ")[0];
				lengthDiff = Math.min(lengthDiff, Math.abs(movie.getTitle().length() - shortTitle.length()));
			}
			
			if (aka != null) {
				lengthDiff = Math.min(lengthDiff, Math.abs(movie.getTitle().length() - aka.length()));
				
				if (aka.contains(": ") || aka.contains(" - ")) {
					String shortAka = aka.split(": ")[0];
					shortAka = shortAka.split(" - ")[0];
					lengthDiff = Math.min(lengthDiff, Math.abs(movie.getTitle().length() - shortAka.length()));
				}
			}
			return lengthDiff;
		}

		private int getLevenstein(Movie movie) {
			int levenstein;
			int levensteinTitle = StringUtils.getLevenshteinDistance(movie.getTitle().toLowerCase(), title.toLowerCase());
			if (aka != null) {
				int levensteinAka = StringUtils.getLevenshteinDistance(movie.getTitle().toLowerCase(), aka.toLowerCase());
				levenstein = Math.min(levensteinAka, levensteinTitle);
				
				if (aka.contains(": ") || aka.contains(" - ")) {
					String shortAka = aka.split(": ")[0];
					shortAka = shortAka.split(" - ")[0];
					
					int levensteinShortAka = StringUtils.getLevenshteinDistance(movie.getTitle().toLowerCase(), shortAka.toLowerCase());
					levenstein = Math.min(levenstein, levensteinShortAka);
				}
			}
			else {
				levenstein = levensteinTitle;
			}
			
			if (title.contains(": ") || title.contains(" - ")) {
				String shortTitle = title.split(": ")[0];
				shortTitle = shortTitle.split(" - ")[0];
				
				int levensteinShortTitle = StringUtils.getLevenshteinDistance(movie.getTitle().toLowerCase(), shortTitle.toLowerCase());
				levenstein = Math.min(levenstein, levensteinShortTitle);
			}
			
			return levenstein;
		}
	}
}
