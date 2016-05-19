package at.jku.learning.movierating.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import at.jku.learning.movierating.model.Movie;

public class RottenTomatoesMovieCrawler extends MovieCrawler {

	public RottenTomatoesMovieCrawler() {
		super("http://www.rottentomatoes.com", "http://www.rottentomatoes.com/search/?search=###SEARCH_TITLE###", "###SEARCH_TITLE###");
	}

	@Override
	protected List<CrawlerSearchResult> findSearchResults(Movie movie, String query)
			throws UnsupportedEncodingException, IOException {

		Document searchPage = getSearchPageDocument(query);

		List<CrawlerSearchResult> results = new ArrayList<>();
		if (isDetailPage(searchPage)) {
			CrawlerSearchResult result = new CrawlerSearchResult();
			result.isDetailPage = true;
			result.href = getSearchUrl(query);
			result.title = movie.getTitle();
			result.searchRank = 1;
			result.year = movie.getYear();
			
			results.add(result);
		}
		else {
			Element resultsUl = searchPage.select("#movie_results_ul").first();
			if (resultsUl != null) {
				int rank = 1;
				
				for (Element item : resultsUl.select("li div div")) {
					Element link = item.select("a").first();
					if (link != null) {
						CrawlerSearchResult result = new CrawlerSearchResult();
						result.element = item;
						result.title = link.text();
						result.href = link.attr("href");
						result.searchRank = rank;
						
						Element yearSpan = item.select(".movie_year").first();
						if (yearSpan != null) {
							try {
								result.year = Integer.parseInt(yearSpan.text().trim().replaceAll("\\(|\\)", ""));
								results.add(result);
							}
							catch(Exception ex) {
							}
						}
					}
					
					rank++;
				}
			}
		}
		
		return results;
	}

	protected boolean isDetailPage(Document searchPage) {
		return searchPage.select(".movie_synopsis").first() != null;
	}
	
	@Override
	protected Movie gatherMoreInformationFromDetailPage(Movie movie, String moviePageUrl) throws IOException {
		Document moviePage = getDocument(moviePageUrl);

		String rtText = moviePage.text();
		movie.setRtText(rtText);
		
		Element rtDescriptionElement = moviePage.select(".movie_synopsis").first();
		if (rtDescriptionElement != null) {
			movie.setRtDescription(rtDescriptionElement.text().trim());
		}
		else {
			System.out.println(moviePageUrl + " RT-DEscription=NULL");
		}
		
		return movie;
	}

}
