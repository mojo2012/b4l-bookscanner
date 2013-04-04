package at.spot.b4lbookscanner.google.service.book;

import java.io.Serializable;

public class SearchInfo implements Serializable {
	private static final long	serialVersionUID	= -4213566408590130608L;
	private String				textSnippet;

	public String getTextSnippet() {
		return this.textSnippet;
	}

	public void setTextSnippet(String textSnippet) {
		this.textSnippet = textSnippet;
	}
}
