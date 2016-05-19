package at.jku.learning.movierating.model;

import java.util.List;

public class Movie {

	private Long id;
	private String title;
	private String title2;
	private Integer year;
	private List<String> genres;

	// IMDB
	private String imdbText;
	private String shortDescription;
	private String storyLine;
	private List<String> actors;
	private List<String> directors;

	// Rotten tomatoes
	private String rtText;
	private String rtDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public String getImdbText() {
		return imdbText;
	}

	public void setImdbText(String imdbText) {
		this.imdbText = imdbText;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getStoryLine() {
		return storyLine;
	}

	public void setStoryLine(String storyLine) {
		this.storyLine = storyLine;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public List<String> getDirectors() {
		return directors;
	}

	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

	public String getRtText() {
		return rtText;
	}

	public void setRtText(String rtText) {
		this.rtText = rtText;
	}

	public String getRtDescription() {
		return rtDescription;
	}

	public void setRtDescription(String rtDescription) {
		this.rtDescription = rtDescription;
	}

	@Override
	public String toString() {
		return "Movie [id=" + id + ", title=" + title + ", title2=" + title2 + ", year=" + year + ", genres=" + genres
				+ "]";
	}

}
