package at.jku.learning.movierating.crawler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlerPage {

	private String url;
	private String cacheName;

	public CrawlerPage(String url) {
		super();
		this.url = url;
	}
	
	public Document getDocument() throws IOException {
		if (isCached()) {
			return getDocumentFromCache();
		}
		else {
			Document document = Jsoup.connect(url).get();
			cacheDocument(document);
			return document;
		}
	}
	
	private Document getDocumentFromCache() throws IOException {
		String html = FileUtils.readFileToString(getCacheFile());
		return Jsoup.parse(html);
	}

	public String getCacheName() {
		if (this.cacheName == null) {
			this.cacheName = CrawlerUtil.getCachePath(
					".movie_rating", "page_" + this.url.toString().replaceAll("[ \\/\\.\\(\\):&=?]", "_") + ".html");
		}
		return this.cacheName;
	}
	
	public File getCacheFile() {
		return new File(this.getCacheName());
	}

	private void cacheDocument(Document document) throws IOException {
		FileUtils.writeStringToFile(getCacheFile(), document.html());
	}

	private boolean isCached() {
		return getCacheFile().exists();
	}
	
}
