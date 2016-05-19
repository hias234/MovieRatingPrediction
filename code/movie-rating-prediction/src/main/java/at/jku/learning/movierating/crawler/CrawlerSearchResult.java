package at.jku.learning.movierating.crawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import at.jku.learning.movierating.model.Movie;

public class CrawlerSearchResult {
	public Element element;
	public Integer searchRank;
	public String title;
	public Integer year;
	public String aka;
	public String href;
	public Boolean isDetailPage = false;
	
	public Integer computeScore(Movie movie) {
		Integer score = 0;

		if (isDetailPage) {
			return 10000000;
		}
		
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

	int getLevenstein(Movie movie) {
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