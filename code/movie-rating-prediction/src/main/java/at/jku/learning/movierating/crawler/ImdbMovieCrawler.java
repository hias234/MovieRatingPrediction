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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import at.jku.learning.movierating.model.Movie;

public class ImdbMovieCrawler extends MovieCrawler {

	public ImdbMovieCrawler() {
		super("http://www.imdb.com", "http://www.imdb.com/find?ref_=nv_sr_fn&q=###SEARCH_TITLE###&s=all", "###SEARCH_TITLE###");
	}

	private Pattern yearPattern = Pattern.compile("(\\(\\d\\d\\d\\d\\))");
	
	protected List<CrawlerSearchResult> findSearchResults(Movie movie, String query) throws UnsupportedEncodingException, IOException {
		Document searchPage = getSearchPageDocument(query);
		
		List<CrawlerSearchResult> results = new ArrayList<>();
		int rank = 1;
		for (Element item : searchPage.select(".findList .findResult .result_text")) {
			Element link = item.select("a").first();
			
			CrawlerSearchResult result = new CrawlerSearchResult();
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

	protected Movie gatherMoreInformationFromDetailPage(Movie movie, String moviePageUrl) throws IOException {
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

}
