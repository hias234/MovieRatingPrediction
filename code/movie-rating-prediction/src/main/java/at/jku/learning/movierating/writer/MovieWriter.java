package at.jku.learning.movierating.writer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import at.jku.learning.movierating.model.Movie;

public class MovieWriter {

	public void writeMovies(List<Movie> movies, OutputStream out) {
		try (PrintWriter pw = new PrintWriter(out)) {
			writeMovies(movies, pw);
			pw.close();
		}
	}

	private void writeMovies(List<Movie> movies, PrintWriter pw) {
		for (Movie movie : movies) {
			writeMovie(movie, pw);
			pw.write("\n");
		}
		pw.flush();
	}

	private void writeMovie(Movie movie, PrintWriter pw) {
		pw.write(movie.getId().toString());
		pw.write("\t");
		pw.write(movie.getTitle());
		if (movie.getTitle2() != null) {
			pw.write(" (" + movie.getTitle2() + ")");
		}
		pw.write(" (" + movie.getYear() + ")");
		pw.write("\t");

		writeList(movie.getGenres(), pw, "|");
		pw.write("\t");

		if (movie.getImdbText() != null) {
			pw.write(movie.getImdbText().replaceAll("\t", ""));
		}
		else {
			pw.write(" ");
		}
		pw.write("\t");

		if (movie.getShortDescription() != null) {
			pw.write(movie.getShortDescription().replaceAll("\t", ""));
		}
		else {
			pw.write(" ");
		}
		pw.write("\t");

		if (movie.getStoryLine() != null) {
			pw.write(movie.getStoryLine().replaceAll("\t", ""));
		}
		else {
			pw.write(" ");
		}
		pw.write("\t");

		writeList(movie.getActors(), pw, "|");
		pw.write("\t");

		writeList(movie.getDirectors(), pw, "|");
	}

	private void writeList(List<String> list, PrintWriter pw, String separator) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) {
					pw.write(separator);
				}
				pw.write(list.get(i));
			}
		}
		else {
			pw.write(" ");
		}
	}
}
