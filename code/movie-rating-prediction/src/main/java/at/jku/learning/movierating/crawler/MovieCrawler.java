package at.jku.learning.movierating.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import at.jku.learning.movierating.model.Movie;

public class MovieCrawler {

	private final String IMDB_BASE_URL = "http://www.imdb.com";
	private final String IMDB_SEARCH_URL = IMDB_BASE_URL + "/find?ref_=nv_sr_fn&q=###SEARCH_TITLE###&s=all";

	private List<Movie> notFoundMovies = new ArrayList<>();

	private Pattern yearPattern = Pattern.compile("(\\d\\d\\d\\d)");
	
	public Movie gatherMoreInformationFromImdb(Movie movie) throws IOException {
		Integer[] searchYearDeltas = new Integer[]{0, -1, 1, -2, 2};
		
		Element bestFitLink = null;
		Document searchPage;
		
		for (Integer searchYearDelta : Arrays.asList(searchYearDeltas)) {
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
		}
		
		String moviePageUrl = IMDB_BASE_URL + bestFitLink.attr("href");
		return gatherMoreInformationFromImdbPage(movie, moviePageUrl);
	}

	private Element findBestFitLinkOfSearchPage(Movie movie, Document searchPage) {
		Element bestFitLink = null;
		for (Element item : searchPage.select(".findList .findResult .result_text")) {
			Element link = item.select("a").first();
			bestFitLink = link;
			break;
			/*if (item.childNodeSize() >= 2) {
				String secondaryText = item.childNode(1).outerHtml();
				if (secondaryText.contains("TV Episode")) {
					continue;
				}
				Matcher matcher = yearPattern.matcher(secondaryText);
				if (matcher.find()) {
					Integer year = Integer.valueOf(matcher.group(1));
				}
			}*/
		}
		return bestFitLink;
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
		Element element;
		String title;
		Integer year;
		
	}
}
